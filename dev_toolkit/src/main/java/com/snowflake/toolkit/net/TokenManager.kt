package com.snowflake.toolkit.net

import com.snowflake.toolkit.utils.MMKVUtil
import timber.log.Timber

/**
 * Token 本地存取封装（MMKV）。
 *
 * 登录成功后调用 [saveToken]；请求头由 [HeaderInterceptor] 自动拼为
 * `Authorization: Bearer <token>`。退出登录请 [clearToken]。
 *
 * ```kotlin
 * TokenManager.saveToken(jwt)
 * val token = TokenManager.getToken()
 * TokenManager.clearToken()
 * ```
 *
 * @author Melon
 * @date 2026/8/9
 */
object TokenManager {

    /** MMKV 存储 key，避免与业务其它字段冲突 */
    private const val KEY_ACCESS_TOKEN = "toolkit_access_token"
    private const val TAG = "TokenManager"

    /**
     * 持久化 access token。
     * 传入空白字符串等同于清除，避免把头写成 `Bearer `。
     */
    @JvmStatic
    fun saveToken(token: String?): Boolean {
        val value = token?.trim().orEmpty()
        if (value.isEmpty()) {
            clearToken()
            return true
        }
        val ok = MMKVUtil.save(KEY_ACCESS_TOKEN, value)
        // 只打脱敏摘要，避免完整 JWT 进日志
        Timber.tag(TAG).d("saveToken ok=%s mask=%s", ok, maskToken(value))
        return ok
    }

    /**
     * 读取已保存的裸 token（不含 Bearer 前缀）。
     * 未登录或已清除时返回 null。
     */
    @JvmStatic
    fun getToken(): String? {
        return MMKVUtil.getString(KEY_ACCESS_TOKEN, null)?.trim()?.takeIf { it.isNotEmpty() }
    }

    /** 是否已有可用 token */
    @JvmStatic
    fun hasToken(): Boolean = !getToken().isNullOrEmpty()

    /**
     * 组装完整 Authorization 头值：`Bearer xxx`。
     * 无 token 时返回 null，拦截器据此决定是否加头。
     */
    @JvmStatic
    fun getAuthorizationHeader(): String? {
        val token = getToken() ?: return null
        return "${NetHeaderKeys.BEARER_PREFIX}$token"
    }

    /** 退出登录或 token 失效时清除本地凭证 */
    @JvmStatic
    fun clearToken() {
        MMKVUtil.remove(KEY_ACCESS_TOKEN)
        Timber.tag(TAG).d("clearToken")
    }

    /** 日志用：保留首尾少量字符，中间打码 */
    private fun maskToken(token: String): String {
        if (token.length <= 12) return "***"
        return token.take(6) + "..." + token.takeLast(4)
    }
}
