package com.github.enteraname74.soulsearching.domain.model.settings

import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType
import kotlinx.serialization.Serializable

@Serializable
data class CloudSoulSearchingSettings(
    val updatedAtMillis: Long,
    val isMusicFileModificationOn: Boolean,
    val musicSortSetup: SortSetup,
    val albumSortSetup: SortSetup,
    val artistSortSetup: SortSetup,
    val playlistSortSetup: SortSetup,
    val collectionSortSetup: SortSetup,
    val playerSettings: PlayerSettings,
    val colorTheme: ColorTheme,
    val mainPage: MainPage,
    val release: Release,
    val album: Album,
) {
    @Serializable
    data class SortSetup(val type: SortType, val direction: SortDirection)

    @Serializable
    data class PlayerSettings(
        val isPlayerSwipeEnabled: Boolean,
        val soulMixTotalByList: Int,
        val isRewindEnabled: Boolean,
        val isMinimisedSongProgressionShown: Boolean,
        val isRemoteLyricsFetchEnabled: Boolean,
    )

    @Serializable
    data class ColorTheme(
        val type: ColorThemeType,
        val dynamicPlayerTheme: Boolean,
        val dynamicPlaylistTheme: Boolean,
        val dynamicOtherViewsTheme: Boolean,
    )

    @Serializable
    data class MainPage(
        val isQuickAccessTabShown: Boolean,
        val isArtistsTabShown: Boolean,
        val isAlbumsTabShown: Boolean,
        val isPlaylistsTabShown: Boolean,
        val isCollectionsTabShown: Boolean,
        val isFoldersTabShown: Boolean,
        val areMusicsByMonthShown: Boolean,
        val isUsingVerticalAccessBar: Boolean,
        val initialTab: String,
    )

    @Serializable
    data class Release(
        val isFetchReleaseFromGithubEnabled: Boolean,
        val shouldShowReleaseBottomEnableHint: Boolean,
        val latestViewedReleaseTag: String?,
    )

    @Serializable
    data class Album(val shouldShowTrackPositionInAlbumView: Boolean)
}

fun SoulSearchingSettings.toCloud(): CloudSoulSearchingSettings = CloudSoulSearchingSettings(
    updatedAtMillis = updatedAtMillis,
    isMusicFileModificationOn = isMusicFileModificationOn,
    musicSortSetup = musicSortSetup.toCloud(),
    albumSortSetup = albumSortSetup.toCloud(),
    artistSortSetup = artistSortSetup.toCloud(),
    playlistSortSetup = playlistSortSetup.toCloud(),
    collectionSortSetup = collectionSortSetup.toCloud(),
    playerSettings = CloudSoulSearchingSettings.PlayerSettings(
        isPlayerSwipeEnabled = playerSettings.isPlayerSwipeEnabled,
        soulMixTotalByList = playerSettings.soulMixTotalByList,
        isRewindEnabled = playerSettings.isRewindEnabled,
        isMinimisedSongProgressionShown = playerSettings.isMinimisedSongProgressionShown,
        isRemoteLyricsFetchEnabled = playerSettings.isRemoteLyricsFetchEnabled,
    ),
    colorTheme = CloudSoulSearchingSettings.ColorTheme(
        type = colorTheme.type,
        dynamicPlayerTheme = colorTheme.dynamicPlayerTheme,
        dynamicPlaylistTheme = colorTheme.dynamicPlaylistTheme,
        dynamicOtherViewsTheme = colorTheme.dynamicOtherViewsTheme,
    ),
    mainPage = CloudSoulSearchingSettings.MainPage(
        isQuickAccessTabShown = mainPage.isQuickAccessShown,
        isArtistsTabShown = mainPage.areArtistsShown,
        isAlbumsTabShown = mainPage.areAlbumsShown,
        isPlaylistsTabShown = mainPage.arePlaylistsShown,
        isCollectionsTabShown = mainPage.areCollectionsShown,
        isFoldersTabShown = mainPage.areMusicsByFoldersShown,
        areMusicsByMonthShown = mainPage.areMusicsByMonthsShown,
        isUsingVerticalAccessBar = mainPage.isUsingVerticalAccessBar,
        initialTab = mainPage.initialTab,
    ),
    release = CloudSoulSearchingSettings.Release(
        isFetchReleaseFromGithubEnabled = release.isFetchReleaseFromGithubEnabled,
        shouldShowReleaseBottomEnableHint = release.shouldShowReleaseBottomEnableHint,
        latestViewedReleaseTag = release.latestViewedReleaseTag,
    ),
    album = CloudSoulSearchingSettings.Album(album.shouldShowTrackPositionInAlbumView),
)

private fun SoulSearchingSettings.SortSetup.toCloud() = CloudSoulSearchingSettings.SortSetup(type, direction)

fun SoulSearchingSettings.withCloud(settings: CloudSoulSearchingSettings): SoulSearchingSettings = copy(
    updatedAtMillis = settings.updatedAtMillis,
    isMusicFileModificationOn = settings.isMusicFileModificationOn,
    musicSortSetup = settings.musicSortSetup.toLocal(),
    albumSortSetup = settings.albumSortSetup.toLocal(),
    artistSortSetup = settings.artistSortSetup.toLocal(),
    playlistSortSetup = settings.playlistSortSetup.toLocal(),
    collectionSortSetup = settings.collectionSortSetup.toLocal(),
    playerSettings = playerSettings.copy(
        isPlayerSwipeEnabled = settings.playerSettings.isPlayerSwipeEnabled,
        soulMixTotalByList = settings.playerSettings.soulMixTotalByList,
        isRewindEnabled = settings.playerSettings.isRewindEnabled,
        isMinimisedSongProgressionShown = settings.playerSettings.isMinimisedSongProgressionShown,
        isRemoteLyricsFetchEnabled = settings.playerSettings.isRemoteLyricsFetchEnabled,
    ),
    colorTheme = colorTheme.copy(
        type = settings.colorTheme.type,
        dynamicPlayerTheme = settings.colorTheme.dynamicPlayerTheme,
        dynamicPlaylistTheme = settings.colorTheme.dynamicPlaylistTheme,
        dynamicOtherViewsTheme = settings.colorTheme.dynamicOtherViewsTheme,
    ),
    mainPage = mainPage.copy(
        isQuickAccessShown = settings.mainPage.isQuickAccessTabShown,
        areArtistsShown = settings.mainPage.isArtistsTabShown,
        areAlbumsShown = settings.mainPage.isAlbumsTabShown,
        arePlaylistsShown = settings.mainPage.isPlaylistsTabShown,
        areCollectionsShown = settings.mainPage.isCollectionsTabShown,
        areMusicsByFoldersShown = settings.mainPage.isFoldersTabShown,
        areMusicsByMonthsShown = settings.mainPage.areMusicsByMonthShown,
        isUsingVerticalAccessBar = settings.mainPage.isUsingVerticalAccessBar,
        initialTab = settings.mainPage.initialTab,
    ),
    release = release.copy(
        isFetchReleaseFromGithubEnabled = settings.release.isFetchReleaseFromGithubEnabled,
        shouldShowReleaseBottomEnableHint = settings.release.shouldShowReleaseBottomEnableHint,
        latestViewedReleaseTag = settings.release.latestViewedReleaseTag,
    ),
    album = album.copy(shouldShowTrackPositionInAlbumView = settings.album.shouldShowTrackPositionInAlbumView),
)

private fun CloudSoulSearchingSettings.SortSetup.toLocal() = SoulSearchingSettings.SortSetup(type, direction)
