package com.example.drivenext.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.drivenext.domain.repository.ConnectivityRepository

class ConnectivityRepositoryImpl(private val context: Context) : ConnectivityRepository {

    override fun isInternetAvailable(): Boolean = try {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } catch (e: SecurityException) {
        false
    } catch (e: RuntimeException) {
        false
    }
}
