package com.demoss.mypaint.presentation.searchbar.subview.abs

import android.graphics.Paint
import android.graphics.PointF
import androidx.annotation.Dimension

abstract class Circle (
    @Dimension
    var radius: Float,
    var center: PointF,
    val paint: Paint
)