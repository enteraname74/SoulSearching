package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.SortingInformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

interface SortingInformationDelegate {
    val musicSortingInformation: Flow<SortingInformation>
    val artistSortingInformation: Flow<SortingInformation>
    val albumSortingInformation: Flow<SortingInformation>
    val playlistSortingInformation: Flow<SortingInformation>

    fun setMusicSortType(type: SortType)
    fun setArtistSortType(type: SortType)
    fun setAlbumSortType(type: SortType)
    fun setPlaylistSortType(type: SortType)

    fun setMusicSortDirection(direction: SortDirection)
    fun setArtistSortDirection(direction: SortDirection)
    fun setAlbumSortDirection(direction: SortDirection)
    fun setPlaylistSortDirection(direction: SortDirection)
}

class SortingInformationDelegateImpl(
    val settings: SoulSearchingSettings,
): SortingInformationDelegate {
    override val musicSortingInformation: Flow<SortingInformation> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_MUSICS_TYPE_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_MUSICS_DIRECTION_KEY),
    ) { sortType, sortDirection ->
        SortingInformation(
            type = SortType.from(sortType) ?: SortType.DEFAULT,
            direction = SortDirection.from(sortDirection) ?: SortDirection.DEFAULT,
        )
    }

    override val artistSortingInformation: Flow<SortingInformation> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_ARTISTS_TYPE_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_ARTISTS_DIRECTION_KEY),
    ) { sortType, sortDirection ->
        SortingInformation(
            type = SortType.from(sortType) ?: SortType.DEFAULT,
            direction = SortDirection.from(sortDirection) ?: SortDirection.DEFAULT,
        )
    }

    override val albumSortingInformation: Flow<SortingInformation> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_ALBUMS_TYPE_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_ALBUMS_DIRECTION_KEY),
    ) { sortType, sortDirection ->
        SortingInformation(
            type = SortType.from(sortType) ?: SortType.DEFAULT,
            direction = SortDirection.from(sortDirection) ?: SortDirection.DEFAULT,
        )
    }

    override val playlistSortingInformation: Flow<SortingInformation> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_PLAYLISTS_TYPE_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_PLAYLISTS_DIRECTION_KEY),
    ) { sortType, sortDirection ->
        SortingInformation(
            type = SortType.from(sortType) ?: SortType.DEFAULT,
            direction = SortDirection.from(sortDirection) ?: SortDirection.DEFAULT,
        )
    }

    override fun setMusicSortType(type: SortType) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_MUSICS_TYPE_KEY.key,
            value = type.value,
        )
    }

    override fun setArtistSortType(type: SortType) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_ARTISTS_TYPE_KEY.key,
            value = type.value,
        )
    }

    override fun setAlbumSortType(type: SortType) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_ALBUMS_TYPE_KEY.key,
            value = type.value,
        )
    }

    override fun setPlaylistSortType(type: SortType) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_PLAYLISTS_TYPE_KEY.key,
            value = type.value,
        )
    }

    override fun setMusicSortDirection(direction: SortDirection) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_MUSICS_DIRECTION_KEY.key,
            value = direction.value,
        )
    }

    override fun setArtistSortDirection(direction: SortDirection) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_ARTISTS_DIRECTION_KEY.key,
            value = direction.value,
        )
    }

    override fun setAlbumSortDirection(direction: SortDirection) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_ALBUMS_DIRECTION_KEY.key,
            value = direction.value,
        )
    }

    override fun setPlaylistSortDirection(direction: SortDirection) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_PLAYLISTS_DIRECTION_KEY.key,
            value = direction.value,
        )
    }
}