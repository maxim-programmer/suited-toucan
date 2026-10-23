package com.example.drivenext.presentation.common

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.example.drivenext.R

class InfoRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelView = TextView(context)
    private val valueView = TextView(context)

    var label: CharSequence?
        get() = labelView.text
        set(value) {
            labelView.text = value
        }

    var value: CharSequence?
        get() = valueView.text
        set(newValue) {
            valueView.text = newValue
        }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val baseTypeface = ResourcesCompat.getFont(context, R.font.montserrat_family)
        val textColor = ContextCompat.getColor(context, R.color.text_primary)

        labelView.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP)
            setTextColor(textColor)
            typeface = baseTypeface?.let { Typeface.create(it, LABEL_WEIGHT, false) }
        }
        valueView.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP)
            setTextColor(textColor)
            gravity = Gravity.END
            typeface = baseTypeface
        }

        addView(labelView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        addView(
            valueView,
            LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = dp(VALUE_MARGIN_DP) }
        )

        val verticalPadding = dp(VERTICAL_PADDING_DP)
        setPadding(paddingLeft, verticalPadding, paddingRight, verticalPadding)

        context.withStyledAttributes(attrs, R.styleable.InfoRowView) {
            getString(R.styleable.InfoRowView_infoLabel)?.let { labelView.text = it }
        }
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(),
        resources.displayMetrics
    ).toInt()

    private companion object {
        const val TEXT_SIZE_SP = 12f
        const val LABEL_WEIGHT = 500
        const val VERTICAL_PADDING_DP = 8
        const val VALUE_MARGIN_DP = 16
    }
}
