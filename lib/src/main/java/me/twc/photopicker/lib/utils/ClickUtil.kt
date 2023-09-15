package me.twc.photopicker.lib.utils

import android.view.View
import com.blankj.utilcode.util.ClickUtils

/**
 * @author 唐万超
 * @date 2023/09/12
 */
fun View.applySingleDebouncing500(listener: (View) -> Unit) {
    ClickUtils.applySingleDebouncing(this, 500, listener)
}