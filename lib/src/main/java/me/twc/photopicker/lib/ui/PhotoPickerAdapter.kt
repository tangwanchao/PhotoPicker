package me.twc.photopicker.lib.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import me.twc.photopicker.lib.R
import me.twc.photopicker.lib.data.BaseItem
import me.twc.photopicker.lib.engine.ImageEngine
import me.twc.photopicker.lib.utils.applySingleDebouncing500
import me.twc.photopicker.lib.widget.TextRadioButton

/**
 * @author 唐万超
 * @date 2023/09/12
 */
class PhotoPickerAdapter(
    private val mImageEngine: ImageEngine,
    private val mItemDataList: MutableList<ItemDisplay> = mutableListOf(),
    private val mOnSelectedCountChangeListener: ((count: Int) -> Unit)? = null
) : RecyclerView.Adapter<PhotoPickerAdapter.PhotoPickerViewHolder>() {

    private val mPayloadsTag = Any()
    private var mSelectedItems = mutableListOf<ItemDisplay>()

    override fun getItemCount(): Int = mItemDataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoPickerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.photo_picker_item_photo_picker, parent, false)
        return PhotoPickerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoPickerViewHolder, position: Int, payloads: MutableList<Any>) {
        val itemData = mItemDataList[position]
        itemData.position = position
        if (payloads.contains(mPayloadsTag)) {
            holder.bindWithPayloadsTag(itemData)
        } else {
            holder.bind(itemData)
        }
    }

    override fun onBindViewHolder(holder: PhotoPickerViewHolder, position: Int) {
        // do noting
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItemDataList(list: List<ItemDisplay>) {
        mItemDataList.clear()
        mItemDataList.addAll(list)
        notifyDataSetChanged()
    }

    fun getSelectedItems() = mSelectedItems.map { it.realItemData }

    inner class PhotoPickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mImageView = view.findViewById<ImageView>(R.id.image_view)
        private val mViewForeground = view.findViewById<View>(R.id.view_foreground)
        private val mRadioButton = view.findViewById<TextRadioButton>(R.id.radio_button)

        init {
            mRadioButton.applySingleDebouncing500 {
                val itemDisplay = itemView.tag as ItemDisplay
                itemDisplay.isSelected = !itemDisplay.isSelected
                if (itemDisplay.isSelected) {
                    mSelectedItems.add(itemDisplay)
                    itemDisplay.number = mSelectedItems.size
                    notifyItemChanged(itemDisplay.position, mPayloadsTag)
                } else {
                    mSelectedItems.remove(itemDisplay)
                    for (selectedItem in mSelectedItems) {
                        if (selectedItem.number > itemDisplay.number) {
                            selectedItem.number = selectedItem.number - 1
                            notifyItemChanged(selectedItem.position, mPayloadsTag)
                        }
                    }
                    itemDisplay.number = 0
                    notifyItemChanged(itemDisplay.position, mPayloadsTag)
                }
                mOnSelectedCountChangeListener?.invoke(mSelectedItems.size)
            }
        }

        fun bind(itemDisplay: ItemDisplay) {
            itemView.tag = itemDisplay
            mImageEngine.load(itemView.context, itemDisplay.realItemData, mImageView)
            bindWithPayloadsTag(itemDisplay)
        }

        fun bindWithPayloadsTag(itemDisplay: ItemDisplay) {
            mViewForeground.isSelected = itemDisplay.isSelected
            mRadioButton.isSelected = itemDisplay.isSelected
            mRadioButton.setNumber(itemDisplay.number)
        }
    }

    data class ItemDisplay(
        val realItemData: BaseItem,
        var isSelected: Boolean = false,
        var number: Int = 0,
        var position: Int = 0
    )
}