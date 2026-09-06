package com.adwant.kit.bean

/**
 * 广告配置接口 `/api/v1/ad/config` 的 data 结构。
 * [networkAppId] 用于穿山甲 / AdKit 初始化；[placements] 为各广告位开关与 slotId。
 */
data class AdConfigBean(
    val configVersion: Int = 0,
    val master: Boolean = false,
    val networkAppId: String = "",
    val placements: List<AdPlacementBean>? = null,
)

/**
 * 单个广告位配置。
 */
data class AdPlacementBean(
    val code: String = "",
    val enabled: Boolean = false,
    val name: String = "",
    val slotId: String = "",
    val type: Int = 0,
    val typeName: String = "",
)
