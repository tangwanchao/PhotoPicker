package me.twc.photopicker.lib.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import me.twc.photopicker.lib.AlbumModel
import me.twc.photopicker.lib.data.BaseItem
import me.twc.photopicker.lib.data.Input
import me.twc.photopicker.lib.data.Output
import me.twc.photopicker.lib.databinding.PhotoPickerActPhotoPickerBinding
import me.twc.photopicker.lib.enums.SupportMedia
import me.twc.photopicker.lib.manager.PhotoPickerManager
import me.twc.photopicker.lib.utils.applySingleDebouncing500

/**
 * @author 唐万超
 * @date 2023/09/12
 */
class PhotoPickerActivity : BaseActivity() {

    private val mBinding by lazy { PhotoPickerActPhotoPickerBinding.inflate(layoutInflater) }
    private val mPreviewLauncher = registerForActivityResult(MediaPreviewActivity.Contract(), ::onPreviewResult)
    private lateinit var mInput: Input
    private lateinit var mAdapter: PhotoPickerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#333333")
        BarUtils.setStatusBarLightMode(this, false)
        setContentView(mBinding.root)
        parseIntent {
            requestPermission()
        }
    }

    //<editor-fold desc="初始化">
    @Suppress("DEPRECATION")
    private fun parseIntent(block: () -> Unit) {
        val input = intent.getParcelableExtra(KEY_EXTRA_INPUT) as? Input
        if (input == null) {
            finish()
            return
        }
        mInput = input
        mAdapter = PhotoPickerAdapter(
            PhotoPickerManager.getImageEngine(),
            mOnSelectedCountChangeListener = ::onSelectedCountChanged
        )
        block()
    }

    private fun requestPermission() {
        val appTargetSdkVersion = AppUtils.getAppTargetSdkVersion()
        val permissions = mutableListOf<String>()
        if (appTargetSdkVersion >= Build.VERSION_CODES.TIRAMISU &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            when (mInput.supportMedia) {
                SupportMedia.IMAGE -> permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                SupportMedia.VIDEO -> permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
                SupportMedia.IMAGE_AND_VIDEO -> {
                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                    permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
                }
            }
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        PermissionUtils.permission(*permissions.toTypedArray())
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    initView()
                    loadItems()
                    initListener()
                }

                override fun onDenied() {
                    ToastUtils.showLong("应用无读取权限")
                }

            })
            .request()
    }

    private fun initView() = mBinding.apply {
        tvTitle.text = mInput.supportMedia.title
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration())
        recyclerView.adapter = mAdapter
        updateBottomTextViews(0)
    }

    private fun initListener() = mBinding.apply {
        @Suppress("DEPRECATION")
        ivClose.applySingleDebouncing500 { onBackPressed() }
        tvPreview.applySingleDebouncing500 { onPreviewClick() }
        tvSend.applySingleDebouncing500 { onSendClick() }
    }


    //</editor-fold>

    //<editor-fold desc="更新 UI">
    @SuppressLint("SetTextI18n")
    private fun updateBottomTextViews(count: Int) = mBinding.apply {
        tvPreview.text = "预览($count)"
        tvSend.text = "发送($count)"
        tvPreview.isEnabled = count > 0
        tvSend.isEnabled = count > 0
    }
    //</editor-fold>

    //<editor-fold desc="回调">
    private fun onSelectedCountChanged(count: Int) {
        updateBottomTextViews(count)
    }

    private fun onPreviewClick() {
        val input = MediaPreviewActivity.Input(mAdapter.getSelectedItems())
        mPreviewLauncher.launch(input)
    }

    private fun onPreviewResult(output: MediaPreviewActivity.Output?) {
        if (output == null) return
        complete(output.items)
    }

    private fun onSendClick() {
        complete(mAdapter.getSelectedItems())
    }
    //</editor-fold>

    //<editor-fold desc="私有方法">
    private fun loadItems() {
        val items = AlbumModel.loadItems(this, mInput)
        mAdapter.setItemDataList(items)
    }

    private fun complete(items: List<BaseItem>) {
        val resultIntent = Intent()
        resultIntent.putExtra(KEY_EXTRA_OUTPUT, Output(items))
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
    //</editor-fold>


    class Contract : ActivityResultContract<Input, Output?>() {
        override fun createIntent(context: Context, input: Input): Intent {
            val intent = Intent(context, PhotoPickerActivity::class.java)
            intent.putExtra(KEY_EXTRA_INPUT, input)
            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Output? {
            @Suppress("DEPRECATION")
            return intent?.getParcelableExtra(KEY_EXTRA_OUTPUT) as? Output
        }
    }
}