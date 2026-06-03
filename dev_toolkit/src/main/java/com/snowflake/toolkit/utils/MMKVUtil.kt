package com.snowflake.toolkit.utils

import android.os.Parcelable
import com.snowflake.toolkit.manger.ToolKitManager
import com.tencent.mmkv.MMKV

/**
 * 本地化保存工具类
 */
object MMKVUtil {
    private val mmkv by lazy {
        MMKV.initialize(ToolKitManager.instance.getContext())
        MMKV.defaultMMKV()
    }

    /**
     * 保存数据
     */
    fun <T> save(key: String, value: T): Boolean {
        return when (value) {
            is Boolean -> mmkv.encode(key, value)
            is Int -> mmkv.encode(key, value)
            is String -> mmkv.encode(key, value)
            is Float -> mmkv.encode(key, value)
            is Double -> mmkv.encode(key, value)
            is ByteArray -> mmkv.encode(key, value)
            is Parcelable -> mmkv.encode(key, value)
            is Set<*> -> mmkv.encode(key, value as Set<String>)
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return mmkv.decodeBool(key, default)
    }

    fun getInt(key: String, default: Int = 0): Int {
        return mmkv.decodeInt(key, default)
    }

    fun getLong(key: String, default: Long = 0L): Long {
        return mmkv.decodeLong(key, default)
    }

    fun getFloat(key: String, default: Float = 0f): Float {
        return mmkv.decodeFloat(key, default)
    }

    fun getDouble(key: String, default: Double = 0.0): Double {
        return mmkv.decodeDouble(key, default)
    }

    fun getString(key: String, default: String? = null): String? {
        return mmkv.decodeString(key, default)
    }

    fun getBytes(key: String, default: ByteArray? = null): ByteArray? {
        return mmkv.decodeBytes(key, default)
    }

    fun <T : Parcelable> getParcelable(
        key: String,
        clazz: Class<T>
    ): T? {
        return mmkv.decodeParcelable(key, clazz)
    }

    fun getStringSet(key: String, default: Set<String>? = null): Set<String>? {
        return mmkv.decodeStringSet(key, default)
    }

    // ======================
    // 其他常用方法
    // ======================

    fun remove(key: String) {
        mmkv.removeValueForKey(key)
    }

    fun contains(key: String): Boolean {
        return mmkv.containsKey(key)
    }

    fun clear() {
        mmkv.clearAll()
    }
}