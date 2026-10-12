package com.example.drivenext.presentation.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.drivenext.DriveNextApp
import com.example.drivenext.di.AppContainer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

val Fragment.appContainer: AppContainer
    get() = (requireActivity().application as DriveNextApp).container

fun <T> Fragment.collectWhenStarted(flow: Flow<T>, action: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { action(it) }
        }
    }
}
