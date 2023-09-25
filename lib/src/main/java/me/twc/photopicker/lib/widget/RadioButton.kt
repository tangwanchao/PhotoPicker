package me.twc.photopicker.lib.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import me.twc.photopicker.lib.utils.ptFloat

/**
 * @author 唐万超
 * @date 2023/09/25
 */
class RadioButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    companion object {
        private val NORMAL_STROKE_WIDTH = 2.ptFloat
        private val HALF_NORMAL_STROKE_WIDTH = NORMAL_STROKE_WIDTH / 2
        private val SPACE = NORMAL_STROKE_WIDTH
        private val NORMAL_COLOR = Color.parseColor("#A8A8A8")
        private val CHECKED_COLOR = Color.parseColor("#1DAB1F")
    }

    private val mNormalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = NORMAL_STROKE_WIDTH

    }
    private val mCheckedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = CHECKED_COLOR
        style = Paint.Style.FILL
    }

    private var mCheckedChangedListener: CheckedChangedListener? = null

    fun setCheckedChangedListener(listener: CheckedChangedListener?) {
        mCheckedChangedListener = listener
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        postInvalidate()
        mCheckedChangedListener?.onChanged(isSelected)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mNormalPaint.color = if (isSelected) CHECKED_COLOR else NORMAL_COLOR
        var l = paddingStart + HALF_NORMAL_STROKE_WIDTH
        var t = paddingTop + HALF_NORMAL_STROKE_WIDTH
        var r = width - paddingEnd - HALF_NORMAL_STROKE_WIDTH
        var b = height - paddingBottom - HALF_NORMAL_STROKE_WIDTH
        canvas.drawOval(l, t, r, b, mNormalPaint)
        if (isSelected) {
            l += HALF_NORMAL_STROKE_WIDTH + SPACE
            t += HALF_NORMAL_STROKE_WIDTH + SPACE
            r -= (HALF_NORMAL_STROKE_WIDTH + SPACE)
            b -= (HALF_NORMAL_STROKE_WIDTH + SPACE)
            canvas.drawOval(l, t, r, b, mCheckedPaint)
        }
    }

    interface CheckedChangedListener {
        fun onChanged(isChecked: Boolean)
    }
}