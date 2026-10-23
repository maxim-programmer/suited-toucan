package com.example.drivenext.presentation.bookings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.drivenext.R
import com.example.drivenext.databinding.ItemBookingBinding
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.model.BookingStatus
import com.example.drivenext.presentation.common.formatDate
import com.example.drivenext.presentation.common.formatDateTime

class BookingAdapter(
    private val onActiveBookingClick: (Booking) -> Unit
) : ListAdapter<Booking, BookingAdapter.BookingViewHolder>(BookingDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding, onActiveBookingClick)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookingViewHolder(
        private val binding: ItemBookingBinding,
        private val onActiveBookingClick: (Booking) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: Booking) {
            val context = binding.root.context
            binding.titleTextView.text = booking.carTitle
            binding.subtitleTextView.text = when (booking.status) {
                BookingStatus.Active ->
                    context.getString(R.string.booking_active_subtitle, booking.startMillis.formatDateTime())
                BookingStatus.Completed ->
                    context.getString(R.string.booking_completed_subtitle, booking.endMillis.formatDate())
                BookingStatus.Cancelled ->
                    context.getString(
                        R.string.booking_cancelled_subtitle,
                        (booking.cancelledAtMillis ?: booking.endMillis).formatDate()
                    )
            }

            val isActive = booking.status == BookingStatus.Active
            binding.root.setOnClickListener(if (isActive) View.OnClickListener { onActiveBookingClick(booking) } else null)
            binding.root.isClickable = isActive
        }
    }

    private object BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean = oldItem == newItem
    }
}
