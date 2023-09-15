package me.twc.photopicker.lib.ui

import android.graphics.Rect
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import me.twc.photopicker.lib.utils.pt

/**
 * @author 唐万超
 * @date 2023/09/13
 */
class VerticalSpaceItemDecoration : ItemDecoration() {

    private val mSpace: Int = 5.pt

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        val itemCount = parent.adapter?.itemCount ?: return
        val spanCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1
        val lastRowCount = if (itemCount % spanCount == 0) spanCount else itemCount % spanCount
        if (itemPosition < itemCount - lastRowCount) {
            outRect.bottom = mSpace
        }
    }
}