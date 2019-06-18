package me.camillebc.utilities

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Constants used for Request Codes
 */
const val RC_NETWORK_STATE = 201

/**
 * Extension function to check if permission has been granted
 *
 * @param permission Permission to check
 */
fun Context.isPermissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED

/**
 * Extension function to check if permission has been granted
 *
 * @param grantResult Result to check
 */
fun Context.isPermissionGranted(grantResult: Int): Boolean =
    grantResult == PackageManager.PERMISSION_GRANTED

/**
 * Extension function to check if user has checked "Never ask again" for permission
 *
 * @param permission Permission to check
 */
fun Activity.isPermissionNeverAskAgain(permission: String): Boolean =
    !this.shouldShowRequestPermissionRationale(permission)

/**
 * Extension function to request permissions and show the reason.
 *
 * @param message Message to display the reasons for permissions' request
 * @param permissions Array of permissions to request
 * @param requestCode Request Code to check the activity result
 */
fun Activity.showPermissionsReasonAndRequest(
    message: String,
    permissions: Array<String>,
    requestCode: Int
) {
    AlertDialog.Builder(this).apply {
        setTitle("Permission request")
        setMessage(message)
        setCancelable(true)

        setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            ActivityCompat.requestPermissions(
                this@showPermissionsReasonAndRequest,
                permissions,
                requestCode
            )
            dialog.cancel()
        }

        setNegativeButton(
            "No"
        ) { dialog, _ -> dialog.cancel() }
    }
        .create()
        .show()
}


/**
 * DSL class to handle permission requests.
 *
 * @property activity The activity handling the results
 * @property requestCode Request Code returned by the activity
 * @property expectedRequestCode RequestCode expected for our Permission Request
 * @property permissions Array of permissions that have been requested
 * @property grantResults Array of results from the request permission
 *
 * @property onSuccess Function called on request success
 * @property onNeverAskAgain Function called if user has checked "Never ask again"
 * @property onFailure Function called on request failure
 *
 * @property handle Function that checks the request result and calls the appropriate callback
 */
class PermissionHandler() {
    lateinit var activity: Activity
    lateinit var requestCode: String
    lateinit var expectedRequestCode: String
    lateinit var permissions: Array<String>
    lateinit var grantResults: IntArray

    lateinit var onSuccess: () -> Unit
    lateinit var onFailure: () -> Unit
    lateinit var onNeverAskAgain: () -> Unit

    fun handle() {
        if (requestCode != expectedRequestCode) return

        when {
            grantResults.all { activity.isPermissionGranted(it) } -> onSuccess()
            permissions.any { activity.isPermissionNeverAskAgain(it) } -> onNeverAskAgain()
            else -> onFailure()
        }
    }

    operator fun invoke(block: PermissionHandler.() -> Unit): PermissionHandler = PermissionHandler().apply { block }

}