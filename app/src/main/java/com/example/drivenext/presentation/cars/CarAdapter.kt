package com.example.drivenext.presentation.cars

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.drivenext.R
import com.example.drivenext.databinding.ItemCarBinding
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.model.FuelType
import com.example.drivenext.domain.model.Transmission

class CarAdapter(
    private val onBookClick: (Car) -> Unit,
    private val onDetailsClick: (Car) -> Unit
) : ListAdapter<Car, CarAdapter.CarViewHolder>(CarDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding, onBookClick, onDetailsClick)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CarViewHolder(
        private val binding: ItemCarBinding,
        private val onBookClick: (Car) -> Unit,
        private val onDetailsClick: (Car) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(car: Car) {
            val context = binding.root.context
            binding.modelTextView.text = car.model
            binding.brandTextView.text = car.brand
            binding.priceTextView.text = context.getString(R.string.car_price, car.pricePerDay)
            binding.transmissionTextView.setText(transmissionLabel(car.transmission))
            binding.fuelTextView.setText(fuelLabel(car.fuelType))
            binding.carImageView.load(car.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_car_placeholder)
                error(R.drawable.ic_car_placeholder)
                fallback(R.drawable.ic_car_placeholder)
            }
            binding.bookButton.setOnClickListener { onBookClick(car) }
            binding.detailsButton.setOnClickListener { onDetailsClick(car) }
        }

        private fun transmissionLabel(transmission: Transmission): Int = when (transmission) {
            Transmission.Automatic -> R.string.transmission_automatic
            Transmission.Manual -> R.string.transmission_manual
        }

        private fun fuelLabel(fuelType: FuelType): Int = when (fuelType) {
            FuelType.Petrol -> R.string.fuel_petrol
            FuelType.Diesel -> R.string.fuel_diesel
            FuelType.Electric -> R.string.fuel_electric
            FuelType.Hybrid -> R.string.fuel_hybrid
        }
    }

    private object CarDiffCallback : DiffUtil.ItemCallback<Car>() {
        override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean = oldItem == newItem
    }
}
