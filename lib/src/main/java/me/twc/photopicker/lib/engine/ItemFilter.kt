package me.twc.photopicker.lib.engine

import me.twc.photopicker.lib.data.BaseItem
import me.twc.photopicker.lib.data.filter.ImageSelectionFilter
import me.twc.photopicker.lib.data.filter.VideoSelectionFilter
import java.io.Serializable

/**
 * @author 唐万超
 * @date 2023/09/19
 *
 * 查询数据后对数据进行赛选过滤
 *
 * @see [ImageSelectionFilter]
 * @see [VideoSelectionFilter]
 */
interface ItemFilter : Serializable {
    fun filter(item: BaseItem): BaseItem?
}