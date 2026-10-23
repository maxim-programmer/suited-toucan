package com.example.drivenext.presentation.rental

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentRentalSuccessBinding

class RentalSuccessFragment : Fragment(R.layout.fragment_rental_success) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRentalSuccessBinding.bind(view)
        val navController = findNavController()

        binding.homeButton.setOnClickListener {
            navController.popBackStack(R.id.mainFragment, false)
        }

        binding.bookingsTextView.setOnClickListener {
            val options = NavOptions.Builder()
                .setPopUpTo(R.id.mainFragment, false)
                .build()
            navController.navigate(R.id.bookingsFragment, null, options)
        }
    }
}
