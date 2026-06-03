package com.snowflake.toolkit.permission

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.snowflake.toolkit.ext.toast

/**
 * @description 是否授予了权限
 * @author Melon
 * @time 2025/7/2 16:02
 */
fun Context.isPermision(permissions: Array<String>): Boolean {
    return XXPermissions.isGranted(this, permissions)
}

/**
 * @description 是否授予了权限
 * @author Melon
 * @time 2025/7/2 16:02
 */
fun FragmentActivity.isPermission(permissions: Array<String>): Boolean {
    return XXPermissions.isGranted(this, permissions)
}

/**
 * @description 是否授权了该权限
 * @author Melon
 * @time 2025/7/2 16:01
 */
fun Fragment.isPermission(permissions: Array<String>): Boolean {
    return XXPermissions.isGranted(requireContext(), permissions)
}

/**
 * @description 申请权限
 * @param permissions 需要申请的权限数组
 * @param permissionDesc 权限描述
 * @param block 授予权限的回调
 * @author Melon
 * @time 2025/6/4 10:25
 */
fun Fragment.requestPermission(
    permissions: Array<String>,
    permissionDesc: String? = null,
    isShowToast: Boolean = true,
    block: ((Boolean) -> Unit)? = null
) {
    if (XXPermissions.isGranted(requireContext(), permissions)) {
        block?.invoke(true)
    } else {
        realRequestPermission(requireContext(), permissions, permissionDesc, isShowToast, block)
    }
}

/**
 * @description 申请权限
 * @param permissions 需要申请的权限数组
 * @param permissionDesc 权限描述
 * @param block 授予权限的回调
 * @author Melon
 * @time 2025/6/4 10:25
 */
fun FragmentActivity.requestPermission(
    permissions: Array<String>,
    permissionDesc: String? = null,
    isShowToast: Boolean = true,
    block: ((Boolean) -> Unit)? = null
) {
    if (XXPermissions.isGranted(this, permissions)) {
        block?.invoke(true)
    } else {
        realRequestPermission(this, permissions, permissionDesc, isShowToast, block)
    }
}

/**
 * @description 真正去申请权限
 * @param permissions 需要申请的权限
 * @param permissionDesc 权限说明
 * @param isShowToast 是否使用默认的吐司提示
 * @author Melon
 * @time 2025/5/30 16:28
 */
private fun realRequestPermission(
    context: Context,
    permissions: Array<String>,
    permissionDesc: String? = null,
    isShowToast: Boolean = true,
    block: ((Boolean) -> Unit)? = null
) {
    XXPermissions.with(context)
        .permission(permissions)
        .interceptor(PermissionInterceptor(permissionDesc))
        .request(object : OnPermissionCallback {
            override fun onGranted(p0: MutableList<String>, p1: Boolean) {
                if (!p1) {
                    if (isShowToast) {
                        context.toast("部分权限未正常授予")
                    }
                    block?.invoke(false)
                    return
                }
                block?.invoke(true)
            }

            override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                super.onDenied(permissions, doNotAskAgain)
                if (doNotAskAgain) {
                    if (isShowToast) {
                        context.toast("请手动打开设置页面，授予权限")
                    }
                }
                block?.invoke(false)
            }
        })
}