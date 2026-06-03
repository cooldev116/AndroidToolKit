package com.snowflake.toolkit.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.snowflake.toolkit.ext.getMultiViewBinding
import com.snowflake.toolkit.inter.ILoading
import com.snowflake.toolkit.utils.LoadingHandler
import com.gyf.immersionbar.ImmersionBar

/**
 * @description:多重继承ViewBinding
 * @author:Melon
 * @date:2025/7/31
 */
abstract class BaseVBMultiActivity<VB : ViewBinding> : AppCompatActivity(), ILoading {
    private var _binding: VB? = null

    protected open val binding get() = _binding!!

    private val loadingHandler by lazy {
        LoadingHandler()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        _binding = getMultiViewBinding(layoutInflater)
        setContentView(_binding?.root)
        if (isImmersion()) {
            ImmersionBar.with(this).statusBarDarkFont(true).init()
        } else {
            ImmersionBar.with(this).statusBarDarkFont(true).statusBarColor(statusBarColor())
                .fitsSystemWindows(true).init()
        }
        initView()
        initListener()
        subscribeData()
        initData()
    }

    protected open fun initView() {}

    protected open fun initListener() {}

    protected open fun initData() {}

    /**
     * @description ViewModel的数据回调
     * @author Melon
     * @time 2025/6/9 17:50
     */
    protected open fun subscribeData() {}

    protected open fun isImmersion(): Boolean {
        return true
    }

    protected open fun statusBarColor(): Int {
        return android.R.color.white
    }

    override fun showLoading(msg: String) {
        loadingHandler.showLoading(this, msg)
    }

    override fun dismissLoading() {
        loadingHandler.dismissLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}