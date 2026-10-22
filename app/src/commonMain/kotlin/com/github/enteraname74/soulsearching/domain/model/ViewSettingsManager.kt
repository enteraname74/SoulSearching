package com.github.enteraname74.soulsearching.domain.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.util.SoulPlatformUtils
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Manage settings of the application linked to the view.
 */
class ViewSettingsManager(
    private val settingsRepository: SettingsRepository,
    workDispatcher: WorkDispatcher,
) {
    private val scope = CoroutineScope(workDispatcher.dispatcher)

    var isMusicFileModificationOn by mutableStateOf(true)
        private set

    var areMusicsByMonthsShown by mutableStateOf(false)
        private set

    var shouldShowAlbumTrackNumber by mutableStateOf(true)
        private set

    init {
        scope.launch {
            settingsRepository.observeSettings().collect { settings ->
                isMusicFileModificationOn = settings.isMusicFileModificationOn
                areMusicsByMonthsShown = settings.mainPage.areMusicsByMonthsShown
                shouldShowAlbumTrackNumber = settings.album.shouldShowTrackPositionInAlbumView
            }
        }
    }

    val visibleElements: Flow<ElementsVisibility> = settingsRepository.observeMainPage().map { settings ->
        ElementsVisibility(
            isQuickAccessShown = settings.isQuickAccessShown,
            arePlaylistsShown = settings.arePlaylistsShown,
            areCollectionsShown = settings.areCollectionsShown,
            areAlbumsShown = settings.areAlbumsShown,
            areArtistsShown = settings.areArtistsShown,
            areMusicFoldersShown = if (SoulPlatformUtils.platform == SoulPlatform.Web) {
                false
            } else {
                settings.areMusicsByFoldersShown
            },
        )
    }

    /**
     * Active or deactivate the music file modification.
     */
    fun toggleMusicFileModification() {
        scope.launch { settingsRepository.setMusicFileModification(!isMusicFileModificationOn) }
    }

    /**
     * Show or hide the musics by months part in the musics tab.
     */
    fun toggleMusicsByMonthsVisibility() {
        scope.launch { settingsRepository.setMusicsByMonthsShown(!areMusicsByMonthsShown) }
    }

    fun toggleShowAlbumTrackNumber() {
        scope.launch { settingsRepository.setShowTrackPositionInAlbumView(!shouldShowAlbumTrackNumber) }
    }
}
