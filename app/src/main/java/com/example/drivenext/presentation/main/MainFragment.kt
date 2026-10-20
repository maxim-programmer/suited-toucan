package com.example.drivenext.presentation.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentMainBinding
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted

class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: MainViewModel by viewModels { appContainer.viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMainBinding.bind(view)

        binding.logoutButton.setOnClickListener { viewModel.onLogoutClicked() }

        collectWhenStarted(viewModel.events) {
            findNavController().navigate(R.id.action_main_to_welcome)
        }
    }
}
