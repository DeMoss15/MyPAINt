package com.demoss.mypaint.presentation.paint

import android.graphics.Path
import androidx.annotation.ColorInt
import androidx.annotation.Dimension

data class FingerPath(
        @ColorInt
        var color: Int,
        @Dimension
        var strokeWidth: Float,
        var emboss: Boolean,
        var blur: Boolean,
        var path: Path
)