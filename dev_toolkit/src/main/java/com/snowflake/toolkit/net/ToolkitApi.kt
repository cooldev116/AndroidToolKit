package com.snowflake.toolkit.net

import com.snowflake.toolkit.bean.FeedbackBean
import com.snowflake.toolkit.bean.FeedbackRequest
import com.snowflake.toolkit.bean.LegalBean
import retrofit2.http.Body
import retrofit2.http.POST

interface ToolkitApi {
    /**
     * 获取隐私政策和用户协议。
     * 该接口 path 在 [HeaderInterceptor] 中不传 udid，可在隐私弹框同意前调用。
     */
    @POST(ToolkitPaths.LEGAL)
    suspend fun getLegal(): ApiResponse<LegalBean>

    /**
     * 提交意见反馈。
     * [FeedbackRequest.content] 必填；contact / screenshots 选填。
     */
    @POST(ToolkitPaths.FEEDBACK)
    suspend fun submitFeedback(@Body request: FeedbackRequest): ApiResponse<FeedbackBean>
}
