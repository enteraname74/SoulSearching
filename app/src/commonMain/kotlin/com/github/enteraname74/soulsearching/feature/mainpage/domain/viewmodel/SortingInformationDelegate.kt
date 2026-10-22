package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType
import com.github.enteraname74.soulsearching.domain.model.SortingInformation
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface SortingInformationDelegate {
    val musicSortingInformation: Flow<SortingInformation>
    val artistSortingInformation: Flow<SortingInformation>
    val albumSortingInformation: Flow<SortingInformation>
    val playlistSortingInformation: Flow<SortingInformation>
    val collectionSortingInformation: Flow<SortingInformation>

    fun setMusicSortType(type: SortType)
    fun setArtistSortType(type: SortType)
    fun setAlbumSortType(type: SortType)
    fun setPlaylistSortType(type: SortType)
    fun setCollectionSortType(type: SortType)

    fun setMusicSortDirection(direction: SortDirection)
    fun setArtistSortDirection(direction: SortDirection)
    fun setAlbumSortDirection(direction: SortDirection)
    fun setPlaylistSortDirection(direction: SortDirection)
    fun setCollectionSortDirection(direction: SortDirection)
}

class SortingInformationDelegateImpl(
    private val settingsRepository: SettingsRepository,
    workDispatcher: WorkDispatcher,
): SortingInformationDelegate {
    private val scope = CoroutineScope(workDispatcher.dispatcher)
    private fun settingsFlow(selector: (SoulSearchingSettings) -> SoulSearchingSettings.SortSetup) =
        settingsRepository.observeSettings().map { settings -> selector(settings).let { SortingInformation(it.type, it.direction) } }

    override val musicSortingInformation = settingsFlow { it.musicSortSetup }
    override val artistSortingInformation = settingsFlow { it.artistSortSetup }
    override val albumSortingInformation = settingsFlow { it.albumSortSetup }
    override val playlistSortingInformation = settingsFlow { it.playlistSortSetup }
    override val collectionSortingInformation = settingsFlow { it.collectionSortSetup }

    override fun setMusicSortType(type: SortType) {
        scope.launch { settingsRepository.setMusicSortType(type) }
    }

    override fun setArtistSortType(type: SortType) {
        scope.launch { settingsRepository.setArtistSortType(type) }
    }

    override fun setAlbumSortType(type: SortType) {
        scope.launch { settingsRepository.setAlbumSortType(type) }
    }

    override fun setPlaylistSortType(type: SortType) {
        scope.launch { settingsRepository.setPlaylistSortType(type) }
    }

    override fun setCollectionSortType(type: SortType) {
        scope.launch { settingsRepository.setCollectionSortType(type) }
    }

    override fun setMusicSortDirection(direction: SortDirection) {
        scope.launch { settingsRepository.setMusicSortDirection(direction) }
    }

    override fun setArtistSortDirection(direction: SortDirection) {
        scope.launch { settingsRepository.setArtistSortDirection(direction) }
    }

    override fun setAlbumSortDirection(direction: SortDirection) {
        scope.launch { settingsRepository.setAlbumSortDirection(direction) }
    }

    override fun setPlaylistSortDirection(direction: SortDirection) {
        scope.launch { settingsRepository.setPlaylistSortDirection(direction) }
    }

    override fun setCollectionSortDirection(direction: SortDirection) {
        scope.launch { settingsRepository.setCollectionSortDirection(direction) }
    }
}
