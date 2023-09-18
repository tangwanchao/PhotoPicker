package me.twc.photopicker.lib.data

import android.net.Uri
import kotlinx.android.parcel.Parcelize

/**
 * @author 唐万超
 * @date 2023/09/18
 */
@Parcelize
data class VideoItem(
    override val id: Long,
    override val path: String,
    override val uri: Uri,
    override val type: String,
    // 视屏时长
    val duration: Long = DEFAULT_DURATION,
    override var isSelected: Boolean = false
) : BaseItem(id, path, uri, type, isSelected){
    companion object{
        const val DEFAULT_DURATION = 0L
    }
}
