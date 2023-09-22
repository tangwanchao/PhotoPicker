package me.twc.photopicker.lib.engine

import android.content.Context
import android.widget.ImageView
import me.twc.photopicker.lib.data.BaseItem

/**
 * @author 唐万超
 * @date 2023/09/12
 *
 * 条目加载引擎
 */
interface ImageEngine {
    /**
     * 加载条目数据到 [imageView]
     */
    fun load(context: Context, item: BaseItem, imageView: ImageView)
}