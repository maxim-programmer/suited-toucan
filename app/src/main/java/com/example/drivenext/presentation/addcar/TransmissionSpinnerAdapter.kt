package com.example.drivenext.presentation.addcar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.drivenext.R
import com.example.drivenext.domain.model.Transmission

class TransmissionSpinnerAdapter(context: Context) : ArrayAdapter<String>(
    context,
    R.layout.item_spinner,
    listOf(
        context.getString(R.string.add_car_transmission_hint),
        context.getString(R.string.add_car_transmission_automatic),
        context.getString(R.string.add_car_transmission_manual)
    )
) {

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

        fun positionOf(transmission: Transmission?): Int = when (transmission) {
            Transmission.Automatic -> 1
            Transmission.Manual -> 2
            null -> HINT_POSITION
        }

        fun transmissionAt(position: Int): Transmission? = when (position) {
            1 -> Transmission.Automatic
            2 -> Transmission.Manual
            else -> null
        }
    }
}
