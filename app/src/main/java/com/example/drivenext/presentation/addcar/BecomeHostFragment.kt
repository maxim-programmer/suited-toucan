package com.example.drivenext.presentation.addcar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentBecomeHostBinding

class BecomeHostFragment : Fragment(R.layout.fragment_become_host) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentBecomeHostBinding.bind(view)
        val navController = findNavController()

        binding.backButton.setOnClickListener { navController.popBackStack() }
        binding.startButton.setOnClickListener {
            navController.navigate(R.id.action_becomeHost_to_address)
        }
    }
}
