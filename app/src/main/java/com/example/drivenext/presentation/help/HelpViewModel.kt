package com.example.drivenext.presentation.help

import androidx.lifecycle.ViewModel
import com.example.drivenext.R
import com.example.drivenext.presentation.onboarding.OnboardingPage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

sealed interface HelpEvent {
    data class ShowPage(val index: Int) : HelpEvent
    data object Finished : HelpEvent
}

class HelpViewModel : ViewModel() {

    val pages: List<OnboardingPage> = listOf(
        OnboardingPage(
            R.string.help_title_1,
            R.string.help_description_1,
            R.drawable.img_onboarding_1
        ),
        OnboardingPage(
            R.string.help_title_2,
            R.string.help_description_2,
            R.drawable.img_onboarding_2
        ),
        OnboardingPage(
            R.string.help_title_3,
            R.string.help_description_3,
            R.drawable.img_onboarding_3
        )
    )

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _events = Channel<HelpEvent>(Channel.BUFFERED)
    val events: Flow<HelpEvent> = _events.receiveAsFlow()

    fun onPageSelected(position: Int) {
        _currentPage.value = position
    }

    fun onNextClicked() {
        val page = _currentPage.value
        if (page >= pages.lastIndex) {
            _events.trySend(HelpEvent.Finished)
        } else {
            _events.trySend(HelpEvent.ShowPage(page + 1))
        }
    }

    fun onCloseClicked() {
        _events.trySend(HelpEvent.Finished)
    }
}
