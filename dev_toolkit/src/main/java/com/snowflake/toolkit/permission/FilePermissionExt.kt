package com.snowflake.toolkit.permission

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

/**
 * @description:文件权限扩展函数
 * @author:Melon
 * @date:2026/2/5
 */
/**
 * 读文件权限组
 */
private val filePermissions = arrayOf(
    Permission.READ_MEDIA_IMAGES, Permission.READ_MEDIA_VIDEO,
    Permission.READ_MEDIA_AUDIO, Permission.READ_MEDIA_VISUAL_USER_SELECTED
)

/**
 * 是否有读文件权限
 */
fun FragmentActivity.isFilePermission(permissions: Array<String> = filePermissions): Boolean {
    return XXPermissions.isGranted(applicationContext, permissions)
}

/**
 * 是否有读文件权限
 */
fun Fragment.isFilePermission(permissions: Array<String> = filePermissions): Boolean {
    return XXPermissions.isGranted(requireContext(), permissions)
}

/**
 * 申请读文件权限
 */
fun FragmentActivity.requestFilePermission(
    permissionDesc: String = "申请读取文件权限，以便用户更好的使用和提供更好的服务",
    isShowToast: Boolean = true,
    permissions: Array<String> = filePermissions,
    block: (Boolean) -> Unit
) {
    requestPermission(
        permissions,
        permissionDesc,
        isShowToast,
        block
    )
}

/**
 * 申请读文件权限
 */
fun Fragment.requestFilePermission(
    permissionDesc: String = "申请读取文件权限，以便用户更好的使用和提供更好的服务",
    isShowToast: Boolean = true,
    permissions: Array<String> = filePermissions,
    block: (Boolean) -> Unit
) {
    requestPermission(
        permissions,
        permissionDesc,
        isShowToast,
        block
    )
}