package me.twc.photopicker.lib.data.filter

import android.content.ContentResolver
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.twc.photopicker.lib.engine.ItemFilter

/**
 * @author 唐万超
 * @date 2023/09/18
 *
 * 查询数据时,照片的选择条件
 *
 * 这个是 [ContentResolver.query] 查询时的选择条件
 *
 * @see [ItemFilter]
 */
@Parcelize
data class ImageSelectionFilter(
    // 是否查询照片大小
    val querySize: Boolean = false,
    // 最小照片大小
    val minSize: Long = Long.MIN_VALUE,
    // 最大照片大小
    val maxSize: Long = Long.MAX_VALUE
) : Parcelable {

    /**
     * 将选择参数填充到 args 中
     */
    fun fillSelectionArgs(args: MutableList<String>) {
        if (querySize) {
            args.add(minSize.toString())
            args.add(maxSize.toString())
        }
    }
}
