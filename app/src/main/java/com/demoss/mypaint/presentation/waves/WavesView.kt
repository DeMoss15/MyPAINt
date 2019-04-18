package com.demoss.mypaint.presentation.waves

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import com.demoss.mypaint.R

class WavesView
@JvmOverloads
constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.wavesViewStyle
) : View(context, attrs, defStyleAttr), TiltListener {

    companion object {
        const val ZERO = 0f
        const val FIRST_ALPHA = 0.25f
        const val SECOND_ALPHA = 0f
    }

    private val wavePaint: Paint
    private val waveGap: Float
    private val starPoints: Int
    private val starSharpness: Float
    private val angelInRadians: Double
    private val gradientColors: IntArray
    private val gradientPaint: Paint = Paint(ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }
    private val gradientMatrix = Matrix()

    private var center = PointF(ZERO, ZERO)
    private var initialRadius = ZERO
    private var maxRadius = ZERO

    private var wavePath = Path()

    private var waveAnimator: ValueAnimator? = null
    private var waveRadiusOffset = ZERO
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }

    init {
        val attrs = context.obtainStyledAttributes(attrs, R.styleable.WavesView, defStyleAttr, R.style.Widget_WaveView)

        val attrColor = attrs.getColor(R.styleable.WavesView_waveColor, 0)

        wavePaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = attrColor
            strokeWidth = attrs.getDimension(R.styleable.WavesView_waveStrokeWidth, ZERO)
            style = Paint.Style.STROKE
        }

        gradientColors = intArrayOf(attrColor, modifyAlpha(attrColor, FIRST_ALPHA), modifyAlpha(attrColor, SECOND_ALPHA))

        starPoints = attrs.getInt(R.styleable.WavesView_starVertexCount, 5) * 2
        starSharpness = attrs.getFloat(R.styleable.WavesView_starSharpness, 0.5f)
        angelInRadians = 2 * Math.PI / starPoints
        waveGap = attrs.getDimension(R.styleable.WavesView_waveGap, 50f)
        attrs.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        waveAnimator = ValueAnimator.ofFloat(ZERO, waveGap).apply {
            addUpdateListener {
                waveRadiusOffset = it.animatedValue as Float
            }
            duration = 1500L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
        tiltSensor.addListener(this)
        tiltSensor.register()
    }

    override fun onDetachedFromWindow() {
        waveAnimator?.cancel()
        tiltSensor.unregister()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //set the center of all circles to be center of the view
        center.set(w / 2f, h / 2f)
        maxRadius = Math.hypot(center.x.toDouble(), center.y.toDouble()).toFloat()
        initialRadius = w / waveGap
        gradientPaint.shader = RadialGradient(center.x, center.y, maxRadius,
                gradientColors, null, Shader.TileMode.CLAMP)
        updateGradient(center.x, center.y)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //draw circles separated by a space the size of waveGap
        var currentRadius = initialRadius + waveRadiusOffset
        while (currentRadius < maxRadius) {
            canvas.drawPath(wavePath.createStar(currentRadius), wavePaint)
            currentRadius += waveGap
        }
        canvas.drawPaint(gradientPaint)
    }

    val tiltSensor = WaveTiltSensor(context)

    override fun onTilt(pitch: Double, roll: Double) {
        ///////////////////////////////////////////////////////////////////////////
        // we take a half of the screen because max offset from the center
        // is just half of the screen
        // that's why adjacent is just a half of the screen, than max opposite will be
        // half of screen too
        ///////////////////////////////////////////////////////////////////////////
        val oppositeX = center.x
        val oppositeY = center.y

        val adjacentX = (Math.tan(roll) * oppositeX).toFloat()
        val adjacentY = (Math.tan(pitch) * oppositeY).toFloat()
        Log.d("DEB_TAG", "x ${center.x}, y ${center.y}===================================")

        ///////////////////////////////////////////////////////////////////////////
        // adjacentY with minus because of scales:
        // pitch: down -; up +; roll: left -; right +;
        // and start of view's coordinates is in left right corner and x,y scales are positive!
        ///////////////////////////////////////////////////////////////////////////
        updateGradient(adjacentX, -adjacentY)
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

    private fun updateGradient(x: Float, y: Float) {
        gradientMatrix.setTranslate(x, y)
        gradientPaint.shader.setLocalMatrix(gradientMatrix)
        postInvalidateOnAnimation()
    }

    private fun modifyAlpha(color: Int, a: Float): Int {
        return Color.argb(
                Math.round(Color.alpha(color) * a),
                Color.red(color),
                Color.blue(color),
                Color.green(color)
        )
    }
}