package com.snowflake.toolkit.net

import android.content.pm.ApplicationInfo
import com.snowflake.toolkit.manger.ToolKitManager
import com.snowflake.toolkit.utils.AppInfoUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Retrofit 网络仓库：按 baseUrl 缓存 ApiService。
 *
 * 宿主需在 Application 清单配置：
 * ```xml
 * <meta-data
 *     android:name="API_URL"
 *     android:value="${API_URL}" />
 * ```
 * 并在 Gradle 中通过 `manifestPlaceholders.API_URL` 注入真实域名。
 *
 * 调用示例：
 * ```kotlin
 * val api = NetRepository.instance.buildApi(ApiService::class.java)
 * val other = NetRepository.instance.buildUrlApi("https://other.example.com/", ApiService::class.java)
 * ```
 *
 * 缓存结构：外层 key 为规范化后的 baseUrl，内层 key 为 ApiService 的 Class，value 为 create 出的代理实例。
 *
 * @author Melon
 * @date 2026/8/9
 */
class NetRepository private constructor() {

    companion object {
        /** 宿主清单中 API 根地址 meta-data 的 name */
        const val META_API_URL = "API_URL"

        private const val DEFAULT_TIMEOUT_SECONDS = 15L
        private const val TAG = "NetRepository"

        val instance by lazy { NetRepository() }
    }

    /** 全库共享 OkHttp，避免重复建连接池 */
    private val okHttpClient: OkHttpClient by lazy { createOkHttpClient() }

    /** baseUrl -> Retrofit，同一域名复用 Converter / CallAdapter */
    private val retrofitCache = ConcurrentHashMap<String, Retrofit>()

    /**
     * baseUrl -> (ApiService Class -> 实例)。
     * 同一 url 下可挂多个不同接口 Class，且线程安全地惰性创建。
     */
    private val apiCache = ConcurrentHashMap<String, ConcurrentHashMap<Class<*>, Any>>()

    /**
     * 使用清单 [META_API_URL] 作为 baseUrl，创建或复用 [service] 对应的 ApiService。
     *
     * @throws IllegalStateException 未配置或值为空时抛出，提示宿主补齐 meta-data
     */
    fun <T> buildApi(service: Class<T>): T {
        val baseUrl = requireApiUrlFromMeta()
        Timber.tag(TAG).d("buildApi service=%s metaBaseUrl=%s", service.simpleName, baseUrl)
        return buildUrlApi(baseUrl, service)
    }

    /**
     * 使用调用方传入的 [url] 作为 baseUrl，创建或复用 [service] 对应的 ApiService。
     * 相同 url + Class 只会 create 一次，后续直接从缓存返回。
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> buildUrlApi(url: String, service: Class<T>): T {
        require(url.isNotBlank()) { "baseUrl 不能为空" }
        val baseUrl = normalizeBaseUrl(url)
        val classMap = apiCache.getOrPut(baseUrl) { ConcurrentHashMap() }
        (classMap[service] as? T)?.let {
            Timber.tag(TAG).d(
                "buildUrlApi cacheHit url=%s service=%s",
                baseUrl,
                service.simpleName
            )
            return it
        }
        // 双重检查：避免并发下对同一 Class 重复 retrofit.create
        synchronized(classMap) {
            (classMap[service] as? T)?.let {
                Timber.tag(TAG).d(
                    "buildUrlApi cacheHit(sync) url=%s service=%s",
                    baseUrl,
                    service.simpleName
                )
                return it
            }
            val retrofitExists = retrofitCache.containsKey(baseUrl)
            val retrofit = retrofitCache.getOrPut(baseUrl) { createRetrofit(baseUrl) }
            if (!retrofitExists) {
                Timber.tag(TAG).d("create Retrofit baseUrl=%s", baseUrl)
            }
            val api = retrofit.create(service)
            classMap[service] = api as Any
            Timber.tag(TAG).d(
                "buildUrlApi cacheMiss create service=%s url=%s",
                service.simpleName,
                baseUrl
            )
            return api
        }
    }

    /**
     * 读取宿主 Application meta-data 中的 [META_API_URL]。
     * 供外部排查配置时主动调用；[buildApi] 内部也会走同一逻辑。
     */
    fun getApiUrlFromMeta(): String? {
        return AppInfoUtil.getMetaDataString(META_API_URL)?.let { normalizeBaseUrl(it) }
    }

    /** 清空 Retrofit / ApiService 缓存（一般仅测试或切换环境时需要） */
    fun clearCache() {
        Timber.tag(TAG).d(
            "clearCache apiEntries=%d retrofitEntries=%d",
            apiCache.size,
            retrofitCache.size
        )
        apiCache.clear()
        retrofitCache.clear()
    }

    private fun requireApiUrlFromMeta(): String {
        val url = AppInfoUtil.getMetaDataString(META_API_URL)
        check(!url.isNullOrBlank()) {
            Timber.tag(TAG).e("meta-data %s missing or empty", META_API_URL)
            "未在清单中配置 meta-data android:name=\"$META_API_URL\"，" +
                    "请在宿主 Application 节点添加并在 Gradle manifestPlaceholders 注入"
        }
        return url
    }

    /**
     * Retrofit 要求 baseUrl 以 `/` 结尾；这里统一补齐，保证缓存 key 一致。
     */
    private fun normalizeBaseUrl(url: String): String {
        val trimmed = url.trim()
        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 公共头 +（仅 debug 包）Body 级日志；HTTP 明细经 Timber 输出，便于统一过滤。
     */
    private fun createOkHttpClient(): OkHttpClient {
        Timber.tag(TAG).d(
            "createOkHttpClient timeout=%ds debuggable=%s",
            DEFAULT_TIMEOUT_SECONDS,
            isDebuggable()
        )
        val builder = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(HeaderInterceptor())

        if (isDebuggable()) {
            val logger = object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    // 转义 %，避免响应体中的百分号被 Timber 当成格式符
                    Timber.tag("OkHttp").d(message.replace("%", "%%"))
                }
            }
            val logging = HttpLoggingInterceptor(logger).apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }
        return builder.build()
    }

    private fun isDebuggable(): Boolean {
        return try {
            val info = ToolKitManager.instance.getContext().applicationInfo
            (info.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (_: Exception) {
            false
        }
    }
}
