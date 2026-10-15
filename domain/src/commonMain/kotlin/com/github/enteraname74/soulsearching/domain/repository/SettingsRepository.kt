package com.github.enteraname74.soulsearching.domain.repository

import com.github.enteraname74.soulsearching.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.domain.model.Release
import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType
import com.github.enteraname74.soulsearching.domain.model.settings.ColorThemeType
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

interface SettingsRepository {
    fun observeSettings(): Flow<SoulSearchingSettings>
    suspend fun getSettings(): SoulSearchingSettings

    fun observePlayer() = observeSettings().map { it.playerSettings }.distinctUntilChanged()
    fun observeColorTheme() = observeSettings().map { it.colorTheme }.distinctUntilChanged()
    fun observeMainPage() = observeSettings().map { it.mainPage }.distinctUntilChanged()
    fun observeRelease() = observeSettings().map { it.release }.distinctUntilChanged()

    suspend fun setHasMusicsBeenFetched(value: Boolean)
    suspend fun setMusicFileModification(value: Boolean)
    suspend fun setMusicSortType(value: SortType)
    suspend fun setMusicSortDirection(value: SortDirection)
    suspend fun setAlbumSortType(value: SortType)
    suspend fun setAlbumSortDirection(value: SortDirection)
    suspend fun setArtistSortType(value: SortType)
    suspend fun setArtistSortDirection(value: SortDirection)
    suspend fun setPlaylistSortType(value: SortType)
    suspend fun setPlaylistSortDirection(value: SortDirection)
    suspend fun setCollectionSortType(value: SortType)
    suspend fun setCollectionSortDirection(value: SortDirection)
    suspend fun setPlayerSwipeEnabled(value: Boolean)
    suspend fun setSoulMixTotalByList(value: Int)
    suspend fun setRewindEnabled(value: Boolean)
    suspend fun setMinimisedSongProgressionShown(value: Boolean)
    suspend fun setPlayerVolume(value: Float)
    suspend fun setRemoteLyricsFetchEnabled(value: Boolean)
    suspend fun setSharedPlayedListMusicUpdateMillis(value: Long)
    suspend fun setColorThemeType(value: ColorThemeType)
    suspend fun setDynamicPlayerTheme(value: Boolean)
    suspend fun setDynamicPlaylistTheme(value: Boolean)
    suspend fun setDynamicOtherViewsTheme(value: Boolean)
    suspend fun setForceDarkTheme(value: Boolean)
    suspend fun setForceLightTheme(value: Boolean)
    suspend fun setUsedTheme(value: SoulSearchingTheme)
    suspend fun setPaletteSeed(value: String)
    suspend fun setQuickAccessShown(value: Boolean)
    suspend fun setPlaylistsShown(value: Boolean)
    suspend fun setCollectionsShown(value: Boolean)
    suspend fun setAlbumsShown(value: Boolean)
    suspend fun setArtistsShown(value: Boolean)
    suspend fun setMusicsByMonthsShown(value: Boolean)
    suspend fun setMusicsByFoldersShown(value: Boolean)
    suspend fun setUsingVerticalAccessBar(value: Boolean)
    suspend fun setInitialTab(value: String)
    suspend fun setArtistCoverFolderRetriever(value: CoverFolderRetriever)
    suspend fun setWindowHeight(value: Float)
    suspend fun setWindowWidth(value: Float)
    suspend fun setFullscreen(value: Boolean)
    suspend fun setFetchReleaseFromGithubEnabled(value: Boolean)
    suspend fun setShowReleaseBottomEnableHint(value: Boolean)
    suspend fun setLatestRelease(value: Release?)
    suspend fun markLatestReleaseViewed(tag: String)
    suspend fun clearLatestRelease()
    suspend fun setShowTrackPositionInAlbumView(value: Boolean)
}
