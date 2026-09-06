package com.adwant.kit.ui

import android.os.Handler
import android.os.Looper
import android.os.Process
import androidx.lifecycle.ViewModelProvider
import com.adwant.kit.AdKit
import com.adwant.kit.BuildConfig
import com.adwant.kit.constant.KEY_AGREE_PRIVACY_POLICY
import com.adwant.kit.vm.SplashStartVM
import com.adwant.kit.bean.AdConfigBean
import com.snowflake.toolkit.ext.toast
import com.snowflake.toolkit.utils.MMKVUtil
import kotlin.system.exitProcess

/**
 * @description:冷启动开屏页面封装基类
 * @author:Melon
 * @date:2026/4/27
 */
abstract class SplashStartAdActivity : BaseSplashAdActivity() {

    private val mainHandler = Handler(Looper.getMainLooper())

    private val viewModel: SplashStartVM by lazy(mode = LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[SplashStartVM::class.java]
    }

    override fun initView() {
        super.initView()
        val isAgree = MMKVUtil.getBoolean(KEY_AGREE_PRIVACY_POLICY, false)
        if (isAgree) {
            startFakeProgress()
            initSDK()
        } else {
            showPrivacyDialog {
                MMKVUtil.save(KEY_AGREE_PRIVACY_POLICY, true)
                startFakeProgress()
                initSDK()
            }
        }
    }

    override fun subscribeData() {
        super.subscribeData()
        // 广告配置成功后再 init SDK；接口失败则退出
        viewModel.adConfigResult.observe(this) { result ->
            result.onSuccess { config ->
                initAdKit(config)
            }.onFailure {
                failAndExitApp()
            }
        }
    }

    /** 触发 ViewModel 拉取广告配置（appId 来自接口，不再由宿主硬编码）。 */
    private fun initSDK() {
        viewModel.fetchAdConfig()
    }

    /**
     * 使用接口返回的 networkAppId 初始化穿山甲；
     * SDK 失败同样走 [failAndExitApp]。
     */
    private fun initAdKit(config: AdConfigBean) {
        AdKit.instance.init(
            applicationContext,
            config.networkAppId,
            isDebug = BuildConfig.DEBUG
        ) { isSuccess, _ ->
            // 穿山甲 init 回调可能在子线程，UI / Lifecycle 相关操作切回主线程
            mainHandler.post {
                if (isSuccess) {
                    AdKit.instance.setAllowShowAd(config.master)
                    enableBackendSplashIfNeeded()
                    onInitSDKSuccess()
                    startShowSplash()
                } else {
                    failAndExitApp()
                }
            }
        }
    }

    /**
     * 初始化链路失败：先 toast，再结束任务栈并杀掉进程（稍延迟以便提示可见）。
     */
    private fun failAndExitApp() {
        toast("初始化失败")
        mainHandler.postDelayed({
            finishAffinity()
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }, EXIT_DELAY_MS)
    }

    private fun enableBackendSplashIfNeeded() {
        val splashClass = getBackendSplashActivityClass() ?: return
        AdKit.instance.enableBackendSplash(application, splashClass)
    }

    /**
     * 后台开屏 Activity（[SplashBackendAdActivity] 实现类）。
     * 返回非 null 时，SDK 初始化成功后会自动启用后台开屏监听。
     */
    protected open fun getBackendSplashActivityClass(): Class<out SplashBackendAdActivity>? = null

    /**
     * 展示隐私协议弹框
     */
    protected abstract fun showPrivacyDialog(next: () -> Unit)

    /**
     * 初始化sdk成功
     */
    protected open fun onInitSDKSuccess() {}

    companion object {
        private const val EXIT_DELAY_MS = 800L
    }
}
