package me.twc.photopicker

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.ComponentActivity
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PermissionUtils.SimpleCallback
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import me.twc.photopicker.databinding.ActMainBinding
import me.twc.photopicker.lib.AlbumModel
import me.twc.photopicker.lib.data.Input
import me.twc.photopicker.lib.engine.ImageEngine
import me.twc.photopicker.lib.enums.SupportMedia
import me.twc.photopicker.lib.ui.PhotoPickerActivity
import me.twc.photopicker.lib.utils.applySingleDebouncing500
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {


    private val mBinding by lazy { ActMainBinding.inflate(layoutInflater) }
    private val mPhotoPickerLauncher = registerForActivityResult(PhotoPickerActivity.Contract()) {

    }

    private object GlideEngine : ImageEngine {
        override fun loadPhoto(context: Context, uri: Uri, imageView: ImageView) {
            Glide.with(context)
                .load(uri)
                .into(imageView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.btnLaunch.applySingleDebouncing500 {
            PermissionUtils.permission(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
            )
                .callback(object : SimpleCallback {
                    override fun onGranted() {
                        val input = Input(
                            imageEngine = GlideEngine,
                            supportMedia = SupportMedia.IMAGE
                        )
                        mPhotoPickerLauncher.launch(input)
                    }

                    override fun onDenied() {
                        ToastUtils.showLong("onDenied")
                    }

                })
                .request()
        }
    }
}
