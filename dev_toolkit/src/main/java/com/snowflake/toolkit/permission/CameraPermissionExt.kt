package com.snowflake.toolkit.permission

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

/**
 * @description:相机权限扩展函数
 * @author:Melon
 * @date:2026/2/5
 */

private val cameraPermission = arrayOf(Permission.CAMERA)

private const val cameraPermissionDesc = "请授权相机权限，用于拍照、摄像"

/**
 * @description 是否有相机权限
 * @author Melon
 * @time 2025/11/25 16:16
 */
fun FragmentActivity.isCameraPermisson(): Boolean {
    return XXPermissions.isGranted(applicationContext, cameraPermission)
}

/**
 * @description 是否有相机权限
 * @author Melon
 * @time 2025/11/25 16:16
 */
fun Fragment.isCameraPermisson(): Boolean {
    return XXPermissions.isGranted(requireContext(), cameraPermission)
}

/**
 * @description 申请相机权限
 * @author Melon
 * @time 2026/2/5 11:38
 */
fun FragmentActivity.requestCameraPermission(
    permissionDesc: String = cameraPermissionDesc,
    isShowToast: Boolean = true,
    block: (Boolean) -> Unit
) {
    requestPermission(
        cameraPermission,
        permissionDesc,
        isShowToast,
        block
    )
}

/**
 * @description 申请相机权限
 * @author Melon
 * @time 2026/2/5 11:38
 */
fun Fragment.requestCameraPermission(
    permissionDesc: String = cameraPermissionDesc,
    isShowToast: Boolean = true,
    block: (Boolean) -> Unit
) {
    requestPermission(
        cameraPermission,
        permissionDesc,
        isShowToast,
        block
    )
}