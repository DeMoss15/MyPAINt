package com.demoss.mypaint.presentation.searchbar.subview

import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.view.MotionEvent
import android.view.MotionEvent.*
import androidx.annotation.ColorInt
import com.demoss.mypaint.presentation.searchbar.Shadow
import com.demoss.mypaint.presentation.searchbar.TrigonometricalCircle
import com.demoss.mypaint.presentation.searchbar.subview.abs.SubViewCircle
import java.lang.Math.abs
import java.lang.Math.toRadians

class SeekBar(
    private val token: Token,
    private val width: Float,
    @ColorInt
    activeColor: Int,
    @ColorInt
    deactiveColor: Int
) : SubViewCircle(0f, PointF(), Paint(ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE
    strokeWidth = width
}) {

    companion object {
        const val FULL_CIRCLE_ANGLE: Double = 360.0
        const val HALF_OF_CIRCLE_ANGLE: Double = 180.0
    }

    private val activeShadow: Shadow = Shadow(width / 2f, activeColor)
    private val deactiveShadow: Shadow = Shadow(width / 2f, deactiveColor)
    private val extruderPaint: Paint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = width
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
    }
    private val startAngle: Double = 90.0
    private val tCircle: TrigonometricalCircle
    private var minAngleInRad: Double = 1.0
    private var maxAngleInRad: Double = 1.0

    init {
        paint.setShadowLayer(deactiveShadow)
        tCircle = TrigonometricalCircle(center, radius)
    }

    private val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        textSize = 32f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(center.x, center.y, radius, extruderPaint)
        val position = (tCircle.calculateAngleInDegrees(token.center) + startAngle) % FULL_CIRCLE_ANGLE
        canvas.drawText(String.format("%.2f", position),  center.x, center.y, textPaint)
        token.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        token.onTouchEvent(event)
        when (event.action) {
            ACTION_DOWN -> if (token.isActive) paint.setShadowLayer(activeShadow)
            ACTION_UP -> paint.setShadowLayer(deactiveShadow)
            ACTION_MOVE -> {
                if (!token.isActive) {
                    paint.setShadowLayer(deactiveShadow)
                } else {
                    moveToken(PointF(event.x, event.y))
                }
            }
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = center.x - width * 1.25f
        // TODO setup start position
        tCircle.apply {
            center.set(center)
            radius = this@SeekBar.radius
            val paddingAngle = calculateAngleInDegrees(PointF(center.x + radius, center.y + token.radius))
            minAngleInRad = toRadians(-startAngle + paddingAngle)
            maxAngleInRad = toRadians(-startAngle - paddingAngle)
            getCoordinatesOnCircleForAngle(minAngleInRad).let {
                token.center.set(it)
            }
        }
    }

    private fun moveToken(pointF: PointF): Unit = with(tCircle) {
        // TODO code cleanup
        fun getRotatedAngleInDegrees(newPoint: PointF): Double =
            (calculateAngleInDegrees(newPoint) + startAngle) % FULL_CIRCLE_ANGLE

        val currentAngle = getRotatedAngleInDegrees(token.center)
        val newAngle = getRotatedAngleInDegrees(pointF)
        val isClipNeeded = abs(newAngle - currentAngle) > HALF_OF_CIRCLE_ANGLE

        getCoordinatesOnCircleForAngle(
            when {
                isClipNeeded && currentAngle > HALF_OF_CIRCLE_ANGLE -> maxAngleInRad
                isClipNeeded && currentAngle < HALF_OF_CIRCLE_ANGLE -> minAngleInRad
                else -> calculateAngle(pointF)
            }
        ).let { coordinates ->
            token.center.set(coordinates)
        }
    }

    private fun Paint.setShadowLayer(shadow: Shadow) =
        with(shadow) { setShadowLayer(radius, center.x, center.y, color) }

}

