package me.twc.photopicker.lib.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.animation.AccelerateInterpolator
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
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
        setContentView(mBinding.root)
        layoutFullScreen()
        applyStateBarHeight { stateBarHeight ->
            mBinding.flActionBar.setPadding(0, stateBarHeight, 0, 0)
        }
        parseIntent {
            initView()
            initListener()
        }
    }

    override fun onStart() {
        super.onStart()
        getCurrentViewHolder()?.changeToVideoPreviewStartState()
    }

    override fun onStop() {
        super.onStop()
        getCurrentViewHolder()?.changeToViewPreviewStopState()
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
        mAdapter = MediaPreviewAdapter(mInput.imageEngine, mInput.items, ::onItemClick)
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

    private fun changeTopAndBottomBarVisibility() = mBinding.apply {
        if (flActionBar.tag != null) return@apply
        flActionBar.tag = ""
        val preIsVisible = flActionBar.isVisible
        if (!preIsVisible) {
            flActionBar.isVisible = true
            flBottom.isVisible = true
        }
        val topHeight = flActionBar.height
        val botHeight = flBottom.height
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addUpdateListener {
            var value = it.animatedValue as Float
            if (!preIsVisible) {
                value = 1f - value
            }
            flActionBar.translationY = -(topHeight * value)
            flBottom.translationY = botHeight * value
        }
        valueAnimator.addListener(onEnd = {
            flActionBar.tag = null
            if (preIsVisible) {
                flActionBar.isVisible = false
                flBottom.isVisible = false
            }
        })
        valueAnimator.interpolator = AccelerateInterpolator()
        valueAnimator.duration = 300L
        valueAnimator.start()
    }
    //</editor-fold>

    //<editor-fold desc="回调">
    private fun onSendClick() {
        complete()
    }

    private fun onItemClick() {
        changeTopAndBottomBarVisibility()
    }
    //</editor-fold>

    //<editor-fold desc="私有方法">
    private fun getCurrentViewHolder(): MediaPreviewAdapter.MediaPreviewViewHolder? {
        val recyclerView = mBinding.viewPager.getChildAt(0) as RecyclerView
        val viewHolder = recyclerView.findViewHolderForLayoutPosition(mBinding.viewPager.currentItem)
        return viewHolder as? MediaPreviewAdapter.MediaPreviewViewHolder
    }

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