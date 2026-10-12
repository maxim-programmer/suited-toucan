package com.example.drivenext.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.R
import com.example.drivenext.domain.usecase.CompleteOnboardingUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface OnboardingEvent {
    data class ShowPage(val index: Int) : OnboardingEvent
    data object Finished : OnboardingEvent
}

class OnboardingViewModel(
    private val completeOnboarding: CompleteOnboardingUseCase
) : ViewModel() {

    val pages: List<OnboardingPage> = listOf(
        OnboardingPage(
            R.string.onboarding_title_1,
            R.string.onboarding_description_1,
            R.drawable.img_onboarding_1
        ),
        OnboardingPage(
            R.string.onboarding_title_2,
            R.string.onboarding_description_2,
            R.drawable.img_onboarding_2
        ),
        OnboardingPage(
            R.string.onboarding_title_3,
            R.string.onboarding_description_3,
            R.drawable.img_onboarding_3
        )
    )

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _events = Channel<OnboardingEvent>(Channel.BUFFERED)
    val events: Flow<OnboardingEvent> = _events.receiveAsFlow()

    fun onPageSelected(position: Int) {
        _currentPage.value = position
    }

    fun onNextClicked() {
        val page = _currentPage.value
        if (page >= pages.lastIndex) {
            finish()
        } else {
            _events.trySend(OnboardingEvent.ShowPage(page + 1))
        }
    }

    fun onSkipClicked() {
        finish()
    }

    private fun finish() {
        viewModelScope.launch {
            completeOnboarding()
            _events.send(OnboardingEvent.Finished)
        }
    }
}
