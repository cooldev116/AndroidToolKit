package com.snowflake.toolkit.ext

/**
 * @description:
 * @author:Melon
 * @date:2025/8/11
 */
class Branch<T>(private var handled: Boolean, private var value: T? = null) {

    fun no(block: () -> Unit) {
        if (!handled) block()
    }

    fun elseIf(condition: Boolean, block: () -> Unit): Branch<Unit> {
        if (!handled && condition) {
            block()
            handled = true
        }
        return Branch(handled)
    }

    fun <R> elseIf(value: R?, block: (R) -> Unit): Branch<R> {
        if (!handled && value != null) {
            block(value)
            handled = true
        }
        return Branch(handled, value)
    }
}

// Boolean 扩展
inline fun Boolean.yes(block: () -> Unit): Branch<Unit> {
    val handled = this
    if (this) block()
    return Branch(handled)
}

// 可空对象扩展
inline fun <T> T?.yes(block: (T) -> Unit): Branch<T> {
    val handled = this != null
    if (handled) block(this!!)
    return Branch(handled, this)
}