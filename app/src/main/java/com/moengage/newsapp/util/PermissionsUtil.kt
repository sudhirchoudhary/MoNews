package com.moengage.newsapp.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.moengage.newsapp.R

fun Context.hasPermissions(permissions: Array<String>): Boolean = permissions.all {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

inline fun Activity.requestPermissionsIfNotGranted(
    permissions: Array<String>,
    ifAllGranted: () -> Unit
) {
    if (!hasPermissions(permissions)) {
        showAlertDialog(
            resources.getString(R.string.permission_required),
            resources.getString(R.string.permission_desc),
            "Grant",
            "Decline",
            onNegativeBtnClicked = {
                it.dismiss()
            }
        ) {
            it.dismiss()
            ActivityCompat.requestPermissions(this, permissions, 101)
        }
    } else {
        ifAllGranted()
    }
}

fun Context.showAlertDialog(
    title: String,
    msg: String,
    positiveBtnText: String,
    negativeButtonText: String = "",
    isCancelable: Boolean = false,
    onNegativeBtnClicked: (DialogInterface) -> Unit = {},
    onPositiveBtnClicked: (DialogInterface) -> Unit = {}
) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(msg)
    builder.setCancelable(isCancelable)
    builder.setPositiveButton(positiveBtnText) { dialog, _ ->
        onPositiveBtnClicked(dialog)
    }
    if (negativeButtonText.isNotEmpty()) {
        builder.setNegativeButton(negativeButtonText) { dialog, _ ->
            onNegativeBtnClicked(dialog)
        }
    }
    builder.show()
}