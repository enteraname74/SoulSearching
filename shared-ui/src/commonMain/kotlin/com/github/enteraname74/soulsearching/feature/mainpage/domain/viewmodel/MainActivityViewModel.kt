package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.github.enteraname74.domain.model.SoulSearchingSettings

class MainActivityViewModel(
    settings: SoulSearchingSettings
) : ScreenModel {
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