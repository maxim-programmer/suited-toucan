package com.example.drivenext.presentation.help

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentHelpBinding
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.onboarding.OnboardingPagerAdapter

class HelpFragment : Fragment(R.layout.fragment_help) {

    private val viewModel: HelpViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.onPageSelected(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHelpBinding.bind(view)

        binding.viewPager.adapter = OnboardingPagerAdapter(this, viewModel.pages)
        binding.pageIndicator.count = viewModel.pages.size
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)

        binding.closeButton.setOnClickListener { viewModel.onCloseClicked() }
        binding.nextButton.setOnClickListener { viewModel.onNextClicked() }

        collectWhenStarted(viewModel.currentPage) { page ->
            binding.pageIndicator.selected = page
            binding.nextButton.setText(
                if (page == viewModel.pages.lastIndex) R.string.help_done else R.string.help_next
            )
        }

        collectWhenStarted(viewModel.events) { event ->
            when (event) {
                is HelpEvent.ShowPage -> binding.viewPager.setCurrentItem(event.index, true)
                HelpEvent.Finished -> findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        _binding = null
        super.onDestroyView()
    }
}
