package com.snowflake.toolkit.vm

import com.snowflake.toolkit.base.BaseViewModel
import com.snowflake.toolkit.bean.FeedbackBean
import com.snowflake.toolkit.bean.FeedbackRequest
import com.snowflake.toolkit.ext.launch
import com.snowflake.toolkit.ext.liveDataResult
import com.snowflake.toolkit.ext.postError
import com.snowflake.toolkit.ext.postFailure
import com.snowflake.toolkit.ext.postSuccess
import com.snowflake.toolkit.net.NetRepository
import com.snowflake.toolkit.net.ToolkitApi
import com.snowflake.toolkit.utils.CheckUtil

/**
 * 意见反馈页 ViewModel：校验入参并提交 [ToolkitApi.submitFeedback]。
 */
class FeedbackVM : BaseViewModel() {

    val submitResult = liveDataResult<FeedbackBean>()

    /**
     * 提交反馈。
     * content 必填；contact 选填但非空时须通过 [CheckUtil.isValidContact]；
     * screenshots 暂不从 UI 采集，预留参数便于后续接上传。
     *
     * 校验失败直接 [postFailure]（不弹 loading）；网络/业务结果走 success / failure / error。
     */
    fun submitFeedback(
        content: String,
        contact: String? = null,
        screenshots: List<String>? = null,
    ) {
        val trimmedContent = content.trim()
        if (trimmedContent.isEmpty()) {
            submitResult.postFailure("请输入反馈内容")
            return
        }
        val trimmedContact = contact?.trim()?.takeIf { it.isNotEmpty() }
        if (!CheckUtil.isValidContact(trimmedContact)) {
            submitResult.postFailure("请输入正确的手机号或邮箱")
            return
        }

        launch(onError = { submitResult.postError(it) }) {
            loadingStatus.postValue(true)
            try {
                val api = NetRepository.instance.buildApi(ToolkitApi::class.java)
                val response = api.submitFeedback(
                    FeedbackRequest(
                        content = trimmedContent,
                        contact = trimmedContact,
                        screenshots = screenshots?.takeIf { it.isNotEmpty() },
                    )
                )
                if (response.isSuccess()) {
                    // data 为空时仍视为成功（接口已返回 code=0），用占位 id 驱动 UI 收尾
                    submitResult.postSuccess(response.data ?: FeedbackBean(id = 0))
                } else {
                    submitResult.postFailure(response.message, response.code)
                }
            } finally {
                loadingStatus.postValue(false)
            }
        }
    }
}
