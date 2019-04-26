package com.demoss.mypaint.presentation.searchbar

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import com.demoss.mypaint.R
import com.demoss.mypaint.presentation.searchbar.subview.SeekBar
import com.demoss.mypaint.presentation.searchbar.subview.Token

class RoundSeekBarView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defaultStyle: Int = R.attr.roundSeekBarStyle
) : View(context, attr, defaultStyle) {

    companion object {
        private const val DEFAULT_BAR_WIDTH = 64f
    }

    private val seekBar: SeekBar

    init {
        val attributes = context.obtainStyledAttributes(
            attr,
            R.styleable.RoundSeekBarView,
            defaultStyle,
            R.style.Widget_RoundSeekBar
        )

        val width = attributes.getDimension(R.styleable.RoundSeekBarView_strokeWidth, DEFAULT_BAR_WIDTH)
        val deactiveColor = attributes.getColor(R.styleable.RoundSeekBarView_color, Color.BLUE)
        val activeColor = attributes.getColor(R.styleable.RoundSeekBarView_highlightColor, Color.CYAN)

        val token = Token(width / 2, PointF(), activeColor, deactiveColor)
        seekBar = SeekBar(token, width, activeColor, deactiveColor)

        setLayerType(LAYER_TYPE_SOFTWARE, seekBar.paint)
        attributes.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        seekBar.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        seekBar.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        return seekBar.onTouchEvent(event).also {
            postInvalidateOnAnimation()
        }
    }
}

data class Shadow(
    @Dimension
    val radius: Float,
    @ColorInt
    val color: Int,
    val center: PointF = PointF()
)