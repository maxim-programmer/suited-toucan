package com.example.drivenext.presentation.auth.login

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentLoginBinding
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.common.showErrorDialog
import com.example.drivenext.presentation.common.showInfoDialog

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        val initial = viewModel.state.value
        binding.emailEditText.setText(initial.email)
        binding.passwordEditText.setText(initial.password)

        binding.emailEditText.doAfterTextChanged { viewModel.onEmailChanged(it?.toString().orEmpty()) }
        binding.passwordEditText.doAfterTextChanged { viewModel.onPasswordChanged(it?.toString().orEmpty()) }

        binding.loginButton.setOnClickListener { viewModel.onLoginClicked() }
        binding.googleButton.setOnClickListener { viewModel.onGoogleClicked() }
        binding.forgotPasswordButton.setOnClickListener { viewModel.onForgotPasswordClicked() }
        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_registration)
        }

        collectWhenStarted(viewModel.state) { state ->
            binding.loginButton.isEnabled = state.isLoginEnabled
            binding.googleButton.isEnabled = !state.isLoading
            binding.forgotPasswordButton.isEnabled = !state.isLoading
            binding.registerButton.isEnabled = !state.isLoading
            binding.progressIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            binding.emailLayout.error = state.emailError?.let { getString(it) }
        }

        collectWhenStarted(viewModel.events) { event ->
            when (event) {
                LoginEvent.NavigateToMain -> findNavController().navigate(R.id.action_login_to_main)
                is LoginEvent.ShowError -> showErrorDialog(event.message)
                is LoginEvent.ShowInfo -> showInfoDialog(event.titleRes, event.messageRes)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
