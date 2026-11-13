package com.example.drivenext.presentation.addcar

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentAddCarAddressBinding
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted

class AddCarAddressFragment : Fragment(R.layout.fragment_add_car_address) {

    private val viewModel: AddCarViewModel by navGraphViewModels(R.id.add_car_graph) {
        appContainer.viewModelFactory
    }

    private var _binding: FragmentAddCarAddressBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddCarAddressBinding.bind(view)

        binding.addressEditText.setText(viewModel.form.value.address)
        binding.addressEditText.doAfterTextChanged {
            viewModel.onAddressChanged(it?.toString().orEmpty())
        }
        binding.addressEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onAddressConfirmed()
                true
            } else {
                false
            }
        }

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.nextButton.setOnClickListener { viewModel.onAddressConfirmed() }

        collectWhenStarted(viewModel.form) { current ->
            binding.nextButton.isEnabled = current.isAddressFilled
        }

        collectWhenStarted(viewModel.events) { event ->
            if (event == AddCarEvent.OpenDetails) {
                findNavController().navigate(R.id.action_address_to_details)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
