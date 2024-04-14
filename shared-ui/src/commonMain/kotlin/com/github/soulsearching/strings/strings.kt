package com.github.soulsearching.strings

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
    val noElements: String
    val backButton: String
    val headerBarRightButton: String
    val image: String
    val moreButton: String
    val more: String
    val settingsAccessButton: String
    val createPlaylistButton: String
    val shuffleButton: String
    val favorite: String
    val playlists: String get() = "Playlists"
    val albums: String get() = "Albums"
    val artists: String
    val musics: String
    val quickAccess: String
    val missingPermissions: String
    val playedList: String
    val currentSong: String
    val lyrics: String
    val noLyricsFound: String
    val lyricsProvider: String

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

    val createPlaylistDialogTitle: String
    val playlistName: String
    val musicName: String
    val albumName: String
    val artistName: String
    val playlistCover: String
    val albumCover: String
    val artistCover: String
    val playlistInformation: String
    val musicInformation: String
    val artistInformation: String
    val albumInformation: String

    val deleteMusicDialogTitle: String
    val deleteMusicDialogText: String
    val deleteAlbumDialogTitle: String
    val deletePlaylistDialogTitle: String
    val deleteArtistDialogTitle: String
    val removeMusicFromPlaylistTitle: String
    val removeMusicFromPlaylistText: String

    val modifyAlbum: String
    val modifyArtist: String
    val modifyMusic: String
    val modifyPlaylist: String

    val deleteAlbum: String
    val deleteArtist: String
    val deleteMusic: String
    val deletePlaylist: String

    val playNext: String

    val personalizedThemeTitle: String
    val personalizedThemeText: String
    val dynamicPlayerView: String
    val dynamicPlaylistView: String
    val dynamicOtherView: String

    val settings: String
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

    val mainPageTitle: String
    val mainPageText: String
    val showQuickAccess: String
    val showPlaylists: String
    val showAlbums: String
    val showArtists: String
    val showVerticalAccessBarTitle: String
    val showVerticalAccessBarText: String

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

    /**
     * Show a text indicating the number of musics.
     */
    fun musics(total: Int): String
}