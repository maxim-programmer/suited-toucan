package com.example.drivenext.presentation.details

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.drivenext.R
import com.example.drivenext.databinding.ItemCarPhotoBinding

class CarPhotoPagerAdapter : ListAdapter<String, CarPhotoPagerAdapter.PhotoViewHolder>(PhotoDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemCarPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PhotoViewHolder(private val binding: ItemCarPhotoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String) {
            binding.photoImageView.scaleType =
                if (url.isBlank()) ImageView.ScaleType.FIT_CENTER else ImageView.ScaleType.CENTER_CROP
            binding.photoImageView.load(url) {
                crossfade(true)
                placeholder(R.drawable.ic_car_placeholder)
                error(R.drawable.ic_car_placeholder)
                fallback(R.drawable.ic_car_placeholder)
            }
        }
    }

    private object PhotoDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }
}