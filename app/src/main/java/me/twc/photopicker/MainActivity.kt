package me.twc.photopicker

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PermissionUtils.SimpleCallback
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import me.twc.photopicker.databinding.ActMainBinding
import me.twc.photopicker.lib.data.BaseItem
import me.twc.photopicker.lib.data.Input
import me.twc.photopicker.lib.data.filter.ImageSelectionFilter
import me.twc.photopicker.lib.data.filter.VideoSelectionFilter
import me.twc.photopicker.lib.engine.ImageEngine
import me.twc.photopicker.lib.engine.ItemFilter
import me.twc.photopicker.lib.enums.SupportMedia
import me.twc.photopicker.lib.manager.PhotoPickerManager
import me.twc.photopicker.lib.ui.PhotoPickerActivity
import me.twc.photopicker.lib.utils.applySingleDebouncing500

class MainActivity : ComponentActivity() {


    private val mBinding by lazy { ActMainBinding.inflate(layoutInflater) }
    private val mPhotoPickerLauncher = registerForActivityResult(PhotoPickerActivity.Contract()) {

    }

    private object GlideEngine : ImageEngine {
        override fun load(context: Context, item: BaseItem, imageView: ImageView) {
            Glide.with(context)
                .load(item.uri)
                .into(imageView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        PhotoPickerManager.init(application,GlideEngine)
        mBinding.btnLaunch.applySingleDebouncing500 {
            val input = Input(
                supportMedia = SupportMedia.VIDEO,
                videoFilter = VideoSelectionFilter(
                    queryDuration = false,
                    minDuration = 6000L,
                    maxDuration = 30000L,
                    querySize = false,
                    minSize = 1024L * 1024L * 1L,
                    maxSize = 1024L * 1024L * 2L
                ),
                imageFilter = ImageSelectionFilter(
                    querySize = false,
                    minSize = 1024L * 1024L * 5L,
                    maxSize = 1024L * 1024L * 6L
                ),
                itemFilter = object : ItemFilter {
                    override fun filter(item: BaseItem): BaseItem {
                        return item
                    }
                }
            )
            mPhotoPickerLauncher.launch(input)
        }
    }
}
