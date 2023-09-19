@file:Suppress("MemberVisibilityCanBePrivate")

package me.twc.photopicker.lib.data

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import me.twc.photopicker.lib.data.filter.ImageFilter
import me.twc.photopicker.lib.data.filter.VideoFilter
import me.twc.photopicker.lib.engine.ImageEngine
import me.twc.photopicker.lib.engine.ItemFilter
import me.twc.photopicker.lib.enums.SupportMedia
import me.twc.photopicker.lib.utils.CursorUtil
import java.io.Serializable
import java.lang.StringBuilder
import java.util.Locale

/**
 * @author 唐万超
 * @date 2023/09/08
 *
 * 启动图片选择器输入内容
 */
open class Input(
    val imageEngine: ImageEngine,
    val supportMedia: SupportMedia,
    val videoFilter: VideoFilter = VideoFilter(),
    val imageFilter: ImageFilter = ImageFilter(),
    val itemFilter: ItemFilter? = null
) : Serializable {

    companion object {
        const val EXTERNAL = "external"
        const val IMAGE_TYPE_SELECTION = "(${MediaStore.Files.FileColumns.MEDIA_TYPE}=${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE})"
        const val VIDEO_TYPE_SELECTION = "(${MediaStore.Files.FileColumns.MEDIA_TYPE}=${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO})"
        const val FILE_DURATION_SELECTION = "(${MediaStore.MediaColumns.DURATION}>=? AND ${MediaStore.MediaColumns.DURATION}<=?)"
        const val FILE_SIZE_SELECTION = "(${MediaStore.MediaColumns.SIZE}>=? AND ${MediaStore.MediaColumns.SIZE}<=?)"
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
        SupportMedia.IMAGE -> {
            val sb = StringBuilder()
            if (imageFilter.querySize) {
                sb.append(FILE_SIZE_SELECTION)
            }
            sb.toStringEmptyNull()
        }

        SupportMedia.VIDEO -> {
            val sb = StringBuilder()
            if (videoFilter.querySize) {
                sb.append(FILE_SIZE_SELECTION)
            }
            if (videoFilter.queryDuration) {
                sb.andAppend(FILE_DURATION_SELECTION)
            }
            sb.toStringEmptyNull()
        }

        SupportMedia.IMAGE_AND_VIDEO -> {
            // image
            val sb = StringBuilder("(")
            sb.append(IMAGE_TYPE_SELECTION)
            if (imageFilter.querySize) {
                sb.andAppend(FILE_SIZE_SELECTION)
            }
            sb.append(")")
            sb.append(" OR ")
            // video
            sb.append("(")
            sb.append(VIDEO_TYPE_SELECTION)
            if (videoFilter.querySize) {
                sb.andAppend(FILE_SIZE_SELECTION)
            }
            if (videoFilter.queryDuration) {
                sb.andAppend(FILE_DURATION_SELECTION)
            }
            sb.append(")")
            sb.toString()
        }
    }

    open fun getSelectionArgs(): Array<String>? = when (supportMedia) {
        SupportMedia.IMAGE -> mutableListOf<String>()
            .apply(imageFilter::fillSelectionArgs)
            .toTypedArray()

        SupportMedia.VIDEO -> mutableListOf<String>()
            .apply(videoFilter::fillSelectionArgs)
            .toTypedArray()

        SupportMedia.IMAGE_AND_VIDEO -> mutableListOf<String>()
            .apply(imageFilter::fillSelectionArgs)
            .apply(videoFilter::fillSelectionArgs)
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
        val item = if (type.isVideoType()) {
            val duration = if (videoFilter.queryDuration) {
                CursorUtil.getCursorLong(cursor, MediaStore.MediaColumns.DURATION) ?: VideoItem.DEFAULT_DURATION
            } else VideoItem.DEFAULT_DURATION
            val size = if (videoFilter.querySize) {
                CursorUtil.getCursorLong(cursor, MediaStore.MediaColumns.SIZE) ?: BaseItem.DEFAULT_SIZE
            } else BaseItem.DEFAULT_SIZE
            VideoItem(id, path, uri, type, size, duration)
        } else {
            val size = if (videoFilter.querySize) {
                CursorUtil.getCursorLong(cursor, MediaStore.MediaColumns.SIZE) ?: BaseItem.DEFAULT_SIZE
            } else BaseItem.DEFAULT_SIZE
            PhotoItem(id, path, uri, type, size)
        }
        return if (itemFilter != null) itemFilter.filter(item) else item
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
        return this.lowercase(Locale.getDefault()).startsWith("image/")
    }

    fun String.isVideoType(): Boolean {
        return this.lowercase(Locale.getDefault()).startsWith("video/")
    }

    fun StringBuilder.andAppend(value: String) {
        if (isNotEmpty()) {
            append(" AND ")
        }
        append(value)
    }

    fun StringBuilder.toStringEmptyNull(): String? {
        return if (isNotEmpty()) toString() else null
    }
}