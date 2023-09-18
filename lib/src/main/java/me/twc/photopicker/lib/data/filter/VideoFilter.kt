package me.twc.photopicker.lib.data.filter

import java.io.Serializable

/**
 * @author 唐万超
 * @date 2023/09/18
 */
data class VideoFilter(
    // 是否查询视屏时长
    val queryDuration: Boolean = false,
    // 最短视屏时长
    val minDuration: Long = Long.MIN_VALUE,
    // 最长视屏时长
    val maxDuration: Long = Long.MAX_VALUE,
    // 是否查询视屏大小
    val querySize: Boolean = false,
    // 最小视屏大小
    val minSize: Long = Long.MIN_VALUE,
    // 最大视屏大小
    val maxSize: Long = Long.MAX_VALUE
) : Serializable