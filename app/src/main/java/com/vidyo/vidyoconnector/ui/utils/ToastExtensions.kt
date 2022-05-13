package com.vidyo.vidyoconnector.ui.utils

import android.widget.Toast
import androidx.annotation.StringRes
import com.vidyo.vidyoconnector.appContext

private val toast = Toast.makeText(appContext, "", Toast.LENGTH_SHORT)

fun showToast(@StringRes text: Int) {
    showToast(appContext.getString(text))
}

fun showToast(text: CharSequence?) {
    toast.setText(text ?: return)
    toast.show()
}
