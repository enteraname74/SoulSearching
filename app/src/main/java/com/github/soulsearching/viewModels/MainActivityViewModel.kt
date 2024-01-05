package com.github.soulsearching.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.soulsearching.classes.utils.SharedPrefUtils

/**
 * ViewModel for managing the main activity.
 */
class MainActivityViewModel : ViewModel() {
    var hasLastPlayedMusicsBeenFetched by mutableStateOf(false)
    var cleanImagesLaunched by mutableStateOf(false)
    var cleanMusicsLaunched by mutableStateOf(false)
    var hasMusicsBeenFetched by mutableStateOf(SharedPrefUtils.hasMusicsBeenFetched())

    var isReadPermissionGranted by mutableStateOf(false)
    var isPostNotificationGranted by mutableStateOf(false)

}