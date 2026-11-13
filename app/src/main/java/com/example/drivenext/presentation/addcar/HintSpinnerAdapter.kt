package com.example.drivenext.presentation.addcar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.drivenext.R

class HintSpinnerAdapter(
    context: Context,
    hint: String,
    items: List<String>
) : ArrayAdapter<String>(context, R.layout.item_spinner, listOf(hint) + items) {

    init {
        setDropDownViewResource(R.layout.item_spinner_dropdown)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        val colorRes = if (position == HINT_POSITION) R.color.hint else R.color.text_primary
        view.setTextColor(ContextCompat.getColor(context, colorRes))
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        if (position == HINT_POSITION) {
            view.visibility = View.GONE
            view.layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        } else {
            view.visibility = View.VISIBLE
            view.layoutParams = AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (DROPDOWN_ITEM_HEIGHT_DP * context.resources.displayMetrics.density).toInt()
            )
        }
        return view
    }

    companion object {
        private const val HINT_POSITION = 0
        private const val DROPDOWN_ITEM_HEIGHT_DP = 48

        fun positionOf(items: List<String>, value: String?): Int {
            val index = items.indexOf(value)
            return if (index >= 0) index + 1 else HINT_POSITION
        }

        fun itemAt(items: List<String>, position: Int): String? = items.getOrNull(position - 1)
    }
}