package me.camillebc.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager


class HardwareStatusManager(private val context: Context) {
    enum class InternetStatus { OFFLINE, RESTRICTED, UNRESTRICTED }
    enum class BatteryStatus { DISCHARGING, CHARGING }

    val isConnected = getConnectivityStatus() != InternetStatus.OFFLINE

    fun getConnectivityStatus(): InternetStatus {
        if (!context.isPermissionGranted(Manifest.permission.ACCESS_NETWORK_STATE)) {
            // TODO("the check should be done in the main app")
//            context.showpermissionsreasonandrequest(
//                "we need to access the network state to check when to download fiction updates.\nplease accept the request.",
//                arrayof(manifest.permission.access_network_state),
//                rc_network_state
//            rc_network_state)
        }
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetworkInfo == null) return InternetStatus.OFFLINE
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)!!
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