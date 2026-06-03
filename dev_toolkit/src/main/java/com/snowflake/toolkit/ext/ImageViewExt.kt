package com.snowflake.toolkit.ext

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.BaseRequestOptions
import com.snowflake.toolkit.R

/**
 * @description:ImageView的扩展方法
 * @author:Melon
 * @date:2025/5/27
 */

/**
 * @description glide加载网络图片扩展方法
 * @param url 图片地址
 * @author Melon
 * @time 2025/5/27 16:53
 */
fun ImageView.load(
    url: String?,
    @DrawableRes placeholder: Int = R.drawable.error_image,
    @DrawableRes error: Int = R.drawable.placeholder_image
) {
    Glide.with(context)
        .load(url?:"")
        .placeholder(placeholder)
        .error(error)
        .into(this)
}

/**
 * @description glide加载网络图片带有配置参数
 * @param url 图片地址
 * @param options 相关配置
 * @author Melon
 * @time 2025/5/27 16:53
 */
fun ImageView.loadWithOptions(url: String?, options: BaseRequestOptions<*>) {
    Glide.with(context)
        .load(url?:"")
        .apply(options)
        .into(this)
}