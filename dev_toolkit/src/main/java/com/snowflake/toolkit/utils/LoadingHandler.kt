package com.snowflake.toolkit.utils

import androidx.fragment.app.FragmentActivity
import com.snowflake.toolkit.ext.showExt
import com.snowflake.toolkit.ui.dialog.LoadingDialog

/**
 * @description:加载框代理类
 * @author:Melon
 * @date:2025/12/3
 */
class LoadingHandler {
    private var loadingDialog: LoadingDialog? = null

    fun showLoading(activity: FragmentActivity, msg: String = "加载中") {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog().apply { setMsg(msg) }
        }
        loadingDialog?.showExt(activity)
    }

    fun dismissLoading() {
        loadingDialog?.dismissAllowingStateLoss()
        loadingDialog = null
    }
}