package com.lqq.tool.net

import com.lqq.tool.bean.BannerBean
import com.snowflake.toolkit.net.ApiResponse
import retrofit2.http.GET

/**
 * 玩 Android 开放接口定义，配合 [com.snowflake.toolkit.net.NetRepository] 使用。
 *
 * 普通对象：`ApiResponse<Bean>`；分页：`ApiResponse<Paging<Bean>>`。
 *
 * @author Melon
 * @date 2026/8/9
 */
interface ApiService {

    /** 首页 Banner 列表，GET banner/json */
    @GET("banner/json")
    suspend fun getBanner(): ApiResponse<List<BannerBean>>
}
