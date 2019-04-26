package com.demoss.mypaint.presentation.searchbar.subview

import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import androidx.annotation.ColorInt
import com.demoss.mypaint.presentation.searchbar.Shadow
import com.demoss.mypaint.presentation.searchbar.TrigonometricalCircle
import com.demoss.mypaint.presentation.searchbar.subview.abs.SubViewCircle
import java.lang.Math.*
import kotlin.math.atan2
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

    private val activeShadow: Shadow = Shadow(width / 2f, activeColor)
    private val deactiveShadow: Shadow = Shadow(width / 2f, deactiveColor)
    private val extruderPaint: Paint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = width
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
    }

    init {
        paint.setShadowLayer(deactiveShadow)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(center.x, center.y, radius, extruderPaint)
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
        val barCircle = TrigonometricalCircle(center, radius)
        barCircle.getCoordinatesOnCircleForAngle(toRadians(-START_ANGLE.toDouble()).toFloat()).let {
            token.center.set(it)
        }
    }

    private val START_ANGLE = 90f

    private fun moveToken(pointF: PointF): Unit = with(TrigonometricalCircle(center, radius)) {
        // TODO code cleanup
        val currentAngle = (calculateAngleInDegrees(token.center) + START_ANGLE) % 360
        val newAngle = (calculateAngleInDegrees(pointF) + START_ANGLE) % 360

        if (abs(- newAngle + currentAngle) < 90f) {
            getCoordinatesOnCircleForAngle(calculateAngle(pointF))
        } else {
            val angle = - START_ANGLE + if (currentAngle > 180) -1.0 else 1.0
            getCoordinatesOnCircleForAngle(toRadians(angle).toFloat())
        }.let { coordinates ->
            token.center.set(coordinates)
        }
    }

    private fun Paint.setShadowLayer(shadow: Shadow) =
        with(shadow) { setShadowLayer(radius, center.x, center.y, color) }

}

