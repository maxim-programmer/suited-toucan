package com.example.drivenext.presentation.onboarding

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.drivenext.R

class PageIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var count: Int = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var selected: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    private val density = resources.displayMetrics.density
    private val activeWidth = 32 * density
    private val inactiveWidth = 12 * density
    private val barHeight = 7 * density
    private val gap = 7 * density
    private val radius = barHeight / 2

    private val activePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.primary)
    }
    private val inactivePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.indicator_inactive)
    }
    private val rect = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val bars = count.coerceAtLeast(1)
        val totalWidth = activeWidth + (bars - 1) * (inactiveWidth + gap)
        setMeasuredDimension(
            resolveSize(totalWidth.toInt(), widthMeasureSpec),
            resolveSize(barHeight.toInt(), heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        var left = 0f
        for (index in 0 until count) {
            val isActive = index == selected
            val width = if (isActive) activeWidth else inactiveWidth
            rect.set(left, 0f, left + width, barHeight)
            canvas.drawRoundRect(rect, radius, radius, if (isActive) activePaint else inactivePaint)
            left += width + gap
        }
    }
}
