package com.example.drivenext.presentation.onboarding

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentOnboardingPageBinding

class OnboardingPageFragment : Fragment(R.layout.fragment_onboarding_page) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOnboardingPageBinding.bind(view)
        val args = requireArguments()
        binding.imageView.setImageResource(args.getInt(KEY_IMAGE))
        binding.titleTextView.setText(args.getInt(KEY_TITLE))
        binding.descriptionTextView.setText(args.getInt(KEY_DESCRIPTION))
    }

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_IMAGE = "image"

        fun newInstance(page: OnboardingPage) = OnboardingPageFragment().apply {
            arguments = bundleOf(
                KEY_TITLE to page.titleRes,
                KEY_DESCRIPTION to page.descriptionRes,
                KEY_IMAGE to page.imageRes
            )
        }
    }
}
