package com.github.enteraname74.localdb.model

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.github.enteraname74.soulsearching.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.domain.model.Release
import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType
import com.github.enteraname74.soulsearching.domain.model.settings.ColorThemeType
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingTheme

@Entity
data class RoomSoulSearchingSettings(
    @PrimaryKey val id: String = Id,
    val hasMusicsBeenFetched: Boolean = false,
    val isMusicFileModificationOn: Boolean = true,
    @Embedded(prefix = "musicSort_") val musicSort: RoomSortSetup = RoomSortSetup(),
    @Embedded(prefix = "albumSort_") val albumSort: RoomSortSetup = RoomSortSetup(),
    @Embedded(prefix = "artistSort_") val artistSort: RoomSortSetup = RoomSortSetup(),
    @Embedded(prefix = "playlistSort_") val playlistSort: RoomSortSetup = RoomSortSetup(),
    @Embedded(prefix = "collectionSort_") val collectionSort: RoomSortSetup = RoomSortSetup(),
    @Embedded(prefix = "player_") val player: RoomPlayerSettings = RoomPlayerSettings(),
    @Embedded(prefix = "colorTheme_") val colorTheme: RoomColorThemeSettings = RoomColorThemeSettings(),
    @Embedded(prefix = "mainPage_") val mainPage: RoomMainPageSettings = RoomMainPageSettings(),
    @Embedded(prefix = "cover_") val cover: RoomCoverSettings = RoomCoverSettings(),
    @Embedded(prefix = "desktop_") val desktop: RoomDesktopSettings = RoomDesktopSettings(),
    @Embedded(prefix = "release_") val release: RoomReleaseSettings = RoomReleaseSettings(),
    @Embedded(prefix = "album_") val album: RoomAlbumSettings = RoomAlbumSettings(),
) {
    companion object { const val Id = "SoulSearchingSettings" }

    fun toDomain() = SoulSearchingSettings(
        hasMusicsBeenFetched, isMusicFileModificationOn, musicSort.toDomain(), albumSort.toDomain(),
        artistSort.toDomain(), playlistSort.toDomain(), collectionSort.toDomain(), player.toDomain(),
        colorTheme.toDomain(), mainPage.toDomain(), cover.toDomain(), desktop.toDomain(),
        release.toDomain(), album.toDomain(),
    )
}

data class RoomSortSetup(val type: SortType = SortType.DEFAULT, val direction: SortDirection = SortDirection.DEFAULT) {
    fun toDomain() = SoulSearchingSettings.SortSetup(type, direction)
}

data class RoomPlayerSettings(
    val isPlayerSwipeEnabled: Boolean = true,
    val soulMixTotalByList: Int = 50,
    val isRewindEnabled: Boolean = true,
    val isMinimisedSongProgressionShown: Boolean = true,
    val playerVolume: Float = 1f,
    val isRemoteLyricsFetchEnabled: Boolean = false,
    val sharedPlayedListMusicUpdateMillis: Long = 0L,
) { fun toDomain() = SoulSearchingSettings.PlayerSettings(isPlayerSwipeEnabled, soulMixTotalByList, isRewindEnabled, isMinimisedSongProgressionShown, playerVolume, isRemoteLyricsFetchEnabled, sharedPlayedListMusicUpdateMillis) }

data class RoomColorThemeSettings(
    val type: ColorThemeType = ColorThemeType.DYNAMIC,
    val dynamicPlayerTheme: Boolean = false,
    val dynamicPlaylistTheme: Boolean = false,
    val dynamicOtherViewsTheme: Boolean = false,
    val forceDarkTheme: Boolean = false,
    val forceLightTheme: Boolean = false,
    val usedTheme: SoulSearchingTheme = SoulSearchingTheme.MainTheme,
    val paletteSeed: String = "DarkVibrant",
) { fun toDomain() = SoulSearchingSettings.ColorTheme(type, dynamicPlayerTheme, dynamicPlaylistTheme, dynamicOtherViewsTheme, forceDarkTheme, forceLightTheme, usedTheme, paletteSeed) }

data class RoomMainPageSettings(
    val isQuickAccessShown: Boolean = true,
    val arePlaylistsShown: Boolean = true,
    val areCollectionsShown: Boolean = true,
    val areAlbumsShown: Boolean = true,
    val areArtistsShown: Boolean = true,
    val areMusicsByMonthsShown: Boolean = false,
    val areMusicsByFoldersShown: Boolean = true,
    val isUsingVerticalAccessBar: Boolean = true,
    val initialTab: String = "QUICK_ACCESS",
) { fun toDomain() = SoulSearchingSettings.MainPage(isQuickAccessShown, arePlaylistsShown, areCollectionsShown, areAlbumsShown, areArtistsShown, areMusicsByMonthsShown, areMusicsByFoldersShown, isUsingVerticalAccessBar, initialTab) }

