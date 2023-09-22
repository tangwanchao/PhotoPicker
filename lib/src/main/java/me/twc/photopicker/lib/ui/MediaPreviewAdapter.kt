package me.twc.photopicker.lib.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import me.twc.photopicker.lib.R
import me.twc.photopicker.lib.data.BaseItem
import me.twc.photopicker.lib.data.VideoItem
import me.twc.photopicker.lib.engine.ImageEngine
import me.twc.photopicker.lib.utils.applySingleDebouncing500

/**
 * @author 唐万超
 * @date 2023/09/20
 */
class MediaPreviewAdapter(
    private val mImageEngine: ImageEngine,
    private val mItemDataList: List<BaseItem>,
    private val mOnItemClickListener: () -> Unit
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

    override fun onViewAttachedToWindow(holder: MediaPreviewViewHolder) {
        holder.onAttach()
    }

    override fun onViewDetachedFromWindow(holder: MediaPreviewViewHolder) {
        holder.onDetached()
    }

    override fun onViewRecycled(holder: MediaPreviewViewHolder) {
        super.onViewRecycled(holder)
        holder.onRelease()
    }

    inner class MediaPreviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val mImageView = view.findViewById<ImageView>(R.id.iv_image)
        private var mPlayerView: PlayerView? = null
        private val mIvPlay = view.findViewById<ImageView>(R.id.iv_play)
        private var mPlayer: ExoPlayer? = null

        init {
            view.applySingleDebouncing500 { mOnItemClickListener() }
            mIvPlay.applySingleDebouncing500 { onPlayClick() }
        }

        fun bind(itemData: BaseItem) {
            itemView.tag = itemData
            mImageView.isVisible = true
            mIvPlay.isVisible = isVideoItem()
            mImageEngine.load(itemView.context, itemData, mImageView)
            videoRelease()
        }

        fun onAttach() {
            if (isVideoItem()) {
                mImageView.isVisible = true
                mIvPlay.isVisible = true
            }
        }


        fun onDetached() {
            if (isVideoItem()) {
                videoRelease()
                removePlayerView()
            }
        }

        fun onRelease() {
            if (isVideoItem()) {
                videoRelease()
            }
        }

        private fun isVideoItem(): Boolean {
            return itemView.tag is VideoItem
        }

        private fun onPlayClick() {
            if (!isVideoItem()) return
            addPlayerView()
            mImageView.isVisible = false
            mIvPlay.isVisible = false
            ExoPlayer.Builder(itemView.context).build().apply {
                mPlayer = this
                mPlayerView?.player = this
                val mediaItem = MediaItem.fromUri((itemView.tag as VideoItem).uri)
                setMediaItem(mediaItem)
                videoPrepare()
                videoPlay()
            }
        }

        private fun addPlayerView() {
            val root = itemView as ViewGroup
            val playerView = root.children.find { it is PlayerView }
            if (playerView != null) return
            mPlayerView = PlayerView(itemView.context).apply {
                useController = false
            }
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            root.addView(mPlayerView, 0, lp)
        }

        private fun removePlayerView() {
            val root = itemView as ViewGroup
            val playerView = root.children.find { it is PlayerView }
            if (playerView != null) {
                root.removeView(playerView)
            }
        }


        private fun videoPrepare() = mPlayer.useIfCommandAvailable(ExoPlayer.COMMAND_PREPARE) {
            it.prepare()
        }

        private fun videoPlay() = mPlayer.useIfCommandAvailable(ExoPlayer.COMMAND_PLAY_PAUSE) {
            it.play()
        }

        @Suppress("unused")
        private fun videoPause() = mPlayer.useIfCommandAvailable(ExoPlayer.COMMAND_PLAY_PAUSE) {
            it.pause()
        }

        @Suppress("unused")
        private fun videoStop() = mPlayer.useIfCommandAvailable(ExoPlayer.COMMAND_STOP) {
            it.stop()
        }

        private fun videoRelease() = mPlayer.useIfCommandAvailable(ExoPlayer.COMMAND_RELEASE) {
            it.release()
            mPlayer = null
        }

        @Suppress("SameParameterValue","unused")
        private fun videoSeekTo(positionMs: Long) = mPlayer.useIfCommandAvailable(ExoPlayer.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM) {
            it.seekTo(positionMs)
        }

        private fun ExoPlayer?.useIfCommandAvailable(command: Int, block: (player: ExoPlayer) -> Unit): Boolean {
            if (this != null && isCommandAvailable(command)) {
                block(this)
                return true
            }
            return false
        }
    }
}