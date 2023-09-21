package me.twc.photopicker.lib.data.filter

import android.content.ContentResolver
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.twc.photopicker.lib.engine.ItemFilter

/**
 * @author 唐万超
 * @date 2023/09/18
 *
 * 查询数据时,视屏的选择条件
 *
 * 这个是 [ContentResolver.query] 查询时的选择条件
 *
 * @see [ItemFilter]
 */
@Parcelize
data class VideoSelectionFilter(
    // 是否查询视屏大小
    val querySize: Boolean = false,
    // 最小视屏大小
    val minSize: Long = Long.MIN_VALUE,
    // 最大视屏大小
    val maxSize: Long = Long.MAX_VALUE,
    // 是否查询视屏时长
    val queryDuration: Boolean = false,
    // 最短视屏时长
    val minDuration: Long = Long.MIN_VALUE,
    // 最长视屏时长
    val maxDuration: Long = Long.MAX_VALUE,
) : Parcelable {

    /**
     * 将选择参数填充到 args 中
     */
    fun fillSelectionArgs(args: MutableList<String>) {
        if (querySize) {
            args.add(minSize.toString())
            args.add(maxSize.toString())
        }
        if (queryDuration) {
            args.add(minDuration.toString())
            args.add(maxDuration.toString())
        }
    }
}