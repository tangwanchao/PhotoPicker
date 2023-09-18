package me.twc.photopicker.lib.data.filter

import android.icu.lang.UCharacter.GraphemeClusterBreak.L
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
    val maxDuration: Long = Long.MAX_VALUE
) : Serializable