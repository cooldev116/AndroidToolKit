package com.lqq.tool.net

import com.lqq.tool.bean.ArticleBean
import com.lqq.tool.bean.BannerBean
import com.lqq.tool.bean.PageBean
import com.snowflake.toolkit.net.ApiResponse
import com.snowflake.toolkit.net.Paging
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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

    @POST("/api/v1/p/tools/articles/list")
    suspend fun getArticleList(
        @Body params: PageBean
    ): ApiResponse<Paging<ArticleBean>>
}
