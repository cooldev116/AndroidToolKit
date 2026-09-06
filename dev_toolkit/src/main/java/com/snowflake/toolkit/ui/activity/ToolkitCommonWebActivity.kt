package com.snowflake.toolkit.ui.activity

import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import com.just.agentweb.AgentWeb
import com.snowflake.toolkit.base.BaseVBActivity
import com.snowflake.toolkit.constant.EXTRA_KEY_TITLE
import com.snowflake.toolkit.constant.EXTRA_KEY_URL
import com.snowflake.toolkit.databinding.ToolkitActivityCommonWebBinding
import com.snowflake.toolkit.ext.extBundle
import com.snowflake.toolkit.ext.openActivity

/**
 * SDK 通用 H5 容器：隐私政策、用户协议及后续反馈 / 客服等 Web 页统一走此页。
 * 推荐通过 [open] 或 [com.snowflake.toolkit.helper.PageJumpHelper] 打开。
 */
class ToolkitCommonWebActivity : BaseVBActivity<ToolkitActivityCommonWebBinding>() {

    private var agentWeb: AgentWeb? = null
    private val title by lazy {
        extBundle()?.getString(EXTRA_KEY_TITLE)
    }

    private val url by lazy {
        extBundle()?.getString(EXTRA_KEY_URL)
    }

    override fun initView() {
        super.initView()
        binding.appBar.setTitle(title)
        initWeb()
        initWebSetting()
    }

    override fun initListener() {
        super.initListener()

        binding.appBar.setOnNavigationListener { finish() }
    }

    private fun initWeb() {
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(binding.flWeb, FrameLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
            .go(url)
    }

    private fun initWebSetting() {
    }

    override fun onResume() {
        super.onResume()
        agentWeb?.webLifeCycle?.onResume()
    }

    override fun onPause() {
        super.onPause()
        agentWeb?.webLifeCycle?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        agentWeb?.webLifeCycle?.onDestroy()
    }

    companion object {
        /** 打开通用 Web 页，title / url 经 Bundle 传入。 */
        fun open(context: Context, title: String, url: String) {
            context.openActivity(
                ToolkitCommonWebActivity::class.java,
                Bundle().apply {
                    putString(EXTRA_KEY_TITLE, title)
                    putString(EXTRA_KEY_URL, url)
                }
            )
        }
    }
}
