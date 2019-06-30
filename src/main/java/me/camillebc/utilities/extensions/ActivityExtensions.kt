package me.camillebc.utilities.extensions

import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.addFragment(frameId: Int, fragment: androidx.fragment.app.Fragment) {
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}

fun AppCompatActivity.removeFragment(fragment: androidx.fragment.app.Fragment) {
    supportFragmentManager.inTransaction { remove(fragment) }
}

fun AppCompatActivity.replaceFragment(
    frameId: Int,
    fragment: androidx.fragment.app.Fragment,
    toBackStack: Boolean = false,
    backStackName: String? = null
) {
    if (toBackStack) {
        supportFragmentManager.inTransaction { replace(frameId, fragment).addToBackStack(backStackName) }
    } else {
        supportFragmentManager.inTransaction { replace(frameId, fragment) }
    }
}
