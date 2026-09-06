package com.lqq.tool.ui.activity

import com.lqq.tool.databinding.ActivityPageHelperBinding
import com.snowflake.toolkit.base.BaseVBActivity
import com.snowflake.toolkit.ext.click
import com.snowflake.toolkit.helper.PageJumpHelper

class PageHelperActivity : BaseVBActivity<ActivityPageHelperBinding>() {
    override fun initListener() {
        super.initListener()

        binding.apply {
            btnPrivacy.click {
                PageJumpHelper.openPrivacy(applicationContext)
            }

            btnUserAgreement.click {
                PageJumpHelper.openAgreement(applicationContext)
            }

            btnFeedback.click {
                PageJumpHelper.openFeedback(this@PageHelperActivity)
            }

            btnAbout.click {
                PageJumpHelper.openAbout(this@PageHelperActivity)
            }
        }
    }
}