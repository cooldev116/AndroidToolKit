package com.snowflake.toolkit.ext

import com.snowflake.toolkit.enums.TimeFormatType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @description:时间相关的扩展函数
 * @author:Melon
 * @date:2025/7/11
 */
private val dateFormatAllCn by lazy {
    SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault())
}
private val dateFormatYMDHMCn by lazy {
    SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault())
}
private val dateFormatYMDCn by lazy {
    SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
}
private val dateFormatAll by lazy {
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
}
private val dateFormatYMDHM by lazy {
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
}
private val dateFormatYMD by lazy {
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
}
private val dateFormatHMS by lazy {
    SimpleDateFormat("HH:mm:ss", Locale.getDefault())
}
private val dateFormatHM by lazy {
    SimpleDateFormat("HH:mm", Locale.getDefault())
}

/**
 * @description 时间格式化
 * @param mode 格式化类型，具体格式可以看上面变量
 * @return 格式化后的时间
 * @author Melon
 * @time 2025/7/11 17:02
 */
fun Long?.toTime(mode: TimeFormatType = TimeFormatType.MODE_ALL): String {
    return if (this == null) {
        ""
    } else {
        val date = Date(this)
        when (mode) {
            TimeFormatType.MODE_ALL_CN -> dateFormatAllCn.format(date)
            TimeFormatType.MODE_YMD_HM_CN -> dateFormatYMDHMCn.format(date)
            TimeFormatType.MODE_YMD_CN -> dateFormatYMDCn.format(date)
            TimeFormatType.MODE_ALL -> dateFormatAll.format(date)
            TimeFormatType.MODE_YMD_HM -> dateFormatYMDHM.format(date)
            TimeFormatType.MODE_YMD -> dateFormatYMD.format(date)
            TimeFormatType.MODE_HMS -> dateFormatHMS.format(date)
            TimeFormatType.MODE_HM -> dateFormatHM.format(date)
        }
    }
}

/**
 * @description 时间格式化
 * @param sdf 自定义时间格式
 * @return 格式化后的时间
 * @author Melon
 * @time 2025/7/11 17:05
 */
fun Long?.toTime(sdf: SimpleDateFormat): String {
    return if (this == null) {
        ""
    } else {
        sdf.format(this)
    }
}