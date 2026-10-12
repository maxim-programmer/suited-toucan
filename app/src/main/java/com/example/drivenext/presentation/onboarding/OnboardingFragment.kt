package com.example.drivenext.presentation.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentOnboardingBinding
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val viewModel: OnboardingViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboardingBinding.bind(view)

        binding.viewPager.adapter = OnboardingPagerAdapter(this, viewModel.pages)
        binding.pageIndicator.count = viewModel.pages.size

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.onPageSelected(position)
            }
        })

        binding.skipButton.setOnClickListener { viewModel.onSkipClicked() }
        binding.nextButton.setOnClickListener { viewModel.onNextClicked() }

        collectWhenStarted(viewModel.currentPage) { page ->
            binding.pageIndicator.selected = page
            binding.nextButton.setText(
                if (page == viewModel.pages.lastIndex) R.string.onboarding_start
                else R.string.onboarding_next
            )
        }

        collectWhenStarted(viewModel.events) { event ->
            when (event) {
                is OnboardingEvent.ShowPage -> binding.viewPager.setCurrentItem(event.index, true)
                OnboardingEvent.Finished -> findNavController().navigate(R.id.action_onboarding_to_auth)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
