package com.demoss.mypaint.presentation.searchbar

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import com.demoss.mypaint.R
import kotlin.math.pow

class RoundSeekBarView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defaultStyle: Int = R.attr.roundSeekBarStyle
) : View(context, attr, defaultStyle) {

    companion object {
        private const val DEFAULT_BAR_WIDTH = 64f
    }

    private val mainPaint: Paint
    private val extruderPaint: Paint

    private val bar = Bar()
    private val token = Token()
    private val deactiveShadow: Shadow
    private val activeShadow: Shadow

    init {
        val attributes = context.obtainStyledAttributes(
            attr,
            R.styleable.RoundSeekBarView,
            defaultStyle,
            R.style.Widget_RoundSeekBar
        )

        bar.width = attributes.getDimension(R.styleable.RoundSeekBarView_strokeWidth, DEFAULT_BAR_WIDTH)
        val color = attributes.getColor(R.styleable.RoundSeekBarView_color, Color.BLUE)
        val highlightColor = attributes.getColor(R.styleable.RoundSeekBarView_highlightColor, Color.CYAN)

        mainPaint = createStrokePaint(bar.width)
        extruderPaint = createStrokePaint(bar.width).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        }

        token.radius = bar.width / 2
        token.paint.color = color
        deactiveShadow = Shadow(bar.width / 2f, color)
        activeShadow = Shadow(bar.width / 2f, highlightColor)

        mainPaint.setShadowLayer(deactiveShadow)
        setLayerType(LAYER_TYPE_SOFTWARE, mainPaint)
        attributes.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bar.center.x = w / 2f
        bar.center.y = h / 2f
        bar.radius = bar.center.x - bar.width * 1.25f
        token.center.apply {
            if (x == 0f && y == 0f) {
                x = bar.center.x
                y = bar.center.y - bar.radius
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        with(bar) {
            canvas.drawCircle(center.x, center.y, radius, mainPaint)
            canvas.drawCircle(center.x, center.y, radius, extruderPaint)
        }
        with(token) {
            canvas.drawCircle(center.x, center.y, radius, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mainPaint.setShadowLayer(activeShadow)
                token.paint.color = activeShadow.color
                moveToken(event)
                postInvalidateOnAnimation()
            }
            MotionEvent.ACTION_UP -> {
                mainPaint.setShadowLayer(deactiveShadow)
                token.paint.color = deactiveShadow.color
                postInvalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                moveToken(event)
                postInvalidateOnAnimation()
            }
        }
        return true
    }

    private fun moveToken(event: MotionEvent): Unit = with(event) {
        val xProjection = x - bar.center.x
        val yProjection = y - bar.center.y
        val hypotenuse = Math.sqrt((xProjection.pow(2) + yProjection.pow(2)).toDouble()).toFloat()

        token.center.apply {
            x = bar.center.x + bar.radius * xProjection / hypotenuse
            y = bar.center.y + bar.radius * yProjection / hypotenuse
        }
    }

    private fun Paint.setShadowLayer(shadow: Shadow) =
        with(shadow) { setShadowLayer(radius, center.x, center.y, color) }

    private fun createStrokePaint(width: Float) = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = width
    }
}

data class Token(
    val center: PointF = PointF(),
    @Dimension
    var radius: Float = 0f,
    val paint: Paint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
)

data class Bar(
    val center: PointF = PointF(),
    @Dimension
    var radius: Float = 0f,
    @Dimension
    var width: Float = 0f
)

data class Shadow(
    @Dimension
    val radius: Float,
    @ColorInt
    val color: Int,
    val center: PointF = PointF()
)