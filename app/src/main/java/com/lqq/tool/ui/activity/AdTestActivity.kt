package com.lqq.tool.ui.activity

import com.adwant.kit.AdKit
import com.adwant.kit.ext.showBannerAd
import com.adwant.kit.ext.showNativeAd
import com.lqq.tool.databinding.ActivityAdTestBinding
import com.snowflake.toolkit.base.BaseVBActivity
import com.snowflake.toolkit.ext.click
import com.snowflake.toolkit.ext.dp2Px

class AdTestActivity : BaseVBActivity<ActivityAdTestBinding>() {

    override fun initView() {
        super.initView()

        showBannerAd("104008284", binding.flBanner)

        showNativeAd("104008543", binding.flNative, height = 200f.dp2Px())
    }

    override fun initListener() {
        super.initListener()

        binding.apply {
            btnSplash.click {
                AdKit.instance.showSplashAd(this@AdTestActivity, "103928850")
            }

            btnInterstitial.click {
                AdKit.instance.showInterstitialAd(this@AdTestActivity, "104008639")
            }

            btnReward.click {
                AdKit.instance.showRewardVideo(this@AdTestActivity, "104008544")
            }
        }
    }
}