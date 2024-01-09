package com.github.soulsearching.viewmodel.handler

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.soulsearching.model.settings.SoulSearchingSettings

/**
 * Handler for managing the MainActivityViewModel.
 */
class MainActivityViewModelHandler(
    private val settings: SoulSearchingSettings
) : ViewModelHandler {
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