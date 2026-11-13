package com.example.drivenext.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.drivenext.R
import com.google.android.material.card.MaterialCardView

class PhotoSlotView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val photoView = ImageView(context).apply {
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        scaleType = ImageView.ScaleType.CENTER_CROP
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    private val plusView = ImageView(context).apply {
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        setImageResource(R.drawable.ic_add_photo)
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    init {
        radius = resources.displayMetrics.density * CORNER_RADIUS_DP
        cardElevation = 0f
        strokeWidth = 0
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.search_header))
        addView(photoView)
        addView(plusView)
        isClickable = true
        isFocusable = true
    }

    fun showPhoto(uri: String?, maxSizePx: Int): Boolean {
        if (uri == null) {
            photoView.tag = null
            photoView.setImageDrawable(null)
            plusView.visibility = VISIBLE
            return true
        }
        plusView.visibility = GONE
        return photoView.showImageFromUri(uri, maxSizePx)
    }

    private companion object {
        const val CORNER_RADIUS_DP = 8f
    }
}
