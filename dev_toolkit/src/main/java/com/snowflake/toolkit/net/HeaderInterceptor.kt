package com.snowflake.toolkit.net

import com.snowflake.toolkit.device.DeviceUdidUtil
import com.snowflake.toolkit.utils.AppInfoUtil
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

/**
 * 为每个请求附加宿主包名、版本、渠道、设备号、平台等公共头。
 * 若 [TokenManager] 中有 token，再附加 `Authorization: Bearer xxx`。
 * udid / token 每次现取，登录或 OAID 就绪后后续请求即可生效，无需重建 OkHttp。
 *
 * 部分接口（如隐私同意前可访问的 legal）不能带 udid，见 [NO_UDID_PATHS]。
 *
 * @author Melon
 * @date 2026/8/9
 */
class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val hasToken = TokenManager.hasToken()
        val path = original.url.encodedPath
        val builder = original.newBuilder()
            .header(NetHeaderKeys.PACKAGE_NAME, AppInfoUtil.getPackageName())
            .header(NetHeaderKeys.VERSION_NAME, AppInfoUtil.getVersionName())
            .header(NetHeaderKeys.VERSION_CODE, AppInfoUtil.getVersionCode().toString())
            .header(NetHeaderKeys.CHANNEL, AppInfoUtil.getChannel())
            .header(NetHeaderKeys.PLATFORM, NetHeaderKeys.PLATFORM_ANDROID)
            .method(original.method, original.body)

        // 隐私同意前可访问的接口不传 udid，避免未授权采集设备标识
        if (path !in NO_UDID_PATHS) {
            builder.header(NetHeaderKeys.UDID, DeviceUdidUtil.getMd5Udid())
        }

        // 未登录不加 Authorization，避免出现空的 Bearer
        TokenManager.getAuthorizationHeader()?.let { authorization ->
            builder.header(NetHeaderKeys.AUTHORIZATION, authorization)
        }

        val request = builder.build()
        Timber.tag(TAG).d(
            "→ %s %s hasAuth=%s channel=%s",
            request.method,
            request.url,
            hasToken,
            AppInfoUtil.getChannel()
        )

        val startNs = System.nanoTime()
        return try {
            val response = chain.proceed(request)
            val costMs = (System.nanoTime() - startNs) / 1_000_000
            Timber.tag(TAG).d(
                "← %s %s code=%d cost=%dms",
                request.method,
                request.url,
                response.code,
                costMs
            )
            response
        } catch (e: Exception) {
            val costMs = (System.nanoTime() - startNs) / 1_000_000
            Timber.tag(TAG).e(
                e,
                "← %s %s failed cost=%dms",
                request.method,
                request.url,
                costMs
            )
            throw e
        }
    }

    companion object {
        private const val TAG = "NetHeader"

        /**
         * 不附加 udid 的接口 path（与 [ToolkitApi] 等声明保持一致）。
         * 后续若有同类「同意前可请求」接口，直接往集合追加即可。
         */
        private val NO_UDID_PATHS = setOf(
            ToolkitPaths.LEGAL,
        )
    }
}
