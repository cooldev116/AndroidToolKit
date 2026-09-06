package com.snowflake.toolkit.ui.activity

import android.content.Context
import com.snowflake.toolkit.R
import com.snowflake.toolkit.base.BaseInputVMActivity
import com.snowflake.toolkit.databinding.ToolkitActivityFeedbackBinding
import com.snowflake.toolkit.ext.click
import com.snowflake.toolkit.ext.openActivity
import com.snowflake.toolkit.ext.toast
import com.snowflake.toolkit.vm.FeedbackVM

/**
 * 意见反馈页：填写内容与可选联系方式后提交。
 * 截图上传暂未接入，不展示选图入口；推荐通过 [open] 或 [com.snowflake.toolkit.helper.PageJumpHelper.openFeedback] 打开。
 */
class ToolkitFeedbackActivity : BaseInputVMActivity<ToolkitActivityFeedbackBinding, FeedbackVM>() {

    override fun initView() {
        super.initView()
        binding.appBar.setTitle(getString(R.string.toolkit_feedback_title))
    }

    override fun initListener() {
        super.initListener()
        binding.appBar.setOnNavigationListener { finish() }
        binding.btnSubmit.click {
            viewModel.submitFeedback(
                content = binding.etContent.text?.toString().orEmpty(),
                contact = binding.etContact.text?.toString(),
            )
        }
    }

    override fun subscribeData() {
        super.subscribeData()
        viewModel.loadingStatus.observe(this) { show ->
            if (show == true) showLoading("提交中...") else dismissLoading()
        }
        // 成功：吐司后关闭页；失败：仅提示原因
        viewModel.submitResult.observe(this) { result ->
            result.onSuccess {
                "提交成功".toast()
                finish()
            }.onFailure { error ->
                (error.message?.takeIf { it.isNotBlank() } ?: "提交失败，请稍后重试").toast()
            }
        }
    }

    companion object {
        /** 打开意见反馈页。 */
        fun open(context: Context) {
            context.openActivity(ToolkitFeedbackActivity::class.java)
        }
    }
}
