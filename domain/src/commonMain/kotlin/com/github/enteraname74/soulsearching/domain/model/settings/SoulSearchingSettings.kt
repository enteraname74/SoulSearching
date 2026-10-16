package com.github.enteraname74.soulsearching.domain.model.settings

import com.github.enteraname74.soulsearching.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.domain.model.Release
import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType
import kotlinx.serialization.Serializable

@Serializable
data class SoulSearchingSettings(
    val updatedAtMillis: Long = 0L,
    val hasMusicsBeenFetched: Boolean = false,
    val isMusicFileModificationOn: Boolean = true,
    val musicSortSetup: SortSetup = SortSetup(),
    val albumSortSetup: SortSetup = SortSetup(),
    val artistSortSetup: SortSetup = SortSetup(),
    val playlistSortSetup: SortSetup = SortSetup(),
    val collectionSortSetup: SortSetup = SortSetup(),
    val playerSettings: PlayerSettings = PlayerSettings(),
    val colorTheme: ColorTheme = ColorTheme(),
    val mainPage: MainPage = MainPage(),
    val cover: Cover = Cover(),
    val desktop: Desktop = Desktop(),
    val release: ReleaseSettings = ReleaseSettings(),
    val album: Album = Album(),
) {
    @Serializable
    data class SortSetup(
        val type: SortType = SortType.DEFAULT,
        val direction: SortDirection = SortDirection.DEFAULT,
    )

    @Serializable
    data class PlayerSettings(
        val isPlayerSwipeEnabled: Boolean = true,
        val soulMixTotalByList: Int = 50,
        val isRewindEnabled: Boolean = true,
        val isMinimisedSongProgressionShown: Boolean = true,
        val playerVolume: Float = 1f,
        val isRemoteLyricsFetchEnabled: Boolean = false,
        val sharedPlayedListMusicUpdateMillis: Long = 0L,
    )

    @Serializable
    data class ColorTheme(
        val type: ColorThemeType = ColorThemeType.DYNAMIC,
        val dynamicPlayerTheme: Boolean = false,
        val dynamicPlaylistTheme: Boolean = false,
        val dynamicOtherViewsTheme: Boolean = false,
        val forceDarkTheme: Boolean = false,
        val forceLightTheme: Boolean = false,
        val usedTheme: SoulSearchingTheme = SoulSearchingTheme.MainTheme,
        val paletteSeed: String = "DarkVibrant",
    )

    @Serializable
    data class MainPage(
        val isQuickAccessShown: Boolean = true,
        val arePlaylistsShown: Boolean = true,
        val areCollectionsShown: Boolean = true,
        val areAlbumsShown: Boolean = true,
        val areArtistsShown: Boolean = true,
        val areMusicsByMonthsShown: Boolean = false,
        val areMusicsByFoldersShown: Boolean = true,
        val isUsingVerticalAccessBar: Boolean = true,
        val initialTab: String = "QUICK_ACCESS",
    )

    @Serializable
    data class Cover(
        val artistCoverFolderRetriever: CoverFolderRetriever = CoverFolderRetriever.default,
    )

    @Serializable
    data class Desktop(
        val windowHeight: Float = 600f,
        val windowWidth: Float = 800f,
        val isFullscreen: Boolean = false,
    )

    @Serializable
    data class ReleaseSettings(
        val isFetchReleaseFromGithubEnabled: Boolean = false,
        val shouldShowReleaseBottomEnableHint: Boolean = true,
        val latestViewedReleaseTag: String? = null,
        val latestRelease: Release? = null,
    )

    @Serializable
    data class Album(
        val shouldShowTrackPositionInAlbumView: Boolean = true,
    )
}
