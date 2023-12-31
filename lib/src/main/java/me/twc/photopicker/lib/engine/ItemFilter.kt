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
 * 注意: ItemFilter 子类必须保证能够序列化
 *
 * @see [ImageSelectionFilter]
 * @see [VideoSelectionFilter]
 */
interface ItemFilter : Serializable {

    /**
     * 注意:不要在此方法做耗时较长的操作,不然会导致首屏加载缓慢
     */
    fun filter(item: BaseItem): BaseItem?
}