data class RoomCoverSettings(
    val isActivated: Boolean = false,
    val folderModePath: String? = null,
    val fileModePath: String? = null,
    val coverFileName: String? = "cover.jpg",
    val fileExtension: String? = "jpg",
    val whiteSpaceRuleActivated: Boolean = false,
    val whiteSpaceReplacement: String = "",
    val lowerCaseRule: Boolean? = null,
    val mode: CoverFolderRetriever.DynamicMode = CoverFolderRetriever.DynamicMode.Folder,
) { fun toDomain() = SoulSearchingSettings.Cover(CoverFolderRetriever(isActivated, folderModePath, fileModePath, coverFileName, fileExtension, CoverFolderRetriever.WhiteSpaceRule(whiteSpaceRuleActivated, whiteSpaceReplacement), lowerCaseRule, mode)) }

data class RoomDesktopSettings(val windowHeight: Float = 600f, val windowWidth: Float = 800f, val isFullscreen: Boolean = false) {
    fun toDomain() = SoulSearchingSettings.Desktop(windowHeight, windowWidth, isFullscreen)
}

data class RoomReleaseSettings(
    val isFetchReleaseFromGithubEnabled: Boolean = false,
    val shouldShowReleaseBottomEnableHint: Boolean = true,
    val latestViewedReleaseTag: String? = null,
    val latestReleaseName: String? = null,
    val latestReleaseTag: String? = null,
    val latestReleaseGithubUrl: String? = null,
    val latestReleaseChangelog: String? = null,
) {
    fun toDomain() = SoulSearchingSettings.ReleaseSettings(
        isFetchReleaseFromGithubEnabled,
        shouldShowReleaseBottomEnableHint,
        latestViewedReleaseTag,
        if (latestReleaseName != null && latestReleaseTag != null && latestReleaseGithubUrl != null) {
            Release(latestReleaseName, latestReleaseTag, latestReleaseGithubUrl, latestReleaseChangelog)
        } else null,
    )
}

data class RoomAlbumSettings(val shouldShowTrackPositionInAlbumView: Boolean = true) {
    fun toDomain() = SoulSearchingSettings.Album(shouldShowTrackPositionInAlbumView)
}

fun SoulSearchingSettings.toRoom() = RoomSoulSearchingSettings(
    hasMusicsBeenFetched = hasMusicsBeenFetched,
    isMusicFileModificationOn = isMusicFileModificationOn,
    musicSort = RoomSortSetup(musicSortSetup.type, musicSortSetup.direction),
    albumSort = RoomSortSetup(albumSortSetup.type, albumSortSetup.direction),
    artistSort = RoomSortSetup(artistSortSetup.type, artistSortSetup.direction),
    playlistSort = RoomSortSetup(playlistSortSetup.type, playlistSortSetup.direction),
    collectionSort = RoomSortSetup(collectionSortSetup.type, collectionSortSetup.direction),
    player = RoomPlayerSettings(playerSettings.isPlayerSwipeEnabled, playerSettings.soulMixTotalByList, playerSettings.isRewindEnabled, playerSettings.isMinimisedSongProgressionShown, playerSettings.playerVolume, playerSettings.isRemoteLyricsFetchEnabled, playerSettings.sharedPlayedListMusicUpdateMillis),
    colorTheme = RoomColorThemeSettings(colorTheme.type, colorTheme.dynamicPlayerTheme, colorTheme.dynamicPlaylistTheme, colorTheme.dynamicOtherViewsTheme, colorTheme.forceDarkTheme, colorTheme.forceLightTheme, colorTheme.usedTheme, colorTheme.paletteSeed),
    mainPage = RoomMainPageSettings(mainPage.isQuickAccessShown, mainPage.arePlaylistsShown, mainPage.areCollectionsShown, mainPage.areAlbumsShown, mainPage.areArtistsShown, mainPage.areMusicsByMonthsShown, mainPage.areMusicsByFoldersShown, mainPage.isUsingVerticalAccessBar, mainPage.initialTab),
    cover = cover.artistCoverFolderRetriever.let { RoomCoverSettings(it.isActivated, it.folderModePath, it.fileModePath, it.coverFileName, it.fileExtension, it.whiteSpaceRule.isActivated, it.whiteSpaceRule.replacement, it.lowerCaseRule, it.mode) },
    desktop = RoomDesktopSettings(desktop.windowHeight, desktop.windowWidth, desktop.isFullscreen),
    release = RoomReleaseSettings(release.isFetchReleaseFromGithubEnabled, release.shouldShowReleaseBottomEnableHint, release.latestViewedReleaseTag, release.latestRelease?.name, release.latestRelease?.tag, release.latestRelease?.githubUrl, release.latestRelease?.changelog),
    album = RoomAlbumSettings(album.shouldShowTrackPositionInAlbumView),
)
