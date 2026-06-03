package com.adwant.kit

import com.bytedance.sdk.openadsdk.TTCustomController
import com.bytedance.sdk.openadsdk.mediation.init.IMediationPrivacyConfig
import com.bytedance.sdk.openadsdk.mediation.init.MediationPrivacyConfig

/**
 * @description:
 * @author:Melon
 * @date:2026/3/15
 */
open class DefaultCustomController : TTCustomController() {
    override fun isCanUseWifiState(): Boolean {
        return super.isCanUseWifiState()
    }

    override fun getMacAddress(): String? {
        return super.getMacAddress()
    }

    override fun isCanUseWriteExternal(): Boolean {
        return super.isCanUseWriteExternal()
    }

    override fun getDevOaid(): String? {
        return super.getDevOaid()
    }

    override fun isCanUseAndroidId(): Boolean {
        return super.isCanUseAndroidId()
    }

    override fun getAndroidId(): String? {
        return super.getAndroidId()
    }

    override fun getMediationPrivacyConfig(): IMediationPrivacyConfig? {
        return object: MediationPrivacyConfig(){
            override fun isLimitPersonalAds(): Boolean {
                return super.isLimitPersonalAds()
            }

            override fun isProgrammaticRecommend(): Boolean {
                return super.isProgrammaticRecommend()
            }
        }
    }
}