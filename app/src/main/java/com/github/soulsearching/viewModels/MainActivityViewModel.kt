package com.github.soulsearching.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.soulsearching.classes.SharedPrefUtils

/**
 * ViewModel for managing the MainActivity.
 */
class MainActivityViewModel : ViewModel() {
    var hasLastPlayedMusicsBeenFetched by mutableStateOf(false)
    var cleanImagesLaunched by mutableStateOf(false)
    var cleanMusicsLaunched by mutableStateOf(false)
    var hasMusicsBeenFetched by mutableStateOf(SharedPrefUtils.hasMusicsBeenFetched())

    var isReadPermissionGranted: Boolean = false
    var isPostNotificationGranted: Boolean = false

}