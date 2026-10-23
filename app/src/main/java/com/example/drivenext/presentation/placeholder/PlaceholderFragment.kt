package com.example.drivenext.presentation.placeholder

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentPlaceholderBinding

class PlaceholderFragment : Fragment(R.layout.fragment_placeholder) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPlaceholderBinding.bind(view)
        val navController = findNavController()
        val destination = navController.currentDestination

        binding.titleTextView.text = destination?.label
        val subtitle = arguments?.getString(ARG_SUBTITLE).orEmpty()
        binding.subtitleTextView.text = subtitle
        binding.subtitleTextView.isVisible = subtitle.isNotEmpty()
        binding.backButton.setOnClickListener { navController.popBackStack() }
    }

    companion object {
        const val ARG_SUBTITLE = "subtitle"
    }
}
