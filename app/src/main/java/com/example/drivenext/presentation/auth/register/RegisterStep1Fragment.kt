package com.example.drivenext.presentation.auth.register

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentRegisterStep1Binding
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted

class RegisterStep1Fragment : Fragment(R.layout.fragment_register_step1) {

    private val viewModel: RegistrationViewModel by navGraphViewModels(R.id.registration_graph) {
        appContainer.viewModelFactory
    }

    private var _binding: FragmentRegisterStep1Binding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterStep1Binding.bind(view)

        val form = viewModel.form.value
        binding.emailEditText.setText(form.email)
        binding.passwordEditText.setText(form.password)
        binding.repeatPasswordEditText.setText(form.repeatPassword)
        binding.agreementCheckBox.isChecked = form.agreed

        binding.emailEditText.doAfterTextChanged { viewModel.onEmailChanged(it?.toString().orEmpty()) }
        binding.passwordEditText.doAfterTextChanged { viewModel.onPasswordChanged(it?.toString().orEmpty()) }
        binding.repeatPasswordEditText.doAfterTextChanged {
            viewModel.onRepeatPasswordChanged(it?.toString().orEmpty())
        }
        binding.agreementCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onAgreementChanged(isChecked)
        }

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.nextButton.setOnClickListener { viewModel.onStep1NextClicked() }

        collectWhenStarted(viewModel.errors) { errors ->
            binding.emailLayout.error = errors.email?.let { getString(it) }
            binding.passwordLayout.error = errors.password?.let { getString(it) }
            binding.repeatPasswordLayout.error = errors.repeatPassword?.let { getString(it) }
            binding.agreementErrorTextView.apply {
                visibility = if (errors.agreement != null) View.VISIBLE else View.GONE
                text = errors.agreement?.let { getString(it) }.orEmpty()
            }
        }

        collectWhenStarted(viewModel.events) { event ->
            if (event == RegistrationEvent.OpenStep2) {
                findNavController().navigate(R.id.action_step1_to_step2)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
