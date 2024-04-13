package com.github.soulsearching.strings

/**
 * English translation for application strings
 */
object EnStrings : Strings {
    override val appLogo = "Application's logo"
    override val noElements = "No elements"
    override val backButton = "Back button"
    override val createPlaylistButton = "Create playlist button"
    override val headerBarRightButton = "Header bar right button"
    override val image = "Image"
    override val moreButton = "More button"
    override val more = "More"
    override val settingsAccessButton = "Settings access button"
    override val shuffleButton = "Shuffle button"
    override val favorite = "Favorites"
    override val missingPermissions = "You must accept all permissions required by the application for it to work properly."
    override val playedList = "Played list"
    override val currentSong = "Current song"
    override val lyrics = "Lyrics"
    override val noLyricsFound = "No lyrics found for this song"
    override val lyricsProvider = "Lyrics proposed by Lyrist"

    override val completeApplicationTitle = "Complete music application"
    override val completeApplicationText = "Listen to all your songs, albums, artists, playlists."
    override val quickAccessTitle = "Keep what you love the most near you."
    override val quickAccessText = "With the quick access, you can easily access your favorite album, or your preferred songs for example."
    override val modifyElementsTitle = "Modify your music related elements"
    override val modifyElementsText = "You can modify information about an album, a song, an artist…"
    override val dynamicThemeFeatureTitle = "Dynamic theme"
    override val dynamicThemeFeatureText = "Soul Searching offers you the possibility of having a dynamic theme depending on the current played song."
    override val manageFoldersTitle = "Manage used folders"
    override val manageFoldersText = "Manage what folders are used to fetch songs."
    override val addNewMusicsTitle = "Add new songs"
    override val addNewMusicsText = "You can easily add new songs from your device."
    override val personalizeMainPageTitle = "Define what you really want"
    override val personalizeMainPageText = "You don't want the quick access, or you wish to have a bar that helps you to reach parts of the main page? You can realize your wishes in the settings of the application."

    override val searchingSongsFromYourDevice = "Searching songs from your device…"
    override val searchForMusics = "Search a song"
    override val searchAll = "Search a song, an artist, an album, a playlist…"

    override val artists = "Artists"
    override val musics = "Songs"
    override val quickAccess = "Quick Access"

    override val sortByDateAdded = "Sort by date added"
    override val sortByMostListened = "Sort by most listened"
    override val sortByName = "Sort by name"
    override val sortByAscOrDesc = "ascending or descending Sort"

    override val removeFromQuickAccess = "Remove from quick access"
    override val removeFromPlaylist = "Remove from playlist"
    override val removeFromPlayedList = "Remove from played list"
    override val addToQuickAccess = "Add to quick access"
    override val addToPlaylist = "Add to a playlist"

    override val create = "Create"
    override val cancel = "Cancel"
    override val delete = "Delete"

    override val createPlaylistDialogTitle = "Create a new playlist"
    override val playlistName = "Playlist's name"
    override val musicName = "Song's name"
    override val albumName = "Album's name"
    override val artistName = "Artist's name"
    override val playlistCover = "Playlist's cover"
    override val albumCover = "Album's cover"
    override val artistCover = "Artist's cover"
    override val playlistInformation = "Playlist's information"
    override val musicInformation = "Song's information"
    override val artistInformation = "Artist's information"
    override val albumInformation = "Album's information"

    override val deleteMusicDialogTitle = "Are you sure to delete this song?"
    override val deleteMusicDialogText = "It will be removed from the application."
    override val deleteAlbumDialogTitle = "Are you sure to delete this album?"
    override val deletePlaylistDialogTitle = "Are you sure to delete this playlist?"
    override val deleteArtistDialogTitle = "Are you sure to delete this artist?"
    override val removeMusicFromPlaylistTitle = "Are you sure to remove this song from this playlist?"
    override val removeMusicFromPlaylistText = "This song will be removed from this playlist but not deleted from the application."
    override val modifyAlbum = "Modify this album"
    override val modifyArtist = "Modify this artist"
    override val modifyMusic = "Modify this song"
    override val modifyPlaylist = "Modify this playlist"

    override val deleteAlbum = "Delete this album"
    override val deleteArtist = "Delete this artist"
    override val deleteMusic = "Delete this song"
    override val deletePlaylist = "Delete this playlist"

    override val playNext = "Play next"

    override val personalizedThemeTitle = "Personalized theme"
    override val personalizedThemeText = "Define precisely your color theme"
    override val dynamicPlayerView = "Use dynamic theme for player view"
    override val dynamicPlaylistView = "Use dynamic theme for playlist, album and artist view"
    override val dynamicOtherView = "Use dynamic theme for the other views"

    override val settings = "Settings"
    override val manageMusicsTitle = "Manage songs"
    override val manageMusicsText = "Add new songs and manage used folders"
    override val colorThemeTitle = "Color theme"
    override val colorThemeText = "Dynamic theme, personalized theme"
    override val personalizationTitle = "Personalization"
    override val personalizationText = "Manage main page view"
    override val aboutTitle = "About Soul Searching"
    override val aboutText = "Developers, application's version name"

    override val mainPageTitle = "Main page"
    override val mainPageText = "Manage main page view"
    override val showQuickAccess = "Show quick access"
    override val showPlaylists = "Show playlists"
    override val showAlbums = "Show albums"
    override val showArtists = "Show artists"
    override val showVerticalAccessBarTitle = "Show vertical access bar"
    override val showVerticalAccessBarText = "Gives quick access to all elements on the main page"

    override val usedFoldersTitle = "Used folders"
    override val usedFoldersText = "Define the folders used in the application"
    override val addMusicsTitle = "Add songs"
    override val addMusicsText = "Add new songs from your device to the application"
    override val fetchingFolders = "Fetching folders"
    override val deletingMusicsFromUnselectedFolders = "Deleting songs from unselected folders…"

    override val dynamicThemeTitle = "Dynamic theme"
    override val dynamicThemeText = "Global color theme based on the current played song's album cover"
    override val systemThemeTitle = "System theme"
    override val systemThemeText = "Global color theme based on your system color mode"

    override val leadDeveloper = "Lead developer"
    override val developersTitle = "Developers"
    override val developersText = "The developers behind the Soul Searching project"
    override val versionNameTitle = "Application's version name"

    override val noNewMusics = "No new songs to add!"
    override val savingNewMusics = "Saving new songs…"

    override fun musics(total: Int): String {
        return when(total) {
            0 -> "No songs"
            1 -> "1 song"
            else -> "$total songs"
        }
    }
}