package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.soulsearching.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.domain.model.Release
import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType
import com.github.enteraname74.soulsearching.domain.model.settings.ColorThemeType
import com.github.enteraname74.soulsearching.domain.model.settings.CloudSoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingTheme
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.util.DateUtils
import com.github.enteraname74.soulsearching.repository.datasource.SettingsLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.SettingsRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max

class SettingsRepositoryImpl(
    private val localDataSource: SettingsLocalDataSource,
    private val remoteDataSource: SettingsRemoteDataSource,
) : SettingsRepository {
    private val updateMutex = Mutex()

    override fun observeSettings(): Flow<SoulSearchingSettings> = localDataSource.observe()
    override suspend fun getSettings(): SoulSearchingSettings = localDataSource.observe().first()
    override fun observeCloudSettings(): Flow<CloudSoulSearchingSettings> = localDataSource.observeCloud()
    override suspend fun getCloudSettingsUpdatedAfter(lastUpdateAt: Long?): CloudSoulSearchingSettings? =
        localDataSource.getCloudUpdatedAfter(lastUpdateAt)
    override suspend fun fetchCloudSettings(lastUpdateAt: Long?): CloudSoulSearchingSettings? =
        remoteDataSource.get(lastUpdateAt)
    override suspend fun upsertCloudSettings(settings: CloudSoulSearchingSettings): CloudSoulSearchingSettings =
        remoteDataSource.upsert(settings)
    override suspend fun applyCloudSettings(
        settings: CloudSoulSearchingSettings,
        lastSettingsSyncMillis: Long,
    ) = updateMutex.withLock {
        localDataSource.applyCloud(settings, lastSettingsSyncMillis)
    }

    private suspend fun update(
        shared: Boolean = false,
        transform: (SoulSearchingSettings) -> SoulSearchingSettings,
    ) {
        updateMutex.withLock {
            val current = getSettings()
            val updated = transform(current).let {
                if (shared) it.copy(updatedAtMillis = max(DateUtils.now(), current.updatedAtMillis + 1))
                else it
            }
            localDataSource.upsert(updated)
        }
    }

    override suspend fun setHasMusicsBeenFetched(value: Boolean) = update { it.copy(hasMusicsBeenFetched = value) }
    override suspend fun setMusicFileModification(value: Boolean) = update(shared = true) { it.copy(isMusicFileModificationOn = value) }
    override suspend fun setMusicSortType(value: SortType) = update(shared = true) { it.copy(musicSortSetup = it.musicSortSetup.copy(type = value)) }
    override suspend fun setMusicSortDirection(value: SortDirection) = update(shared = true) { it.copy(musicSortSetup = it.musicSortSetup.copy(direction = value)) }
    override suspend fun setAlbumSortType(value: SortType) = update(shared = true) { it.copy(albumSortSetup = it.albumSortSetup.copy(type = value)) }
    override suspend fun setAlbumSortDirection(value: SortDirection) = update(shared = true) { it.copy(albumSortSetup = it.albumSortSetup.copy(direction = value)) }
    override suspend fun setArtistSortType(value: SortType) = update(shared = true) { it.copy(artistSortSetup = it.artistSortSetup.copy(type = value)) }
    override suspend fun setArtistSortDirection(value: SortDirection) = update(shared = true) { it.copy(artistSortSetup = it.artistSortSetup.copy(direction = value)) }
    override suspend fun setPlaylistSortType(value: SortType) = update(shared = true) { it.copy(playlistSortSetup = it.playlistSortSetup.copy(type = value)) }
    override suspend fun setPlaylistSortDirection(value: SortDirection) = update(shared = true) { it.copy(playlistSortSetup = it.playlistSortSetup.copy(direction = value)) }
    override suspend fun setCollectionSortType(value: SortType) = update(shared = true) { it.copy(collectionSortSetup = it.collectionSortSetup.copy(type = value)) }
    override suspend fun setCollectionSortDirection(value: SortDirection) = update(shared = true) { it.copy(collectionSortSetup = it.collectionSortSetup.copy(direction = value)) }
    override suspend fun setPlayerSwipeEnabled(value: Boolean) = update(shared = true) { it.copy(playerSettings = it.playerSettings.copy(isPlayerSwipeEnabled = value)) }
    override suspend fun setSoulMixTotalByList(value: Int) = update(shared = true) { it.copy(playerSettings = it.playerSettings.copy(soulMixTotalByList = value)) }
    override suspend fun setRewindEnabled(value: Boolean) = update(shared = true) { it.copy(playerSettings = it.playerSettings.copy(isRewindEnabled = value)) }
    override suspend fun setMinimisedSongProgressionShown(value: Boolean) = update(shared = true) { it.copy(playerSettings = it.playerSettings.copy(isMinimisedSongProgressionShown = value)) }
    override suspend fun setPlayerVolume(value: Float) = update { it.copy(playerSettings = it.playerSettings.copy(playerVolume = value)) }
    override suspend fun setRemoteLyricsFetchEnabled(value: Boolean) = update(shared = true) { it.copy(playerSettings = it.playerSettings.copy(isRemoteLyricsFetchEnabled = value)) }
    override suspend fun setSharedPlayedListMusicUpdateMillis(value: Long) = update { it.copy(playerSettings = it.playerSettings.copy(sharedPlayedListMusicUpdateMillis = value)) }
    override suspend fun setColorThemeType(value: ColorThemeType) = update(shared = true) { it.copy(colorTheme = it.colorTheme.copy(type = value)) }
    override suspend fun setDynamicPlayerTheme(value: Boolean) = update(shared = true) { it.copy(colorTheme = it.colorTheme.copy(dynamicPlayerTheme = value)) }
    override suspend fun setDynamicPlaylistTheme(value: Boolean) = update(shared = true) { it.copy(colorTheme = it.colorTheme.copy(dynamicPlaylistTheme = value)) }
    override suspend fun setDynamicOtherViewsTheme(value: Boolean) = update(shared = true) { it.copy(colorTheme = it.colorTheme.copy(dynamicOtherViewsTheme = value)) }
    override suspend fun setForceDarkTheme(value: Boolean) = update { it.copy(colorTheme = it.colorTheme.copy(forceDarkTheme = value)) }
    override suspend fun setForceLightTheme(value: Boolean) = update { it.copy(colorTheme = it.colorTheme.copy(forceLightTheme = value)) }
    override suspend fun setUsedTheme(value: SoulSearchingTheme) = update { it.copy(colorTheme = it.colorTheme.copy(usedTheme = value)) }
    override suspend fun setPaletteSeed(value: String) = update { it.copy(colorTheme = it.colorTheme.copy(paletteSeed = value)) }
    override suspend fun setQuickAccessShown(value: Boolean) = update(shared = true) { it.copy(mainPage = it.mainPage.copy(isQuickAccessShown = value)) }
    override suspend fun setPlaylistsShown(value: Boolean) = update(shared = true) { it.copy(mainPage = it.mainPage.copy(arePlaylistsShown = value)) }
    override suspend fun setCollectionsShown(value: Boolean) = update(shared = true) { it.copy(mainPage = it.mainPage.copy(areCollectionsShown = value)) }
    override suspend fun setAlbumsShown(value: Boolean) = update(shared = true) { it.copy(mainPage = it.mainPage.copy(areAlbumsShown = value)) }
    override suspend fun setArtistsShown(value: Boolean) = update(shared = true) { it.copy(mainPage = it.mainPage.copy(areArtistsShown = value)) }
    override suspend fun setMusicsByMonthsShown(value: Boolean) = update(shared = true) { it.copy(mainPage = it.mainPage.copy(areMusicsByMonthsShown = value)) }
    override suspend fun setMusicsByFoldersShown(value: Boolean) = update(shared = true) { it.copy(mainPage = it.mainPage.copy(areMusicsByFoldersShown = value)) }
    override suspend fun setUsingVerticalAccessBar(value: Boolean) = update(shared = true) { it.copy(mainPage = it.mainPage.copy(isUsingVerticalAccessBar = value)) }
    override suspend fun setInitialTab(value: String) = update(shared = true) { it.copy(mainPage = it.mainPage.copy(initialTab = value)) }
    override suspend fun setArtistCoverFolderRetriever(value: CoverFolderRetriever) = update { it.copy(cover = it.cover.copy(artistCoverFolderRetriever = value)) }
    override suspend fun setWindowHeight(value: Float) = update { it.copy(desktop = it.desktop.copy(windowHeight = value)) }
    override suspend fun setWindowWidth(value: Float) = update { it.copy(desktop = it.desktop.copy(windowWidth = value)) }
    override suspend fun setFullscreen(value: Boolean) = update { it.copy(desktop = it.desktop.copy(isFullscreen = value)) }
    override suspend fun setFetchReleaseFromGithubEnabled(value: Boolean) = update(shared = true) { it.copy(release = it.release.copy(isFetchReleaseFromGithubEnabled = value)) }
    override suspend fun setShowReleaseBottomEnableHint(value: Boolean) = update(shared = true) { it.copy(release = it.release.copy(shouldShowReleaseBottomEnableHint = value)) }
    override suspend fun setLatestRelease(value: Release?) = update { it.copy(release = it.release.copy(latestRelease = value)) }
    override suspend fun markLatestReleaseViewed(tag: String) = update(shared = true) { it.copy(release = it.release.copy(latestViewedReleaseTag = tag)) }
    override suspend fun clearLatestRelease() = update(shared = true) { it.copy(release = it.release.copy(latestRelease = null, latestViewedReleaseTag = null)) }
    override suspend fun setShowTrackPositionInAlbumView(value: Boolean) = update(shared = true) { it.copy(album = it.album.copy(shouldShowTrackPositionInAlbumView = value)) }
}
