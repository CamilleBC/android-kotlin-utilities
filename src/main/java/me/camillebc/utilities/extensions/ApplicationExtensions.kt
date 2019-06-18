package com.camillebc.fusy.utilities

import android.app.Application
import android.content.Context.MODE_PRIVATE
import me.camillebc.utilities.BuildConfig

private const val APP_PREF = "AppPreferences"

/**
 * This should be copy/pasted in the main activity or application, as it's relying on the library BuildConfig
 * instead of the app's
 */
fun Application.isFirstLaunch(): Boolean {
    val prefsName = APP_PREF
    val prefVersionCodeKey = "version_code"
    val inexistent = -1

    // Get current version code
    val currentVersionCode = BuildConfig.VERSION_CODE

    // Get saved version code
    val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
    val savedVersionCode = prefs.getInt(prefVersionCodeKey, inexistent)

    // Update the shared preferences with the current version code
    prefs.edit().putInt(prefVersionCodeKey, currentVersionCode).apply()

    // Check for first run or upgrade
    // This is just a normal run
    return when {
        currentVersionCode == savedVersionCode -> false
        currentVersionCode == inexistent -> true // TODO This is a new install (or the user cleared the shared preferences)
        currentVersionCode > savedVersionCode -> true // TODO This is an upgrade
        else -> true
    }

}