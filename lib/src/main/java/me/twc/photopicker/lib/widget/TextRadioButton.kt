package me.twc.photopicker.lib.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.MainThread
import me.twc.photopicker.lib.utils.ptFloat

/**
 * @author 唐万超
 * @date 2023/09/12
 */
class TextRadioButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    companion object {

        private val STROKE_WIDTH = 2.ptFloat
        private val HALF_STROKE_WIDTH = STROKE_WIDTH / 2F
        private val TEXT_SIZE = 12.ptFloat

        private val TEXT_PAINT = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = TEXT_SIZE
        }

        private val NORMAL_PAINT = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = STROKE_WIDTH
        }
        private val NORMAL_BG_PAINT = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#33333333")
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = STROKE_WIDTH
        }

        private val SELECT_PAINT = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00C65C")
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = STROKE_WIDTH
        }
    }

    private var mNumber: Int = 0
    private val mNumberBounds = Rect()

    @MainThread
    fun setNumber(number: Int) {
        mNumber = number
        TEXT_PAINT.getTextBounds(mNumber.toString(), 0, mNumber.toString().length, mNumberBounds)
        mNumberBounds.right = TEXT_PAINT.measureText(mNumber.toString()).toInt()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val l = paddingLeft + HALF_STROKE_WIDTH
        val t = paddingTop + HALF_STROKE_WIDTH
        val r = width - paddingRight - HALF_STROKE_WIDTH
        val b = height - paddingBottom - HALF_STROKE_WIDTH
        canvas.drawOval(l, t, r, b, getBgPaint())
        canvas.drawOval(l, t, r, b, getPaint())

        val x = (r - l - mNumberBounds.width()) / 2f + l
        val y = (b - t - mNumberBounds.height()) / 2f + t + mNumberBounds.height()
        if (mNumber > 0) {
            canvas.drawText(mNumber.toString(), x, y, TEXT_PAINT)
        }
    }

    private fun getBgPaint(): Paint = NORMAL_BG_PAINT
    private fun getPaint(): Paint = if (isSelected) SELECT_PAINT else NORMAL_PAINT
}