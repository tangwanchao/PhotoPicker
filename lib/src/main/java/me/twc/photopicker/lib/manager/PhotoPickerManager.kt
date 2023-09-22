package me.twc.photopicker.lib.manager

import android.app.Application
import com.blankj.utilcode.util.Utils
import me.twc.photopicker.lib.engine.ImageEngine

/**
 * @author 唐万超
 * @date 2023/09/12
 *
 * 库管理类
 */
@Suppress("unused")
object PhotoPickerManager {

    private lateinit var mImageEngine: ImageEngine

    fun init(app: Application, imageEngine: ImageEngine) {
        Utils.init(app)
        mImageEngine = imageEngine
    }

    fun setImageEngine(imageEngine: ImageEngine) {
        mImageEngine = imageEngine
    }

    fun getImageEngine(): ImageEngine = mImageEngine
}