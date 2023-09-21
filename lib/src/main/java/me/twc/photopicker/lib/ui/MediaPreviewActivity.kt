package me.twc.photopicker.lib.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.blankj.utilcode.util.BarUtils
import kotlinx.parcelize.Parcelize
import me.twc.photopicker.lib.data.BaseItem
import me.twc.photopicker.lib.databinding.PhotoPickerActMediaPreviewBinding
import me.twc.photopicker.lib.engine.ImageEngine
import me.twc.photopicker.lib.utils.applySingleDebouncing500

/**
 * @author 唐万超
 * @date 2023/09/20
 */
class MediaPreviewActivity : BaseActivity() {


    private val mBinding by lazy { PhotoPickerActMediaPreviewBinding.inflate(layoutInflater) }
    private lateinit var mInput: Input
    private lateinit var mAdapter: MediaPreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#333333")
        BarUtils.setStatusBarLightMode(this, false)
        setContentView(mBinding.root)
        parseIntent {
            initView()
            initListener()
        }
    }

    @Suppress("DEPRECATION")
    private fun parseIntent(block: () -> Unit) {
        val input = intent?.getParcelableExtra(KEY_EXTRA_INPUT) as? Input
        if (input == null) {
            finish()
            return
        }
        mInput = input
        block()
    }

    //<editor-fold desc="初始化">
    private fun initView() = mBinding.apply {
        updateTitle()
        mAdapter = MediaPreviewAdapter(mInput.imageEngine, mInput.items)
        viewPager.adapter = mAdapter
        updateBottomTextViews()
    }

    @Suppress("DEPRECATION")
    private fun initListener() = mBinding.apply {
        ivBack.applySingleDebouncing500 { onBackPressed() }
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateTitle()
            }
        })
        tvSend.applySingleDebouncing500 { onSendClick() }
    }
    //</editor-fold>

    //<editor-fold desc="更新 UI">
    @SuppressLint("SetTextI18n")
    private fun updateTitle() {
        mBinding.tvTitle.text = "${mBinding.viewPager.currentItem + 1}/${mInput.items.size}"
    }

    @SuppressLint("SetTextI18n")
    private fun updateBottomTextViews() = mBinding.apply {
        tvSend.text = "发送(${mInput.items.size})"
    }
    //</editor-fold>

    //<editor-fold desc="回调">
    private fun onSendClick() {
        complete()
    }
    //</editor-fold>

    //<editor-fold desc="私有方法">
    private fun complete() {
        val result = Intent()
        result.putExtra(KEY_EXTRA_OUTPUT, Output(mInput.items))
        setResult(Activity.RESULT_OK, result)
        finish()
    }
    //</editor-fold>

    @Parcelize
    data class Input(
        val imageEngine: ImageEngine,
        val items: List<BaseItem>
    ) : Parcelable

    @Parcelize
    data class Output(
        val items: List<BaseItem>
    ) : Parcelable

    class Contract : ActivityResultContract<Input, Output?>() {
        override fun createIntent(context: Context, input: Input): Intent {
            val showIntent = Intent(context, MediaPreviewActivity::class.java)
            showIntent.putExtra(KEY_EXTRA_INPUT, input)
            return showIntent
        }

        @Suppress("DEPRECATION")
        override fun parseResult(resultCode: Int, intent: Intent?): Output? {
            return intent?.getParcelableExtra(KEY_EXTRA_OUTPUT) as? Output
        }

    }
}