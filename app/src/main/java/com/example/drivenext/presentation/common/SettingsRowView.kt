package com.example.drivenext.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.example.drivenext.R

class SettingsRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_settings_row, this, true)
        val iconView = findViewById<ImageView>(R.id.rowIcon)
        val titleView = findViewById<TextView>(R.id.rowTitle)

        context.withStyledAttributes(attrs, R.styleable.SettingsRowView) {
            val iconRes = getResourceId(R.styleable.SettingsRowView_rowIcon, 0)
            if (iconRes != 0) iconView.setImageResource(iconRes)
            titleView.text = getString(R.styleable.SettingsRowView_rowTitle)
        }

        isClickable = true
        isFocusable = true
        val typedValue = TypedValue()
        if (context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)) {
            foreground = ContextCompat.getDrawable(context, typedValue.resourceId)
        }
    }
}
