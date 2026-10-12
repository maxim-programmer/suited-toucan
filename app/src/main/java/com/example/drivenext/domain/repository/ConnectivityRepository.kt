package com.example.drivenext.domain.repository

interface ConnectivityRepository {
    fun isInternetAvailable(): Boolean
}
