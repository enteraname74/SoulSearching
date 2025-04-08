package com.github.enteraname74.soulsearching.coreui.strings

import androidx.compose.ui.text.intl.Locale

val strings = when(Locale.current.language) {
    "fr" -> FrStrings
    else -> EnStrings
}

/**
 * Application strings.
 */
interface Strings {
    val appName: String get() = "Soul Searching"
    val appLogo: String
    val cannotRetrieveSongs: String
    val noElements: String
    val backButton: String
    val headerBarRightButton: String
    val image: String
    val moreButton: String
    val more: String
    val settingsAccessButton: String
    val createPlaylistButton: String
    val shuffleButton: String
    val soulMix: String get() = "Soul Mix"
    val favorite: String
    val playlists: String get() = "Playlists"
    val albums: String get() = "Albums"
    val folders: String
    val artists: String
    val byFolders: String
    val byMonths: String
    val musics: String
    val quickAccess: String
    val missingPermissions: String
    val playedList: String
    val currentSong: String

    val lyrics: String
    val activateRemoteLyricsFetchTitle: String
    val activateRemoteLyricsFetchText: String
    val activateRemoteLyricsFetchHint: String
    val noLyricsFound: String
    val lyricsProvider: String
    val lyricsProviderName: String get() = "LrcLib"

    val completeApplicationTitle: String
    val completeApplicationText: String
    val quickAccessTitle: String
    val quickAccessText: String
    val modifyElementsTitle: String
    val modifyElementsText: String
    val dynamicThemeFeatureTitle: String
    val dynamicThemeFeatureText: String
    val manageFoldersTitle: String
    val manageFoldersText: String
    val addNewMusicsTitle: String
    val addNewMusicsText: String
    val personalizeMainPageTitle: String
    val personalizeMainPageText: String

    val managePlayerTitle: String
    val managePlayerText: String
    val playerSwipeTitle: String
    val playerRewindTitle: String
    val playerMinimisedProgressionTitle: String
    val soulMixSettingsTitle: String

    val searchingSongsFromYourDevice: String
    val searchForMusics: String
    val searchAll: String

    val sortByDateAdded: String
    val sortByMostListened: String
    val sortByName: String
    val sortByAscOrDesc: String

    val removeFromQuickAccess: String
    val removeFromPlaylist: String
    val removeFromPlayedList: String
    val addToQuickAccess: String
    val addToPlaylist: String

    val create: String
    val cancel: String
    val delete: String
    val ok: String get() = "Ok"

    val soulMixInfoDialogTitle: String get() = "Soul Mix"
    val soulMixInfoDialogText: String

    val createPlaylistDialogTitle: String
    val playlistName: String
    val musicName: String
    val musicAlbumPosition: String
    val albumName: String
    val artistName: String
    val playlistCover: String
    val albumCover: String
    val artistCover: String
    val playlistInformation: String
    val musicInformation: String
    val musicPath: String
    val musicFileCover: String
    val musicAppCover: String
    val coverSelection: String
    val artistInformation: String
    val albumInformation: String

    val addArtist: String

    val deleteMusicDialogTitle: String
    val deleteMusicDialogText: String
    val deleteAlbumDialogTitle: String
    val deletePlaylistDialogTitle: String
    val deleteArtistDialogTitle: String

    val deleteSelectedMusicsDialogTitle: String
    val deleteSelectedMusicsDialogText: String
    val deleteSelectedAlbumsDialogTitle: String
    val deleteSelectedArtistsDialogTitle: String
    val deleteSelectedPlaylistsDialogTitle: String

    val removeMusicFromPlaylistTitle: String
    val removeMusicFromPlaylistText: String
    val removeSelectedMusicFromPlaylistTitle: String
    val removeSelectedMusicFromPlaylistText: String

    val modifyAlbum: String
    val modifyArtist: String
    val modifyMusic: String
    val modifyPlaylist: String
    val coversOfTheAlbum: String
    val coversOfTheArtist: String
    val coversOfThePlaylist: String
    val coversOfSongAlbum: String
    val noAvailableCovers: String

    val deleteAlbum: String
    val deleteArtist: String
    val deleteMusic: String
    val deletePlaylist: String
    val deleteSelectedAlbums: String
    val deleteSelectedArtists: String
    val deleteSelectedPlaylists: String
    val deleteSelectedMusics: String

