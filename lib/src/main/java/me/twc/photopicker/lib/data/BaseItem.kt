package me.twc.photopicker.lib.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author 唐万超
 * @date 2023/09/11
 *
 * 媒体条目
 *
 * @see [PhotoItem]
 * @see [VideoItem]
 */
@Parcelize
open class BaseItem(
    // 数据库 id
    open val id: Long,
    // 文件绝对路径
    open val path: String,
    // 文件 uri
    open val uri: Uri,
    // 文件类型
    open val type: String,
    // 文件大小
    open val size: Long = DEFAULT_SIZE,
    // 是否是原图
    open var isOriginal:Boolean = DEFAULT_ORIGINAL
) : Parcelable {
    companion object {
        const val DEFAULT_SIZE = 0L
        const val DEFAULT_ORIGINAL = false
    }
}