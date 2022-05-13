package com.vidyo.vidyoconnector.ui.utils

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun ProgressDialog() {
    Dialog(onDismissRequest = { /*TODO*/ }) {
        CircularProgressIndicator()
    }
}
