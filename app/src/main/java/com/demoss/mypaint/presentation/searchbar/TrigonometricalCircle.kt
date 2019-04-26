package com.demoss.mypaint.presentation.searchbar

import android.graphics.PointF
import java.lang.Math.*
import kotlin.math.cos
import kotlin.math.sin

class TrigonometricalCircle(private val center: PointF, private val radius: Float) {

    fun calculateAngle(touchPosition: PointF): Float {
        return atan2(touchPosition.y - center.y.toDouble(), touchPosition.x - center.x.toDouble()).toFloat()
    }

    fun calculateAngleInDegrees(touchPosition: PointF): Float {
        val degrees = toDegrees(calculateAngle(touchPosition).toDouble()).toFloat()
        return degrees + if (degrees < 0) 360f else 0f
    }

    fun getCoordinatesOnCircleForAngle(angleInRad: Float): PointF = PointF(
        center.x + radius * cos(angleInRad),
        center.y + radius * sin(angleInRad)
    )
}