package com.snowflake.toolkit.ui.activity

import android.content.Context
import com.snowflake.toolkit.R
import com.snowflake.toolkit.base.BaseVBActivity
import com.snowflake.toolkit.databinding.ToolkitActivityAboutBinding
import com.snowflake.toolkit.ext.openActivity
import com.snowflake.toolkit.utils.AppInfoUtil

/**
 * 关于我们页：展示应用图标、应用名称与版本号。
 * 推荐通过 [open] 或 [com.snowflake.toolkit.helper.PageJumpHelper.openAbout] 打开。
 */
class ToolkitAboutActivity : BaseVBActivity<ToolkitActivityAboutBinding>() {

    override fun initView() {
        super.initView()
        binding.appBar.setTitle(getString(R.string.toolkit_about_title))
        bindAppInfo()
    }

    override fun initListener() {
        super.initListener()
        binding.appBar.setOnNavigationListener { finish() }
    }

    /**
     * 从宿主包信息填充图标、名称与版本（格式如 v1.0.0）。
     */
    private fun bindAppInfo() {
        binding.ivAppIcon.setImageDrawable(packageManager.getApplicationIcon(applicationInfo))
        binding.tvAppName.text = AppInfoUtil.getAppName()
        val versionName = AppInfoUtil.getVersionName().ifBlank { "1.0.0" }
        binding.tvVersion.text = getString(R.string.toolkit_about_version_format, versionName)
    }

    companion object {
        /** 打开关于我们页。 */
        fun open(context: Context) {
            context.openActivity(ToolkitAboutActivity::class.java)
        }
    }
}
