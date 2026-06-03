package com.snowflake.toolkit.ui.dialog

import androidx.lifecycle.lifecycleScope
import com.snowflake.toolkit.base.BaseDialogFragment
import com.snowflake.toolkit.databinding.ToolkitDialogLoadingBinding

/**
 * @description:加载框
 * @author:Melon
 * @date:2025/6/30
 */
class LoadingDialog : BaseDialogFragment<ToolkitDialogLoadingBinding>() {
    fun setMsg(msg: String) {
        lifecycleScope.launchWhenResumed {
            binding.tvMsg.text = msg
        }
    }
}