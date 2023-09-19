package me.twc.photopicker.lib

import android.content.Context
import me.twc.photopicker.lib.data.Input
import me.twc.photopicker.lib.ui.PhotoPickerAdapter

/**
 * @author 唐万超
 * @date 2023/09/08
 *
 */
object AlbumModel {

    fun loadItems(context: Context, input: Input): List<PhotoPickerAdapter.ItemDisplay> {
        val items = mutableListOf<PhotoPickerAdapter.ItemDisplay>()
        context.contentResolver.query(
            input.getContentUri(),
            input.getProjections(),
            input.getSelection(),
            input.getSelectionArgs(),
            input.getQuerySortOrder()
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val item = input.createItem(cursor) ?: continue
                items.add(PhotoPickerAdapter.ItemDisplay(item))
            }
        }
        return items
    }
}