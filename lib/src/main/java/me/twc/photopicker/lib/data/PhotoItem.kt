package me.twc.photopicker.lib.data

import android.net.Uri
import kotlinx.android.parcel.Parcelize

/**
 * @author 唐万超
 * @date 2023/09/11
 *
 * 照片条目
 */

@Parcelize
data class PhotoItem(
    override val id: Long,
    override val path: String,
    override val uri: Uri,
    override val type: String,
    override val size: Long = DEFAULT_SIZE,
    override var isSelected: Boolean = false
) : BaseItem(id, path, uri, type, size, isSelected)
