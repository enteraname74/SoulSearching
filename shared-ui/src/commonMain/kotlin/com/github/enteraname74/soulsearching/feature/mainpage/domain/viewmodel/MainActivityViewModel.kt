package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys

class MainActivityViewModel(
    settings: SoulSearchingSettings,
) : ScreenModel {
    var hasMusicsBeenFetched by mutableStateOf(
        settings.get(SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY)
    )

    var isReadPermissionGranted by mutableStateOf(false)
    var isPostNotificationGranted by mutableStateOf(false)

}