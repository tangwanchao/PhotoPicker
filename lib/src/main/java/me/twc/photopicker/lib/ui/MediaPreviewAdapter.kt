package me.twc.photopicker.lib.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import me.twc.photopicker.lib.R
import me.twc.photopicker.lib.data.BaseItem
import me.twc.photopicker.lib.data.PhotoItem
import me.twc.photopicker.lib.data.VideoItem
import me.twc.photopicker.lib.engine.ImageEngine

/**
 * @author 唐万超
 * @date 2023/09/20
 */
class MediaPreviewAdapter(
    private val mImageEngine: ImageEngine,
    private val mItemDataList: List<BaseItem>
) : RecyclerView.Adapter<MediaPreviewAdapter.MediaPreviewViewHolder>() {

    override fun getItemCount(): Int = mItemDataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaPreviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.photo_picker_item_media_preview, parent, false)
        return MediaPreviewViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MediaPreviewViewHolder, position: Int) {
        val itemData = mItemDataList[position]
        holder.bind(itemData)
    }

    inner class MediaPreviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val mImageView = view.findViewById<ImageView>(R.id.iv_image)

        fun bind(itemData: BaseItem) {
            itemView.tag = itemData
            when(itemData){
                is PhotoItem->{
                    mImageEngine.load(itemView.context,itemData,mImageView)
                }
                is VideoItem->{

                }
            }
        }
    }
}