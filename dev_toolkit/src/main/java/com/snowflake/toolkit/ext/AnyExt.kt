package com.snowflake.toolkit.ext

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * Activity获取ViewBinding的代理方法
 */
inline fun <VB : ViewBinding> FragmentActivity.getViewBinding(inflater: LayoutInflater): VB {
    val dbClazz =
        (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
    val inflate = dbClazz[0].getDeclaredMethod("inflate", LayoutInflater::class.java)
    return inflate.invoke(null, inflater) as VB
}

/**
 * FragmentActivity多层集成时，获取ViewBinding的方式
 */
inline fun <VB : ViewBinding> FragmentActivity.getMultiViewBinding(
    inflater: LayoutInflater
): VB {
    var thisClass: Class<*> = javaClass
    while (true) {
        val type = thisClass.genericSuperclass
        if (type is ParameterizedType) {
            type.actualTypeArguments.forEach {
                if (it is Class<*> && ViewBinding::class.java.isAssignableFrom(it)) {
                    val inflate = it.getDeclaredMethod("inflate", LayoutInflater::class.java)
                    return inflate.invoke(null, inflater) as VB
                }
            }
        }
        thisClass = thisClass.superclass ?: break
    }
    throw IllegalArgumentException("<<<<---------------Not found ViewBidding type------------>>>>")
}

/**
 * Fragment获取ViewBinding的代理方法
 * 这个是用于有多层继承的，为了找到ViewBinding需要遍历
 */
inline fun <VB : ViewBinding> Fragment.getMultiViewBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
    isAttach: Boolean = false
): VB {
    var thisClass: Class<*> = javaClass
    while (true) {
        val type = thisClass.genericSuperclass
        if (type is ParameterizedType) {
            type.actualTypeArguments.forEach {
                if (it is Class<*> && ViewBinding::class.java.isAssignableFrom(it)) {
                    val inflate = it.getDeclaredMethod(
                        "inflate",
                        LayoutInflater::class.java,
                        ViewGroup::class.java,
                        Boolean::class.java
                    )
                    return inflate.invoke(null, inflater, container, false) as VB
                }
            }
        }
        thisClass = thisClass.superclass ?: break
    }
    throw IllegalArgumentException("<<<<---------------Not found ViewBidding type------------>>>>")
}

/**
 * Fragment获取ViewBinding的代理方法
 */
inline fun <VB : ViewBinding> Fragment.getViewBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
    isAttach: Boolean = false
): VB {
    val dbClazz =
        (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
    val inflate = dbClazz[0].getDeclaredMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )
    return inflate.invoke(null, inflater, container, false) as VB
}

/**
 * 获取ViewModel代理方法
 */
inline fun <VM : ViewModel> FragmentActivity.getViewModel(): VM {
    return realGenerateViewModel(this)
}

/**
 * 获取ViewModel代理方法
 */
inline fun <VM : ViewModel> Fragment.getViewModel(): VM {
    return realGenerateViewModel(this)
}

inline fun <VM : ViewModel> FragmentActivity.getMultiViewModel(): VM {
    var thisClass: Class<*> = javaClass
    while (true) {
        val type = thisClass.genericSuperclass
        if (type is ParameterizedType) {
            type.actualTypeArguments.forEach {
                if (it is Class<*> && ViewModel::class.java.isAssignableFrom(it)) {
                    val clazz = it as Class<VM>
                    return ViewModelProvider(this)[clazz]
                }
            }
        }
        thisClass = thisClass.superclass ?: break
    }
    throw IllegalArgumentException("<<<<---------------Not found ViewModel type------------>>>>")
}

/**
 * 多层继承时获取ViewModel
 */
inline fun <VM : ViewModel> Fragment.getMultiViewModel(): VM {
    var thisClass: Class<*> = javaClass
    while (true) {
        val type = thisClass.genericSuperclass
        if (type is ParameterizedType) {
            type.actualTypeArguments.forEach {
                if (it is Class<*> && ViewModel::class.java.isAssignableFrom(it)) {
                    val clazz = it as Class<VM>
                    return ViewModelProvider(this)[clazz]
                }
            }
        }
        thisClass = thisClass.superclass ?: break
    }
    throw IllegalArgumentException("<<<<---------------Not found ViewModel type------------>>>>")
}

inline fun <VM : ViewModel> realGenerateViewModel(owner: ViewModelStoreOwner): VM {
    val type = (owner.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]
    val clazz = type as Class<VM>
    return ViewModelProvider(owner)[clazz]
}