    val playNext: String

    val personalizedThemeTitle: String
    val personalizedThemeText: String
    val dynamicPlayerView: String
    val dynamicPlaylistView: String
    val dynamicOtherView: String

    val settings: String
    val advancedSettingsTitle: String
    val advancedSettingsText: String
    val reloadCoversTitle: String
    val reloadCoversText: String
    val reloadMusicsCovers: String
    val deletePlaylistsCovers: String
    val reloadAlbumsCovers: String
    val reloadArtistsCovers: String

    val splitMultipleArtistsTitle: String
    val splitMultipleArtistsText: String

    val activateGithubReleaseFetchTitle: String
    val activateGithubReleaseFetchText: String
    val activateGithubReleaseFetchHint: String
    val goToSettings: String

    val manageMusicsTitle: String
    val manageMusicsText: String
    val modifyMusicFileTitle: String
    val modifyMusicFileText: String
    val colorThemeTitle: String
    val colorThemeText: String
    val personalizationTitle: String
    val personalizationText: String
    val aboutTitle: String
    val aboutText: String
    val newReleaseAvailableTitle: String
    val statisticsTitle: String
    val statisticsText: String
    val mostPlayedSongs: String
    val mostPlayedAlbums: String
    val mostPlayedArtists: String
    val artistsWithMostSongs: String
    val mostPlayedPlaylists: String
    val themeSelectionTitle: String
    val themeSelectionText: String
    val forceDarkThemeTitle: String
    val forceLightThemeTitle: String
    val mainTheme: String get() = "Soul Searching"
    val steelTheme: String
    val glacierTheme: String get() = "Glacier"
    val duskTheme: String
    val passionTheme: String get() = "Passion"
    val greeneryTheme: String
    val treeBarkTheme: String

    val mainPageTitle: String
    val mainPageText: String
    val showQuickAccess: String
    val showPlaylists: String
    val showAlbums: String
    val showArtists: String
    val manageMusicsViewText: String
    val showMusicsByFolders: String
    val showMusicsByMonths: String
    val useVerticalAccessBarTitle: String
    val useHorizontalAccessBarText: String

    val usedFoldersTitle: String
    val usedFoldersText: String
    val fetchingFolders: String
    val addMusicsTitle: String
    val addMusicsText: String
    val deletingMusicsFromUnselectedFolders: String

    val dynamicThemeTitle: String
    val dynamicThemeText: String
    val systemThemeTitle: String
    val systemThemeText: String

    val leadDeveloper: String
    val developersTitle: String
    val developersText: String
    val versionNameTitle: String
    val versionNameActionText: String get() = "Soul Searching - Average White Band - 1976"

    val noNewMusics: String
    val savingNewMusics: String
    val songsSaved: String
    val searchForNewSongs: String

    val appMigration: String

    val multipleArtistsTitle: String
    val multipleArtistsText: String
    val multipleArtistsSelectionTitle: String
    val noMultipleArtists: String

    val multipleSelection: String

    val fieldCannotBeEmpty: String
    val fieldMustBeANumber: String

    val playerVolume: String

    val anErrorOccurred: String
    val albumDoesNotExists: String
    val artistDoesNotExists: String
    val playlistDoesNotExists: String
    val folderDoesNotExists: String
    val monthPlaylistDoesNotExists: String

    val playlistDetailTitle: String get() = "Playlist"
    val artistDetailTitle: String
    val albumDetailTitle: String get() = "Album"
    val monthDetailTitle: String
    val folderDetailTitle: String

    val elementDetailEdit: String
    val elementDetailPlay: String
    val elementDetailShuffle: String
    val elementDetailTitles: String

    /**
     * Shows a text indicating the number of musics.
     */
    fun musics(total: Int): String

    /**
     * Shows a text indicating that songs were deleted from the app automatically.
     */
    fun deletedMusicsAutomatically(total: Int): String

    /**
     * Shows a text indicating the total play of an element.
     */
    fun plays(total: Int): String

    /**
     * Shows a text indicating the current folder where songs are fetched.
     */
    fun fetchingMusicsFrom(path: String): String

    /**
     * Shows a text indicating the total of selected elements.
     */
    fun selectedElements(total: Int): String

    /**
     * Shows a text indicating the detail of the new latest release of Soul Searching
     */
    fun newReleaseAvailableText(releaseName: String): String
}