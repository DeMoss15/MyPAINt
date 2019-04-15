package com.demoss.mypaint.presentation.waves

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.graphics.alpha
import com.demoss.mypaint.R

class WavesView
@JvmOverloads
constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.wavesViewStyle
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val MAX_ALPHA = 255
    }

    private val wavePaint: Paint
    private val waveGap: Float
    private val starPoints: Int
    private val starSharpness: Float
    private val angelInRadians: Double

    private var center = PointF(0f, 0f)
    private var initialRadius = 0f
    private var maxRadius = 0f

    private var wavePath = Path()

    private var waveAnimator: ValueAnimator? = null
    private var waveRadiusOffset = 0f
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }

    init {
        val attrs = context.obtainStyledAttributes(attrs, R.styleable.WavesView, defStyleAttr, R.style.Widget_WaveView)

        wavePaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = attrs.getColor(R.styleable.WavesView_waveColor, 0)
            strokeWidth = attrs.getDimension(R.styleable.WavesView_waveStrokeWidth, 0f)
            style = Paint.Style.STROKE
        }

        starPoints = attrs.getInt(R.styleable.WavesView_starVertexCount, 5) * 2
        starSharpness = attrs.getFloat(R.styleable.WavesView_starSharpness, 0.5f)
        angelInRadians = 2 * Math.PI / starPoints
        waveGap = attrs.getDimension(R.styleable.WavesView_waveGap, 50f)
        attrs.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        waveAnimator = ValueAnimator.ofFloat(0f, waveGap).apply {
            addUpdateListener {
                waveRadiusOffset = it.animatedValue as Float
            }
            duration = 1500L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }

    override fun onDetachedFromWindow() {
        waveAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //set the center of all circles to be center of the view
        center.set(w / 2f, h / 2f)
        maxRadius = Math.hypot(center.x.toDouble(), center.y.toDouble()).toFloat()
        /*Math.sqrt((w * w + h * h).toDouble()).toFloat()*/
        initialRadius = w / waveGap
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //draw circles separated by a space the size of waveGap
        var currentRadius = initialRadius + waveRadiusOffset
        val alphaShift = (MAX_ALPHA / (maxRadius / waveGap)).toInt()
        wavePaint.alpha = MAX_ALPHA
        while (currentRadius < maxRadius) {
            wavePaint.alpha -= alphaShift
            canvas.drawPath(wavePath.createStar(currentRadius), wavePaint)
            currentRadius += waveGap
        }
    }

    private fun Path.createStar(radius: Float) = this.apply {
        reset()
        var currentAngle = 0.0
        val smallerRadius = radius * starSharpness

        fun findNewPoint(r: Float): Pair<Float, Float> =
                center.x + (r * Math.cos(currentAngle)).toFloat() to
                        center.y + (r * Math.sin(currentAngle)).toFloat()

        findNewPoint(radius).apply { moveTo(first, second) }

        for (i in 1..starPoints + 1) {
            with(findNewPoint(if (i % 2 == 0) smallerRadius else radius)) {
                lineTo(first, second)
            }
            currentAngle -= angelInRadians
        }
        close()
    }
}