package me.twc.photopicker.lib.data

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import me.twc.photopicker.lib.data.filter.VideoFilter
import me.twc.photopicker.lib.engine.ImageEngine
import me.twc.photopicker.lib.enums.SupportMedia
import me.twc.photopicker.lib.utils.CursorUtil
import java.io.Serializable

/**
 * @author 唐万超
 * @date 2023/09/08
 *
 * 启动图片选择器输入内容
 */
open class Input(
    val imageEngine: ImageEngine,
    val supportMedia: SupportMedia,
    val videoFilter: VideoFilter = VideoFilter()
) : Serializable {

    companion object {
        const val EXTERNAL = "external"
        const val VIDEO_DURATION_SELECTION = "(${MediaStore.MediaColumns.DURATION}>=? AND ${MediaStore.MediaColumns.DURATION}<=?)"
        const val FILE_TYPE_SELECTION = "(${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
    }

    open fun getContentUri(): Uri = when (supportMedia) {
        SupportMedia.IMAGE -> MediaStore.Images.Media.getContentUri(EXTERNAL)
        SupportMedia.VIDEO -> MediaStore.Video.Media.getContentUri(EXTERNAL)
        SupportMedia.IMAGE_AND_VIDEO -> MediaStore.Files.getContentUri(EXTERNAL)
    }

    open fun getProjections(): Array<String>? = mutableListOf(
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.DATA,
        MediaStore.MediaColumns.MIME_TYPE
    ).apply {
        if (videoFilter.queryDuration) {
            add(MediaStore.MediaColumns.DURATION)
        }
    }.toTypedArray()

    open fun getSelection(): String? = when (supportMedia) {
        SupportMedia.IMAGE -> null
        SupportMedia.VIDEO -> if (videoFilter.queryDuration) VIDEO_DURATION_SELECTION else null
        SupportMedia.IMAGE_AND_VIDEO -> "$FILE_TYPE_SELECTION AND $VIDEO_DURATION_SELECTION"
    }

    open fun getSelectionArgs(): Array<String>? = when (supportMedia) {
        SupportMedia.IMAGE -> null
        SupportMedia.VIDEO -> mutableListOf<String>()
            .apply {
                if (videoFilter.queryDuration) {
                    add(videoFilter.minDuration.toString())
                    add(videoFilter.maxDuration.toString())
                }
            }.toTypedArray()

        SupportMedia.IMAGE_AND_VIDEO -> mutableListOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        ).apply {
            if (videoFilter.queryDuration) {
                add(videoFilter.minDuration.toString())
                add(videoFilter.maxDuration.toString())
            }
        }
            .toTypedArray()
    }

    open fun getQuerySortOrder(): String? = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"

    open fun createItem(cursor: Cursor): BaseItem? {
        val id = CursorUtil.getCursorLong(cursor, MediaStore.MediaColumns._ID) ?: return null
        val path = CursorUtil.getCursorString(cursor, MediaStore.MediaColumns.DATA) ?: return null
        val type = CursorUtil.getCursorString(cursor, MediaStore.MediaColumns.MIME_TYPE) ?: return null
        val uri = type.typeToUri(id) ?: return null
        if (path.isBlank()) return null
        if (type.isBlank()) return null
        if (!isSupportType(type)) return null
        return if (type.isVideoType()) {
            val duration = if (videoFilter.queryDuration) {
                CursorUtil.getCursorLong(cursor, MediaStore.MediaColumns.DURATION) ?: VideoItem.DEFAULT_DURATION
            } else VideoItem.DEFAULT_DURATION
            VideoItem(id, path, uri, type, duration)
        } else {
            PhotoItem(id, path, uri, type)
        }
    }

    open fun isSupportType(type: String): Boolean = when (supportMedia) {
        SupportMedia.IMAGE -> type.isImageType()
        SupportMedia.VIDEO -> type.isVideoType()
        SupportMedia.IMAGE_AND_VIDEO -> type.isImageType() || type.isVideoType()
    }

    open fun String.typeToUri(id: Long): Uri? = when {
        isImageType() -> ContentUris.withAppendedId(MediaStore.Images.Media.getContentUri(EXTERNAL), id)
        isVideoType() -> ContentUris.withAppendedId(MediaStore.Video.Media.getContentUri(EXTERNAL), id)
        else -> null
    }

    fun String.isImageType(): Boolean {
        return this.toLowerCase().startsWith("image/")
    }

    fun String.isVideoType(): Boolean {
        return this.toLowerCase().startsWith("video/")
    }
}