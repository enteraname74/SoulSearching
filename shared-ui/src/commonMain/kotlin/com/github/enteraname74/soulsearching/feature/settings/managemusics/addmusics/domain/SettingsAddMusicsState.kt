package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain

import com.github.enteraname74.soulsearching.features.filemanager.musicfetching.SelectableMusicItem

/**
 * State for managing adding musics.
 */
sealed interface SettingsAddMusicsState {
    data object Fetching: SettingsAddMusicsState
    data class SavingSongs(
        val progress: Float,
    ): SettingsAddMusicsState
    data object SongsSaved: SettingsAddMusicsState

    data class Data(
        val fetchedMusics: List<SelectableMusicItem>,
    ): SettingsAddMusicsState
}
