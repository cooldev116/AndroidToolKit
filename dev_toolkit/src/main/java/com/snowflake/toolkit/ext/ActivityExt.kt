package com.snowflake.toolkit.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.snowflake.toolkit.constant.EXTRA_KEY_BEAN
import com.snowflake.toolkit.constant.EXTRA_KEY_BUNDLE

/**
 * @description:Activity相关扩展方法
 * @author:Melon
 * @date:2025/7/18
 */

fun Activity.openActivity(clazz: Class<out FragmentActivity>, bundle: Bundle? = null) {
    val intent = Intent(this, clazz)
    bundle?.apply {
        intent.putExtra(EXTRA_KEY_BUNDLE, this)
    }
    startActivity(intent)
}

fun Fragment.openActivity(clazz: Class<out FragmentActivity>, bundle: Bundle? = null) {
    val intent = Intent(requireActivity(), clazz)
    bundle?.apply {
        intent.putExtra(EXTRA_KEY_BUNDLE, this)
    }
    requireActivity().startActivity(intent)
}

fun Context.openActivity(clazz: Class<out FragmentActivity>, bundle: Bundle? = null) {
    val intent = Intent(this, clazz)
    if (this !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    bundle?.apply {
        intent.putExtra(EXTRA_KEY_BUNDLE, this)
    }
    startActivity(intent)
}

/**
 * 通过Intent传递的bundle
 */
fun FragmentActivity.extBundle(): Bundle? {
    return intent.getBundleExtra(EXTRA_KEY_BUNDLE)
}

/**
 * 通过Intent传递数据，获取数据bean
 */
fun <T> FragmentActivity.extBean(): T? {
    return extBundle()?.getParcelable(EXTRA_KEY_BEAN) as T?
}