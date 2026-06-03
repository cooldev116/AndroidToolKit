package com.snowflake.toolkit.ext

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.snowflake.toolkit.manger.ToolKitManager

/**
 * @description:吐司扩展函数
 * @author:Melon
 * @date:2025/5/30
 */
fun Context.toast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message ?: "", duration).show()
}

fun Fragment.toast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), message ?: "", duration).show()
}

fun FragmentActivity.toast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(applicationContext, message ?: "", duration).show()
}

fun String?.toast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(ToolKitManager.instance.getContext(), this ?: "", duration).show()
}