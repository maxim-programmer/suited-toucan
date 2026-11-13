package com.example.drivenext.presentation.addcar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentAddCarSuccessBinding

class AddCarSuccessFragment : Fragment(R.layout.fragment_add_car_success) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddCarSuccessBinding.bind(view)

        binding.homeButton.setOnClickListener {
            findNavController().popBackStack(R.id.mainFragment, false)
        }
    }
}
