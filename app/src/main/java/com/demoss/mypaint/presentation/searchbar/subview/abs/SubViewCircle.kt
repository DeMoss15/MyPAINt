package com.demoss.mypaint.presentation.searchbar.subview.abs

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF

abstract class SubViewCircle(radius: Float, center: PointF, paint: Paint) : Circle(radius, center, paint),
    SubView {
    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(center.x, center.y, radius, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        center.set(w / 2f, h / 2f)
        radius = center.x
    }
}