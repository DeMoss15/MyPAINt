package com.demoss.mypaint.presentation.searchbar

import android.graphics.PointF
import java.lang.Math.*
import kotlin.math.cos
import kotlin.math.sin

class TrigonometricalCircle(val center: PointF, var radius: Float) {

    fun calculateAngle(touchPosition: PointF): Double {
        return atan2(touchPosition.y - center.y.toDouble(), touchPosition.x - center.x.toDouble())
    }

    fun calculateAngleInDegrees(touchPosition: PointF): Double {
        val degrees = toDegrees(calculateAngle(touchPosition))
        return degrees + if (degrees < 0) 360 else 0
    }

    fun getCoordinatesOnCircleForAngle(angleInRad: Double): PointF = angleInRad.toFloat().run {
        PointF(
            center.x + radius * cos(this),
            center.y + radius * sin(this)
        )
    }
}