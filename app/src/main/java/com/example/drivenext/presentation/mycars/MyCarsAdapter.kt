package com.example.drivenext.presentation.mycars

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.drivenext.R
import com.example.drivenext.databinding.ItemMyCarBinding
import com.example.drivenext.domain.model.Car

class MyCarsAdapter(
    private val onDetailsClick: (Car) -> Unit,
    private val onDeleteClick: (Car) -> Unit
) : ListAdapter<Car, MyCarsAdapter.MyCarViewHolder>(MyCarDiffCallback) {

    var deletingId: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyItemRangeChanged(0, itemCount)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCarViewHolder {
        val binding = ItemMyCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyCarViewHolder(binding, onDetailsClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: MyCarViewHolder, position: Int) {
        holder.bind(getItem(position), deletingId)
    }

    class MyCarViewHolder(
        private val binding: ItemMyCarBinding,
        private val onDetailsClick: (Car) -> Unit,
        private val onDeleteClick: (Car) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(car: Car, deletingId: String?) {
            val context = binding.root.context
            binding.modelTextView.text = car.model
            binding.brandTextView.text = if (car.year > 0) {
                context.getString(R.string.my_cars_brand_year, car.brand, car.year)
            } else {
                car.brand
            }
            binding.priceTextView.text = context.getString(R.string.car_price_per_day, car.pricePerDay)
            binding.photosTextView.text =
                context.getString(R.string.my_cars_photos_count, car.gallery.size)
            binding.carImageView.load(car.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_car_placeholder)
                error(R.drawable.ic_car_placeholder)
                fallback(R.drawable.ic_car_placeholder)
            }
            val busy = deletingId != null
            binding.detailsButton.isEnabled = !busy
            binding.deleteButton.isEnabled = !busy
            binding.deleteProgress.visibility =
                if (deletingId == car.id) View.VISIBLE else View.GONE
            binding.detailsButton.setOnClickListener { onDetailsClick(car) }
            binding.deleteButton.setOnClickListener { onDeleteClick(car) }
        }
    }

    private object MyCarDiffCallback : DiffUtil.ItemCallback<Car>() {
        override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean = oldItem == newItem
    }
}