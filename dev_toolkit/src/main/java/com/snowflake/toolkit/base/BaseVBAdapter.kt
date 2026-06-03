package com.snowflake.toolkit.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.lang.reflect.ParameterizedType

/**
 * @description:带有ViewBinding列表适配器
 * @author:Melon
 * @date:2025/7/18
 */
abstract class BaseVBAdapter<T, VB : ViewBinding>(
    @LayoutRes private val layoutResId: Int
) :
    BaseQuickAdapter<T, BaseVBAdapter.VBViewHolder<VB>>(layoutResId) {

    private val bindingClass: Class<VB> by lazy {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]
        (type as Class<VB>)
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): VBViewHolder<VB> {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        val method = bindingClass.getMethod("bind", View::class.java)
        val binding = method.invoke(null, view) as VB
        return VBViewHolder(binding)
    }

    override fun convert(holder: VBViewHolder<VB>, item: T) {
        convert(holder.binding, item, holder.bindingAdapterPosition)
    }

    abstract fun convert(binding: VB, item: T, position: Int)

    class VBViewHolder<VB : ViewBinding>(val binding: VB) : BaseViewHolder(binding.root)
}