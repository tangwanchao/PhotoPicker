package me.twc.photopicker.lib.engine

import me.twc.photopicker.lib.data.BaseItem
import java.io.Serializable

/**
 * @author 唐万超
 * @date 2023/09/19
 */
interface ItemFilter : Serializable {
    fun filter(item: BaseItem): BaseItem?
}