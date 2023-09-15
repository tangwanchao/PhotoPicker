package me.twc.photopicker.lib.utils

import android.database.Cursor


/**
 * @author 唐万超
 * @date 2023/09/11
 */
object CursorUtil {

    fun getCursorString(cursor: Cursor, columnName: String): String? = getCursorValue(cursor, columnName, String::toString)

    fun getCursorLong(cursor: Cursor, columnName: String): Long? = getCursorValue(cursor, columnName, String::toLongOrNull)

    fun getCursorInt(cursor: Cursor, columnName: String): Int? = getCursorValue(cursor, columnName, String::toIntOrNull)

    private fun <T> getCursorValue(cursor: Cursor, columnName: String, block: String.() -> T?): T? {
        val index = cursor.getColumnIndex(columnName)
        if (index == -1) {
            return null
        }
        val value = cursor.getString(index) ?: return null
        return value.block()
    }
}

