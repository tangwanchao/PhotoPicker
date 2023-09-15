package me.twc.photopicker.lib.utils

import com.blankj.utilcode.util.AdaptScreenUtils

/**
 * @author 唐万超
 * @date 2023/09/12
 */
val Number.pt
    get() = try {
        AdaptScreenUtils.pt2Px(this.toFloat())
    } catch (th: Throwable) {
        th.printStackTrace()
        this.toFloat().toInt()
    }

val Number.ptFloat
    get() = this.pt.toFloat()