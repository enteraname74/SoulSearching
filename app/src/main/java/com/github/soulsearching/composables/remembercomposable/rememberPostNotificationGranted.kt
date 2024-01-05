package com.github.soulsearching.composables.remembercomposable

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Retrieve the state of the post notification permission.
 * If the device is below Android 13, the post notification is not necessary.
 */
@Composable
fun rememberPostNotificationGranted(): MutableState<Boolean> {
    val context = LocalContext.current
    return rememberSaveable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }
}