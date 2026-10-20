package com.example.drivenext.presentation.auth.register

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentRegisterStep2Binding
import com.example.drivenext.domain.model.Gender
import com.example.drivenext.domain.validation.DateFormats
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.common.showDatePicker

class RegisterStep2Fragment : Fragment(R.layout.fragment_register_step2) {

    private val viewModel: RegistrationViewModel by navGraphViewModels(R.id.registration_graph) {
        appContainer.viewModelFactory
    }

    private var _binding: FragmentRegisterStep2Binding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterStep2Binding.bind(view)

        val form = viewModel.form.value
        binding.lastNameEditText.setText(form.lastName)
        binding.firstNameEditText.setText(form.firstName)
        binding.middleNameEditText.setText(form.middleName)
        binding.birthDateEditText.setText(form.birthDate)
        when (form.gender) {
            Gender.Male -> binding.maleRadioButton.isChecked = true
            Gender.Female -> binding.femaleRadioButton.isChecked = true
            null -> Unit
        }

        binding.lastNameEditText.doAfterTextChanged { viewModel.onLastNameChanged(it?.toString().orEmpty()) }
        binding.firstNameEditText.doAfterTextChanged { viewModel.onFirstNameChanged(it?.toString().orEmpty()) }
        binding.middleNameEditText.doAfterTextChanged { viewModel.onMiddleNameChanged(it?.toString().orEmpty()) }
        binding.birthDateEditText.doAfterTextChanged { viewModel.onBirthDateChanged(it?.toString().orEmpty()) }

        binding.genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.maleRadioButton -> viewModel.onGenderChanged(Gender.Male)
                R.id.femaleRadioButton -> viewModel.onGenderChanged(Gender.Female)
            }
        }

        val openPicker = {
            showDatePicker(R.string.picker_birth_date_title, DateFormats.BIRTH_DATE_PATTERN) {
                binding.birthDateEditText.setText(it)
            }
        }
        binding.birthDateEditText.setOnClickListener { openPicker() }
        binding.birthDateLayout.setStartIconOnClickListener { openPicker() }

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.nextButton.setOnClickListener { viewModel.onStep2NextClicked() }

        collectWhenStarted(viewModel.form) { current ->
            binding.nextButton.isEnabled = current.isStep2Filled
        }

        collectWhenStarted(viewModel.errors) { errors ->
            binding.birthDateLayout.error = errors.birthDate?.let { getString(it) }
            binding.requiredErrorTextView.apply {
                visibility = if (errors.requiredFields != null) View.VISIBLE else View.GONE
                text = errors.requiredFields?.let { getString(it) }.orEmpty()
            }
        }

        collectWhenStarted(viewModel.events) { event ->
            if (event == RegistrationEvent.OpenStep3) {
                findNavController().navigate(R.id.action_step2_to_step3)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
