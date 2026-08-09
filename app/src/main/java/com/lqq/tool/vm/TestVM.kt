package com.lqq.tool.vm

import com.lqq.tool.bean.BannerBean
import com.lqq.tool.net.ApiService
import com.snowflake.toolkit.base.BaseViewModel
import com.snowflake.toolkit.ext.launch
import com.snowflake.toolkit.ext.liveDataResult
import com.snowflake.toolkit.ext.postError
import com.snowflake.toolkit.ext.postFailure
import com.snowflake.toolkit.ext.postSuccess
import com.snowflake.toolkit.net.NetRepository

/**
 * Demo 用 ViewModel：验证 NetRepository + 玩 Android Banner 接口。
 *
 * @author Melon
 * @date 2025/12/3
 */
class TestVM : BaseViewModel() {

    val bannerResult = liveDataResult<List<BannerBean>>()

    /**
     * 通过清单配置的 API_URL 构建 [ApiService]，请求 banner/json。
     * errorCode!=0 走业务失败；网络/解析异常走 [postError]。
     */
    fun fetchBanner() {
        launch(onError = { bannerResult.postError(it) }) {
            loadingStatus.postValue(true)
            try {
                val api = NetRepository.instance.buildApi(ApiService::class.java)
                val response = api.getBanner()
                if (response.isSuccess()) {
                    bannerResult.postSuccess(response.data.orEmpty())
                } else {
                    bannerResult.postFailure(response.errorMsg, response.errorCode)
                }
            } finally {
                loadingStatus.postValue(false)
            }
        }
    }
}
