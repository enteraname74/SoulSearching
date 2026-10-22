package com.github.enteraname74.localdb.settings

import com.github.enteraname74.soulsearching.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.domain.model.Release
import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType
import com.github.enteraname74.soulsearching.domain.model.settings.ColorThemeType
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingTheme
import com.github.enteraname74.soulsearching.features.serialization.SerializationUtils
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

internal class LegacySettingsReader(private val settings: Settings) {
    fun read(): SoulSearchingSettings = SoulSearchingSettings(
        hasMusicsBeenFetched = settings[Keys.HasMusicsBeenFetched, false],
        isMusicFileModificationOn = settings[Keys.MusicFileModification, true],
        musicSortSetup = sort(Keys.MusicSortType, Keys.MusicSortDirection),
        albumSortSetup = sort(Keys.AlbumSortType, Keys.AlbumSortDirection),
        artistSortSetup = sort(Keys.ArtistSortType, Keys.ArtistSortDirection),
        playlistSortSetup = sort(Keys.PlaylistSortType, Keys.PlaylistSortDirection),
        collectionSortSetup = sort(Keys.CollectionSortType, Keys.CollectionSortDirection),
        playerSettings = SoulSearchingSettings.PlayerSettings(
            isPlayerSwipeEnabled = settings[Keys.PlayerSwipe, true],
            soulMixTotalByList = settings[Keys.SoulMixTotal, 50],
            isRewindEnabled = settings[Keys.Rewind, true],
            isMinimisedSongProgressionShown = settings[Keys.MinimisedProgression, true],
            playerVolume = settings[Keys.PlayerVolume, 1f],
            isRemoteLyricsFetchEnabled = settings[Keys.RemoteLyrics, false],
            sharedPlayedListMusicUpdateMillis = settings[Keys.SharedListUpdate, 0L],
        ),
        colorTheme = SoulSearchingSettings.ColorTheme(
            type = ColorThemeType.fromOrDefault(settings[Keys.ColorThemeType, 0]),
            dynamicPlayerTheme = settings[Keys.DynamicPlayerTheme, false],
            dynamicPlaylistTheme = settings[Keys.DynamicPlaylistTheme, false],
            dynamicOtherViewsTheme = settings[Keys.DynamicOtherViewsTheme, false],
            forceDarkTheme = settings[Keys.ForceDarkTheme, false],
            forceLightTheme = settings[Keys.ForceLightTheme, false],
            usedTheme = SoulSearchingTheme.fromOrDefault(settings[Keys.UsedTheme, SoulSearchingTheme.MainTheme.value]),
            paletteSeed = settings[Keys.PaletteSeed, "DarkVibrant"],
        ),
        mainPage = SoulSearchingSettings.MainPage(
            isQuickAccessShown = settings[Keys.QuickAccess, true],
            arePlaylistsShown = settings[Keys.Playlists, true],
            areCollectionsShown = settings[Keys.Collections, true],
            areAlbumsShown = settings[Keys.Albums, true],
            areArtistsShown = settings[Keys.Artists, true],
            areMusicsByMonthsShown = settings[Keys.MusicsByMonths, false],
            areMusicsByFoldersShown = settings[Keys.MusicsByFolders, true],
            isUsingVerticalAccessBar = settings[Keys.VerticalAccessBar, true],
            initialTab = settings[Keys.InitialTab, "QUICK_ACCESS"],
        ),
        cover = SoulSearchingSettings.Cover(
            artistCoverFolderRetriever = settings[Keys.ArtistCoverRetriever, ""].takeIf(String::isNotBlank)
                ?.let { runCatching { SerializationUtils.deserialize<CoverFolderRetriever>(it) }.getOrNull() }
                ?: CoverFolderRetriever.default,
        ),
        desktop = SoulSearchingSettings.Desktop(
            windowHeight = settings[Keys.WindowHeight, 600f],
            windowWidth = settings[Keys.WindowWidth, 800f],
            isFullscreen = settings[Keys.Fullscreen, false],
        ),
        release = SoulSearchingSettings.ReleaseSettings(
            isFetchReleaseFromGithubEnabled = settings[Keys.FetchRelease, false],
            shouldShowReleaseBottomEnableHint = settings[Keys.ReleaseHint, true],
            latestViewedReleaseTag = settings[Keys.LatestViewedRelease, ""].takeIf(String::isNotBlank),
            latestRelease = settings[Keys.LatestRelease, ""].takeIf(String::isNotBlank)
                ?.let { runCatching { SerializationUtils.deserialize<Release>(it) }.getOrNull() },
        ),
        album = SoulSearchingSettings.Album(
            shouldShowTrackPositionInAlbumView = settings[Keys.ShowTrackPosition, true],
        ),
    )

    private fun sort(typeKey: String, directionKey: String) = SoulSearchingSettings.SortSetup(
        type = SortType.from(settings[typeKey, SortType.DEFAULT.value]) ?: SortType.DEFAULT,
        direction = SortDirection.from(settings[directionKey, SortDirection.DEFAULT.value]) ?: SortDirection.DEFAULT,
    )

