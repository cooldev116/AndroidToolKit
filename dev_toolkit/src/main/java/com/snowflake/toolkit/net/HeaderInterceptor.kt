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
 * @author Melon
 * @date 2026/8/9
 */
class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val hasToken = TokenManager.hasToken()
        val builder = original.newBuilder()
            .header(NetHeaderKeys.PACKAGE_NAME, AppInfoUtil.getPackageName())
            .header(NetHeaderKeys.VERSION, AppInfoUtil.getVersionName())
            .header(NetHeaderKeys.VERSION_CODE, AppInfoUtil.getVersionCode().toString())
            .header(NetHeaderKeys.CHANNEL, AppInfoUtil.getChannel())
            .header(NetHeaderKeys.UDID, DeviceUdidUtil.getMd5Udid())
            .header(NetHeaderKeys.PLATFORM, NetHeaderKeys.PLATFORM_ANDROID)
            .method(original.method, original.body)

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
    }
}
