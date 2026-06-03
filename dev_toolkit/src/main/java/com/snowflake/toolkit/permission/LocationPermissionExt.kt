package com.snowflake.toolkit.permission

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

/**
 * @description:位置权限扩展函数
 * @author:Melon
 * @date:2026/2/5
 */
/**
 * 普通位置权限
 */
private val locationPermissions =
    arrayOf(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION)

/**
 * 需要后台定位的位置权限
 */
private val locationWithBackPermissions = arrayOf(
    Permission.ACCESS_COARSE_LOCATION,
    Permission.ACCESS_FINE_LOCATION,
    Permission.ACCESS_BACKGROUND_LOCATION
)

/**
 * @description 是否有位置权限
 * @author Melon
 * @time 2025/11/25 15:31
 */
fun FragmentActivity.isLocationPermission(isBackLocation: Boolean = false): Boolean {
    return if (isBackLocation) {
        XXPermissions.isGranted(applicationContext, locationWithBackPermissions)
    } else {
        XXPermissions.isGranted(applicationContext, locationPermissions)
    }
}

/**
 * @description 是否有位置权限
 * @author Melon
 * @time 2025/11/25 15:31
 */
fun Fragment.isLocationPermission(isBackLocation: Boolean = false): Boolean {
    return if (isBackLocation) {
        XXPermissions.isGranted(requireContext(), locationWithBackPermissions)
    } else {
        XXPermissions.isGranted(requireContext(), locationPermissions)
    }
}

/**
 * @description 申请位置权限
 * @author Melon
 * @param isBackLocation 是否需要后台定位，默认不需要
 * @time 2025/7/2 16:09
 */
fun FragmentActivity.requestLocationPermission(
    permissionDesc: String = "申请定位权限，以便用户更好的使用和提供更好的服务",
    isBackLocation: Boolean = false,
    isShowToast: Boolean = true,
    block: (Boolean) -> Unit
) {
    val permissions = if (isBackLocation) {
        locationWithBackPermissions
    } else {
        locationPermissions
    }
    requestPermission(
        permissions,
        permissionDesc,
        isShowToast,
        block
    )
}

/**
 * @description 申请位置权限
 * @author Melon
 * @param isBackLocation 是否需要后台定位，默认不需要
 * @time 2025/7/2 16:09
 */
fun Fragment.requestLocationPermission(
    permissionDesc: String = "申请定位权限，以便用户更好的使用和提供更好的服务",
    isBackLocation: Boolean = false,
    isShowToast: Boolean = true,
    block: (Boolean) -> Unit
) {
    val permissions = if (isBackLocation) {
        locationWithBackPermissions
    } else {
        locationPermissions
    }
    requestPermission(
        permissions,
        permissionDesc,
        isShowToast,
        block
    )
}