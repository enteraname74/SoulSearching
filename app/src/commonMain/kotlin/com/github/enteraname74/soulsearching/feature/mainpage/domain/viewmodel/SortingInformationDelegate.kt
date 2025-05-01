package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

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

    fun setMusicSortType(type: Int)
    fun setArtistSortType(type: Int)
    fun setAlbumSortType(type: Int)
    fun setPlaylistSortType(type: Int)

    fun setMusicSortDirection(direction: Int)
    fun setArtistSortDirection(direction: Int)
    fun setAlbumSortDirection(direction: Int)
    fun setPlaylistSortDirection(direction: Int)
}

class SortingInformationDelegateImpl(
    val settings: SoulSearchingSettings,
): SortingInformationDelegate {
    override val musicSortingInformation: Flow<SortingInformation> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_MUSICS_TYPE_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_MUSICS_DIRECTION_KEY),
    ) { sortType, sortDirection ->
        SortingInformation(
            type = sortType,
            direction = sortDirection,
        )
    }

    override val artistSortingInformation: Flow<SortingInformation> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_ARTISTS_TYPE_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_ARTISTS_DIRECTION_KEY),
    ) { sortType, sortDirection ->
        SortingInformation(
            type = sortType,
            direction = sortDirection,
        )
    }

    override val albumSortingInformation: Flow<SortingInformation> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_ALBUMS_TYPE_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_ALBUMS_DIRECTION_KEY),
    ) { sortType, sortDirection ->
        SortingInformation(
            type = sortType,
            direction = sortDirection,
        )
    }

    override val playlistSortingInformation: Flow<SortingInformation> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_PLAYLISTS_TYPE_KEY),
        settings.getFlowOn(SoulSearchingSettingsKeys.Sort.SORT_PLAYLISTS_DIRECTION_KEY),
    ) { sortType, sortDirection ->
        SortingInformation(
            type = sortType,
            direction = sortDirection,
        )
    }

    override fun setMusicSortType(type: Int) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_MUSICS_TYPE_KEY.key,
            value = type,
        )
    }

    override fun setArtistSortType(type: Int) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_ARTISTS_TYPE_KEY.key,
            value = type,
        )
    }

    override fun setAlbumSortType(type: Int) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_ALBUMS_TYPE_KEY.key,
            value = type,
        )
    }

    override fun setPlaylistSortType(type: Int) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_PLAYLISTS_TYPE_KEY.key,
            value = type,
        )
    }

    override fun setMusicSortDirection(direction: Int) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_MUSICS_DIRECTION_KEY.key,
            value = direction,
        )
    }

    override fun setArtistSortDirection(direction: Int) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_ARTISTS_DIRECTION_KEY.key,
            value = direction,
        )
    }

    override fun setAlbumSortDirection(direction: Int) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_ALBUMS_DIRECTION_KEY.key,
            value = direction,
        )
    }

    override fun setPlaylistSortDirection(direction: Int) {
        settings.set(
            key = SoulSearchingSettingsKeys.Sort.SORT_PLAYLISTS_DIRECTION_KEY.key,
            value = direction,
        )
    }
}