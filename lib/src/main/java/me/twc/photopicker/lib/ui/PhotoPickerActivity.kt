package me.twc.photopicker.lib.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.blankj.utilcode.util.BarUtils
import me.twc.photopicker.lib.AlbumModel
import me.twc.photopicker.lib.data.Input
import me.twc.photopicker.lib.data.Output
import me.twc.photopicker.lib.databinding.PhotoPickerActPhotoPickerBinding
import me.twc.photopicker.lib.utils.applySingleDebouncing500

/**
 * @author 唐万超
 * @date 2023/09/12
 */
class PhotoPickerActivity : BaseActivity() {

    companion object {
        private const val KEY_EXTRA_INPUT = "KEY_EXTRA_INPUT"
        private const val KEY_EXTRA_OUTPUT = "KEY_EXTRA_OUTPUT"
    }

    private val mBinding by lazy { PhotoPickerActPhotoPickerBinding.inflate(layoutInflater) }
    private lateinit var mInput: Input
    private lateinit var mAdapter: PhotoPickerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#333333")
        BarUtils.setStatusBarLightMode(this, false)
        setContentView(mBinding.root)
        parseIntent {
            initView()
            loadItems()
            initListener()
        }
    }

    //<editor-fold desc="初始化">
    @Suppress("DEPRECATION")
    private fun parseIntent(block: () -> Unit) {
        val input = intent.getSerializableExtra(KEY_EXTRA_INPUT) as? Input
        if (input == null) {
            finish()
            return
        }
        mInput = input
        mAdapter = PhotoPickerAdapter(mInput.imageEngine)
        block()
    }

    private fun initView() = mBinding.apply {
        tvTitle.text = mInput.supportMedia.title
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration())
        recyclerView.adapter = mAdapter
    }

    private fun initListener() = mBinding.apply {
        @Suppress("DEPRECATION")
        ivClose.applySingleDebouncing500 { onBackPressed() }
    }

    private fun loadItems() {
        val items = AlbumModel.loadItems(this, mInput)
        mAdapter.setItemDataList(items)
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