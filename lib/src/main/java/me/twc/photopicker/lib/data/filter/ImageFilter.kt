package me.twc.photopicker.lib.data.filter

import java.io.Serializable

/**
 * @author 唐万超
 * @date 2023/09/18
 */
data class ImageFilter(
    // 是否查询照片大小
    val querySize: Boolean = false,
    // 最小照片大小
    val minSize: Long = Long.MIN_VALUE,
    // 最大照片大小
    val maxSize: Long = Long.MAX_VALUE
) : Serializable{
    fun fillSelectionArgs(args:MutableList<String>){
        if(querySize){
            args.add(minSize.toString())
            args.add(maxSize.toString())
        }
    }
}
