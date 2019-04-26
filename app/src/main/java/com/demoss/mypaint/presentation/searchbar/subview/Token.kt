package com.demoss.mypaint.presentation.searchbar.subview

import android.graphics.Paint
import android.graphics.PointF
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.ACTION_UP
import android.view.MotionEvent
import androidx.annotation.ColorInt
import com.demoss.mypaint.presentation.searchbar.subview.abs.SubViewCircle

class Token(
    radius: Float,
    center: PointF,
    @ColorInt
    val activeColor: Int,
    @ColorInt
    val deactiveColor: Int
) : SubViewCircle(radius, center, Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    color = deactiveColor
}) {

    var isActive: Boolean = false
        set(value) {
            paint.color = if (value) activeColor else deactiveColor
            field = value
        }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        event.apply {
            when (action) {
                ACTION_DOWN -> isActive = action == ACTION_DOWN && center.x.inBounds(x) && center.y.inBounds(y)
                ACTION_UP -> isActive = false
            }
            return action == ACTION_DOWN || action == ACTION_UP
        }
    }

    private fun Float.inBounds(value: Float) = value >= this - radius && value <= this + radius
}