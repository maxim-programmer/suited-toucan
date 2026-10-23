package com.example.drivenext.presentation.common

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.drivenext.R

fun NavController.navigateToTab(@IdRes tabId: Int) {
    if (currentDestination?.id == tabId) return
    val options = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setPopUpTo(R.id.mainFragment, false)
        .build()
    navigate(tabId, null, options)
}
