package com.example.drivenext.presentation.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingPagerAdapter(
    fragment: Fragment,
    private val pages: List<OnboardingPage>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment =
        OnboardingPageFragment.newInstance(pages[position])
}
