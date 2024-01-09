package com.github.soulsearching.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.soulsearching.model.settings.SoulSearchingSettings

/**
 * ViewModel for managing the main activity.
 */
class MainActivityViewModel(
    private val settings: SoulSearchingSettings
) : ViewModel() {
    var hasLastPlayedMusicsBeenFetched by mutableStateOf(false)
    var cleanImagesLaunched by mutableStateOf(false)
    var cleanMusicsLaunched by mutableStateOf(false)
    var hasMusicsBeenFetched by mutableStateOf(
        settings.getBoolean(
            SoulSearchingSettings.HAS_MUSICS_BEEN_FETCHED_KEY,
            false
        )
    )

    var isReadPermissionGranted by mutableStateOf(false)
    var isPostNotificationGranted by mutableStateOf(false)

}