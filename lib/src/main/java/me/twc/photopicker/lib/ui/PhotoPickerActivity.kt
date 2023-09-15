package me.twc.photopicker.lib.ui

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES.S
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import me.twc.photopicker.lib.AlbumModel
import me.twc.photopicker.lib.data.Input
import me.twc.photopicker.lib.data.Output
import me.twc.photopicker.lib.databinding.PhotoPickerActPhotoPickerBinding
import kotlin.concurrent.thread

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
        setContentView(mBinding.root)
        parseIntent {
            initView()
            loadItems()
        }

        thread {
            while (true){
                Thread.sleep(3000L)
                System.gc()
            }
        }
    }

    //<editor-fold desc="初始化">
    private fun parseIntent(block: () -> Unit) {
        mInput = intent.getSerializableExtra(KEY_EXTRA_INPUT) as? Input ?: return
        mAdapter = PhotoPickerAdapter(mInput.imageEngine)
        block()
    }

    private fun initView() = mBinding.apply {
        val itemDecoration = VerticalSpaceItemDecoration()
        recyclerView.addItemDecoration(itemDecoration)
        recyclerView.adapter = mAdapter
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