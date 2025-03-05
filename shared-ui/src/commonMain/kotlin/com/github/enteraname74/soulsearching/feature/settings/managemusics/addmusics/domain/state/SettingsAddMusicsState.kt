package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.state

import com.github.enteraname74.soulsearching.features.musicmanager.fetching.SelectableMusicItem

/**
 * State for managing adding musics.
 */
sealed interface SettingsAddMusicsState {
    data object Fetching: SettingsAddMusicsState
    data class SavingSongs(
        val progress: Float,
        val fetchedMusics: List<SelectableMusicItem>,
    ): SettingsAddMusicsState
    data object SongsSaved: SettingsAddMusicsState

    data class Data(
        val fetchedMusics: List<SelectableMusicItem>,
    ): SettingsAddMusicsState
}
