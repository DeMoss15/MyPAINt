package com.demoss.mypaint.presentation.paint

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View

class PaintView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    companion object {
        const val BRUSH_SIZE = 20f
        const val DEFAULT_COLOR = Color.RED
        const val DEFAULT_BACKGROUND = Color.WHITE
        private const val TOUCH_TOLERANCE = 4f
    }

    private var mX = 0f
    private var mY = 0f
    private lateinit var path: Path
    private val paths: MutableList<FingerPath> = mutableListOf()
    private var currentColor = DEFAULT_COLOR
    private var currentBackgroundColor = DEFAULT_BACKGROUND
    private var strokeWidth = BRUSH_SIZE
    private var isEmboss = false
    private var isBlur = false
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private var bitmapPaint: Paint = Paint()
    private val emboss: MaskFilter by lazy { EmbossMaskFilter(FloatArray(3), 0.4f, 6f, 3.5f) }
    private val blur: MaskFilter by lazy { BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL) }
    private val paint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            isDither = true
            color = currentColor
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            xfermode = null
            alpha = 0xff
        }
    }

    fun init(metrics: DisplayMetrics) {
        bitmap = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    fun normal() {
        isEmboss = false
        isBlur = false
    }

    fun blur() {
        isEmboss = false
        isBlur = true
    }

    fun emboss() {
        isEmboss = true
        isBlur = false
    }

    fun clear() {
        currentBackgroundColor = DEFAULT_BACKGROUND
        paths.clear()
        normal()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        canvas.save()
        this.canvas.drawColor(currentBackgroundColor)

        paths.forEach {
            paint.apply {
                color = it.color
                strokeWidth = it.strokeWidth
                maskFilter = when {
                    isEmboss -> emboss
                    isBlur -> blur
                    else -> null
                }
            }

            this.canvas.drawPath(it.path, paint)
        }

        canvas.drawBitmap(bitmap, 0f, 0f, bitmapPaint)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart(event.x, event.y)
            MotionEvent.ACTION_MOVE -> touchMove(event.x, event.y)
            MotionEvent.ACTION_UP -> touchUp()
            else -> null
        }?.let { invalidate() }
        return true
    }

    private fun touchStart(x: Float, y: Float) {
        path = Path()
        paths.add(FingerPath(currentColor, strokeWidth, isEmboss, isBlur, path))
        path.reset()
        path.moveTo(x, y)
        this.mX = x
        this.mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        if (Math.abs((x - mX)) >= TOUCH_TOLERANCE || Math.abs((y - mY)) >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        path.lineTo(mX, mY)
    }
}