package me.twc.photopicker.lib.engine

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import java.io.Serializable

/**
 * @author 唐万超
 * @date 2023/09/12
 */
interface ImageEngine : Serializable {
    /**
     * 加载图片
     */
    fun loadPhoto(context: Context, uri: Uri, imageView: ImageView)
}