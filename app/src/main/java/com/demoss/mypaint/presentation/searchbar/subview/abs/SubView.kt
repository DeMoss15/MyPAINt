package com.demoss.mypaint.presentation.searchbar.subview.abs

import android.graphics.Canvas
import android.view.MotionEvent

interface SubView {
    fun onDraw(canvas: Canvas)
    fun onTouchEvent(event: MotionEvent): Boolean
    fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {}
}