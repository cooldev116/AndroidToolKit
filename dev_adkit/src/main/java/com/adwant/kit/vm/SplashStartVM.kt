package com.adwant.kit.vm

import com.adwant.kit.bean.AdConfigBean
import com.adwant.kit.net.AdKitService
import com.snowflake.toolkit.base.BaseViewModel
import com.snowflake.toolkit.ext.launch
import com.snowflake.toolkit.ext.liveDataResult
import com.snowflake.toolkit.ext.postError
import com.snowflake.toolkit.ext.postFailure
import com.snowflake.toolkit.ext.postSuccess
import com.snowflake.toolkit.net.NetRepository

/**
 * 冷启动开屏用 ViewModel：拉取广告配置，供 [com.adwant.kit.ui.SplashStartAdActivity] 初始化 SDK。
 */
class SplashStartVM : BaseViewModel() {

    val adConfigResult = liveDataResult<AdConfigBean>()

    /**
     * 请求 [AdKitService] 广告配置。
     * 业务成功且 [AdConfigBean.networkAppId] 非空才 [postSuccess]；否则走 failure / error。
     */
    fun fetchAdConfig() {
        launch(onError = { adConfigResult.postError(it) }) {
            val api = NetRepository.instance.buildApi(AdKitService::class.java)
            val response = api.getAdConfig()
            if (!response.isSuccess()) {
                adConfigResult.postFailure(response.message, response.code)
                return@launch
            }
            val data = response.data
            val networkAppId = data?.networkAppId?.takeIf { it.isNotBlank() }
            if (data == null || networkAppId == null) {
                adConfigResult.postFailure("networkAppId empty")
                return@launch
            }
            adConfigResult.postSuccess(data)
        }
    }
}
