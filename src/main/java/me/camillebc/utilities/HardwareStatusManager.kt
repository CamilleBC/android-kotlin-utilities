package me.camillebc.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager


class HardwareStatusManager(private val context: Activity) {
    enum class InternetStatus { OFFLINE, RESTRICTED, UNRESTRICTED }
    enum class BatteryStatus { DISCHARGING, CHARGING }

    fun getConnectivityStatus(): InternetStatus {
        if (!context.isPermissionGranted(Manifest.permission.ACCESS_NETWORK_STATE)) {
            context.showPermissionsReasonAndRequest(
                "We need to access the network state to check when to download fiction updates.\nPlease accept the request.",
                arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                RC_NETWORK_STATE
            )
        }
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetworkInfo == null) return InternetStatus.OFFLINE
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)) {
                return InternetStatus.UNRESTRICTED
            }
            return InternetStatus.RESTRICTED
        }
        return InternetStatus.OFFLINE
    }

    fun getBatteryStatus(): BatteryStatus {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { intentFilter ->
            context.registerReceiver(null, intentFilter)
        }
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

        if (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL) {
            return BatteryStatus.CHARGING
        }
        return BatteryStatus.DISCHARGING
    }
}