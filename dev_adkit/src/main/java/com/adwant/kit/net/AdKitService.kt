package com.adwant.kit.net

import com.adwant.kit.bean.AdConfigBean
import com.snowflake.toolkit.net.ApiResponse
import retrofit2.http.POST

/**
 * AdKit 网络接口：广告配置等与广告 SDK 相关的后端能力。
 */
interface AdKitService {

    /**
     * 拉取广告配置，无请求体。
     * 成功后用 [AdConfigBean.networkAppId] 初始化广告 SDK。
     */
    @POST(AdKitService.AD_CONFIG)
    suspend fun getAdConfig(): ApiResponse<AdConfigBean>

    companion object {
        const val AD_CONFIG = "/api/v1/ad/config"
    }
}