    fun clear() = Keys.All.forEach(settings::remove)

    private object Keys {
        const val HasMusicsBeenFetched = "MUSICS_FETCHED"
        const val MusicFileModification = "IS_MUSIC_FILE_MODIFICATION_ON"
        const val MusicSortType = "SORT_MUSICS_TYPE"; const val MusicSortDirection = "SORT_MUSICS_DIRECTION"
        const val AlbumSortType = "SORT_ALBUMS_TYPE"; const val AlbumSortDirection = "SORT_ALBUMS_DIRECTION"
        const val ArtistSortType = "SORT_ARTISTS_TYPE"; const val ArtistSortDirection = "SORT_ARTISTS_DIRECTION"
        const val PlaylistSortType = "SORT_PLAYLISTS_TYPE"; const val PlaylistSortDirection = "SORT_PLAYLISTS_DIRECTION"
        const val CollectionSortType = "SORT_COLLECTIONS_TYPE"; const val CollectionSortDirection = "SORT_COLLECTIONS_DIRECTION"
        const val PlayerSwipe = "IS_PLAYER_SWIPE_ENABLED"; const val SoulMixTotal = "SOUL_MIX_TOTAL_BY_LIST"
        const val Rewind = "IS_REWIND_ENABLED"; const val MinimisedProgression = "IS_MINIMISED_SONG_PROGRESSION_SHOWN"
        const val PlayerVolume = "PLAYER_VOLUME"; const val RemoteLyrics = "IS_REMOTE_LYRICS_FETCH_ENABLED"
        const val SharedListUpdate = "SHARED_PLAYED_LIST_MUSIC_UPDATE_MILLIS"
        const val ColorThemeType = "COLOR_THEME"; const val DynamicPlayerTheme = "DYNAMIC_PLAYER_THEME"
        const val DynamicPlaylistTheme = "DYNAMIC_PLAYLIST_THEME"; const val DynamicOtherViewsTheme = "DYNAMIC_OTHER_VIEWS_THEME"
        const val ForceDarkTheme = "FORCE_DARK_THEME_KEY"; const val ForceLightTheme = "FORCE_LIGHT_THEME_KEY"
        const val UsedTheme = "USED_COLOR_THEME_ID_KEY"
        const val PaletteSeed = "COLOR_PALETTE_SEED"
        const val QuickAccess = "IS_QUICK_ACCESS_SHOWN"; const val Playlists = "IS_PLAYLISTS_SHOWN"
        const val Collections = "ARE_COLLECTIONS_SHOWN"; const val Albums = "IS_ALBUMS_SHOWN"
        const val Artists = "IS_ARTISTS_SHOWN"; const val MusicsByMonths = "ARE_MUSICS_BY_MONTHS_SHOWN"
        const val MusicsByFolders = "ARE_MUSICS_BY_FOLDERS_SHOWN"; const val VerticalAccessBar = "IS_USING_VERTICAL_ACCESS_BAR"
        const val InitialTab = "MAIN_PAGE_INITAL_TAB"
        const val ArtistCoverRetriever = "ARTIST_COVER_FOLDER_RETRIEVER"
        const val WindowHeight = "WINDOW_HEIGHT"; const val WindowWidth = "WINDOW_WIDTH"; const val Fullscreen = "IS_FULLSCREEN"
        const val FetchRelease = "IS_FETCH_RELEASE_FROM_GITHUB_ENABLED"; const val ReleaseHint = "SHOULD_SHOW_RELEASE_BOTTOM_ENABLE_HINT"
        const val LatestViewedRelease = "LATEST_VIEWED_RELEASE"; const val LatestRelease = "LATEST_RELEASE"
        const val ShowTrackPosition = "SHOULD_SHOW_TRACK_POSITION_IN_ALBUM_VIEW"
        val All = listOf(HasMusicsBeenFetched, MusicFileModification, MusicSortType, MusicSortDirection, AlbumSortType, AlbumSortDirection, ArtistSortType, ArtistSortDirection, PlaylistSortType, PlaylistSortDirection, CollectionSortType, CollectionSortDirection, PlayerSwipe, SoulMixTotal, Rewind, MinimisedProgression, PlayerVolume, RemoteLyrics, SharedListUpdate, ColorThemeType, DynamicPlayerTheme, DynamicPlaylistTheme, DynamicOtherViewsTheme, ForceDarkTheme, ForceLightTheme, UsedTheme, PaletteSeed, QuickAccess, Playlists, Collections, Albums, Artists, MusicsByMonths, MusicsByFolders, VerticalAccessBar, InitialTab, ArtistCoverRetriever, WindowHeight, WindowWidth, Fullscreen, FetchRelease, ReleaseHint, LatestViewedRelease, LatestRelease, ShowTrackPosition, "CURRENT_DB_VERSION")
    }
}
