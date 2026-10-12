package com.example.drivenext.presentation.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingPage(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int
)
