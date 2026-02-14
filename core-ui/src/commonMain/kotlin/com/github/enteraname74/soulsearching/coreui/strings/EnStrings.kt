package com.github.enteraname74.soulsearching.coreui.strings

import com.github.enteraname74.soulsearching.coreui.theme.color.ColorPaletteSeed

/**
 * English translation for application strings
 */
object EnStrings : Strings {
    override val appLogo = "Application's logo"
    override val noElements = "No elements"
    override val emptyQuickAccess = "No items in quick access"
    override val quickAccessExplanation: String = "Add a song, album, artist, or playlist to quick access " +
            "to see it appear here."
    override val cannotRetrieveSongs: String = "Cannot retrieve songs!"
    override val backButton = "Back button"
    override val createPlaylistButton = "Create playlist button"
    override val headerBarRightButton = "Header bar right button"
    override val image = "Image"
    override val moreButton = "More button"
    override val more = "More"
    override val settingsAccessButton = "Settings access button"
    override val shuffleButton = "Shuffle button"
    override val favorite = "Favorites"
    override val missingPermissions =
        "You must accept all permissions required by the application for it to work properly."
    override val playedList = "Played list"
    override val currentSong = "Current song"

    override val lyrics = "Lyrics"
    override val activateRemoteLyricsFetchTitle = "Enable remote lyrics fetch"
    override val activateRemoteLyricsFetchText by lazy {
        "The app needs your permission to search for the lyrics of the current song via an external service ($lyricsProviderName) " +
                "when no lyrics is found in the song's file."
    }
    override val activateRemoteLyricsFetchHint by lazy {
        "The application will use a song's name, album and artist to find lyrics from a remote source ($lyricsProviderName) " +
                "when no lyrics where found in the song's file."
    }
    override val noLyricsFound = "No lyrics found for this song"
    override val localLyricsProvider = "Lyrics from the metadata of the music file"
    override val remoteLyricsProvider = "Lyrics proposed by LrcLib"

    override val completeApplicationTitle = "Complete music application"
    override val completeApplicationText = "Listen to all your songs, albums, artists, playlists."
    override val quickAccessTitle = "Keep what you love the most near you."
    override val quickAccessText =
        "With the quick access, you can easily access your favorite album, or your preferred songs for example."
    override val modifyElementsTitle = "Modify your music related elements"
    override val modifyElementsText = "You can modify information about an album, a song, an artist…"
    override val dynamicThemeFeatureTitle = "Dynamic theme"
    override val dynamicThemeFeatureText =
        "Soul Searching offers you the possibility of having a dynamic theme depending on the current played song."
    override val manageFoldersTitle = "Manage used folders"
    override val manageFoldersText = "Manage what folders are used to fetch songs."
    override val addNewMusicsTitle = "Add new songs"
    override val addNewMusicsText = "You can easily add new songs from your device."
    override val personalizeMainPageTitle = "Define what you really want"
    override val personalizeMainPageText =
        "You don't want the quick access, or you wish to have a bar that helps you to reach parts of the main page? You can realize your wishes in the settings of the application."

    override val searchingSongsFromYourDevice = "Searching songs from your device…"
    override val searchForMusics = "Search a song"
    override val searchAll = "Search a song, an artist, an album, a playlist…"

    override val artists = "Artists"
    override val musics = "Songs"
    override val folders = "Folders"
    override val byFolders = "By folders"
    override val byMonths = "By months"
    override val quickAccess = "Quick Access"

    override val sortByAddedDate = "Added date"
    override val sortByMostListened = "Most listened"
    override val sortByName = "Alphabetical"

    override val removeFromQuickAccess = "Remove from quick access"
    override val removeFromPlaylist = "Remove from playlist"
    override val removeFromPlayedList = "Remove from played list"
    override val addToQuickAccess = "Add to quick access"
    override val addToPlaylist = "Add to a playlist"

    override val create = "Create"
    override val cancel = "Cancel"
    override val delete = "Delete"

    override val soulMixInfoDialogText = "Listen to a mix of fetched songs from each of your folder!\nYou can " +
            "define the total of songs fetched from each folder in the settings."

    override val createPlaylistDialogTitle = "Create a new playlist"
    override val playlistName = "Playlist's name"
    override val musicName = "Song's name"
    override val musicAlbumPosition = "Position in album"
    override val albumName = "Album's name"
    override val albumArtistName = "Album's artist name"
    override val artistName = "Artist's name"
    override val playlistCover = "Playlist's cover"
    override val albumCover = "Album's cover"
    override val artistCover = "Artist's cover"
    override val playlistInformation = "Playlist's information"
    override val musicInformation = "Song's information"
    override val musicPath = "Song's path:"
    override val musicFileCover = "File's cover"
    override val musicAppCover = "App's cover"
    override val coverSelection = "Cover selection"
    override val artistInformation = "Artist's information"
    override val albumInformation = "Album's information"

    override val addArtist = "Add artist"

    override val deleteMusicDialogTitle = "Are you sure to delete this song?"
    override val deleteMusicDialogText = "It will be removed from the application."
    override val deleteAlbumDialogTitle = "Are you sure to delete this album?"
    override val deletePlaylistDialogTitle = "Are you sure to delete this playlist?"
    override val deleteArtistDialogTitle = "Are you sure to delete this artist?"

    override val deleteSelectedMusicsDialogTitle = "Are you sure to delete the selected songs?"
    override val deleteSelectedMusicsDialogText = "They will be removed from the application."
    override val deleteSelectedAlbumsDialogTitle = "Are you sure to delete the selected albums?"
    override val deleteSelectedArtistsDialogTitle = "Are you sure to delete the selected artists?"
    override val deleteSelectedPlaylistsDialogTitle = "Are you sure to delete the selected playlists?"

    override val removeMusicFromPlaylistTitle = "Are you sure to remove this song from this playlist?"
    override val removeMusicFromPlaylistText =
        "This song will be removed from this playlist but not deleted from the application."
    override val removeSelectedMusicFromPlaylistTitle = "Are you sure to remove the selected songs from this playlist?"
    override val removeSelectedMusicFromPlaylistText = "The selected songs will be removed from this playlist but not deleted from the application."

    override val modifyAlbum = "Modify this album"
    override val modifyArtist = "Modify this artist"
    override val modifyMusic = "Modify this song"
    override val modifyPlaylist = "Modify this playlist"
    override val coversOfTheAlbum = "Covers of the album's songs"
    override val coversOfTheArtist = "Covers of the artist's songs"
    override val coversOfThePlaylist = "Covers of the playlist's songs"
    override val coversOfSongAlbum = "Covers in the song's album"
    override val noAvailableCovers = "No available covers"

    override val deleteAlbum = "Delete this album"
    override val deleteArtist = "Delete this artist"
    override val deleteMusic = "Delete this song"
    override val deletePlaylist = "Delete this playlist"
    override val deleteSelectedAlbums = "Delete selected albums"
    override val deleteSelectedArtists = "Delete selected artists"
    override val deleteSelectedPlaylists = "Delete selected playlists"
    override val deleteSelectedMusics = "Delete selected songs"

    override val playNext = "Play next"
    override val addToQueue = "Add to queue"

    override val personalizedThemeTitle = "Personalized theme"
    override val personalizedThemeText = "Apply dynamic theme on:"
    override val dynamicPlayerView = "Player view"
    override val dynamicPlaylistView = "Playlist, album, artist, month and folder view"
    override val dynamicOtherView = "Other views"

    override val settings = "Settings"
    override val advancedSettingsTitle = "Advanced settings"
    override val advancedSettingsText = "Manage covers used in the app"
    override val reloadCoversTitle = "Reload covers"
    override val reloadCoversText = "Reload covers from files data"
    override val reloadMusicsCovers = "Reload songs cover"
    override val deletePlaylistsCovers = "Delete playlists covers"
    override val reloadAlbumsCovers = "Reload albums covers"
    override val reloadArtistsCovers = "Reload artists covers"

    override val splitMultipleArtistsTitle = "Split artists"
    override val splitMultipleArtistsText = "Select and split artists composed of multiple artists"

    override val artistCoverMethodTitle: String = "Artists covers source"
    override val artistCoverMethodText: String = "Select artists cover retrieve mode"
    override val activateArtistCoverMethod: String = "Enable fetch by path"
    override val artistCoverMethodDynamicNameTitle: String = "Artist name processing"

    override val coverFolderRetrieverPathSelectionTitle: String = "Select parent folder"
    override val coverFolderRetrieverPathSelectionNoPathSelected: String = "No folder selected"

    override val coverFolderRetrieverFileExtension: String = "Image extension"

    override val coverFolderRetrieverFolderTitle: String = "Dynamic folder"
    override val coverFolderRetrieverFolderText: String = "The folder name corresponds to the artist's name"
    override val coverFolderRetrieverFolderIncomplete: String = "The required data is incomplete"
    override val coverFolderRetrieverRulesWhiteSpace: String = "Replace whitespaces"
    override val coverFolderRetrieverRulesReplacement: String = "Replacement"
    override val coverFolderRetrieverRulesDynamicNameUppercase: String = "Uppercase"
    override val coverFolderRetrieverRulesDynamicNameNoTreatment: String = "No treatment"
    override val coverFolderRetrieverRulesDynamicNameLowercase: String = "Lowercase"

    override val coverFolderRetrieverFileTitle: String = "Dynamic cover file"
    override val coverFolderRetrieverFileText: String = "The image file name corresponds to the artist's name"

    override val coverFolderRetrieverDynamicFileTitle: String = "Cover file name"

    override val activateGithubReleaseFetchTitle = "Enable checking for new versions from GitHub"
    override val activateGithubReleaseFetchHint = "You will receive an in-app notification when a new version of the app is available on GitHub."
    override val activateGithubReleaseFetchText = "Do you want to receive an in-app notification when a new version of the app is available on GitHub?"
    override val goToSettings = "Go to settings"

    override val manageMusicsTitle = "Manage songs"
    override val manageMusicsText = "Modify and add new songs, manage used folders"
    override val modifyMusicFileTitle = "Modify files on the device"
    override val modifyMusicFileText =
        "Modifying song's information will modify the metadata of the file on your device"
    override val colorThemeTitle = "Color theme"
    override val colorThemeText = "Dynamic theme, personalized theme"
    override val personalizationTitle = "Personalization"
    override val personalizationText = "Manage player and main page view"
    override val aboutTitle = "About Soul Searching"
    override val aboutText = "Developers, application's version name"
    override val newReleaseAvailableTitle = "New release available!"
    override val statisticsTitle = "Statistics"
    override val statisticsText = "Statistics about your plays"
    override val mostPlayedSongs = "Most played songs"
    override val mostPlayedAlbums = "Most played albums"
    override val mostPlayedArtists = "Most played artists"
    override val artistsWithMostSongs = "Artists with the most songs"
    override val mostPlayedPlaylists = "Most played playlists"
    override val themeSelectionTitle = "Principal theme"
    override val themeSelectionText = "Choose the principal theme of the app"
    override val forceDarkThemeTitle = "Force dark theme"
    override val forceLightThemeTitle = "Force light theme"
    override val steelTheme = "Steel"
    override val duskTheme = "Dusk"
    override val greeneryTheme = "Greenery"
    override val treeBarkTheme = "Tree bark"
    override val extractedColorTitle = "Extracted color from cover"
    override val extractedColorText = "Define the seed used to generate a dynamic color theme"
    override val extractedColorInfo = "The Dominant color will be used if the selected color couldn't be extracted."

    override val mainPageTitle = "Main page"
    override val mainPageText = "Manage main page view"
    override val showQuickAccess = "Show quick access"
    override val showPlaylists = "Show playlists"
    override val showAlbums = "Show albums"
    override val showArtists = "Show artists"
    override val manageMusicsViewText = "Manage songs view"
    override val showMusicsByFolders = "Show songs by folders"
    override val showMusicsByMonths = "Show songs by months"
    override val showAlbumTrackNumber = "Show tracks number"
    override val useVerticalAccessBarTitle = "Use vertical access bar"
    override val useHorizontalAccessBarText = "Use horizontal access bar"


    override val manageAlbumViewTitle = "Album view"
    override val manageAlbumViewText = "Manage album view"
    override val managePlayerTitle = "Music player"
    override val managePlayerText = "Manage music player"
    override val playerSwipeTitle = "Swipe the song cover to move forward or backward in the played list"
    override val playerRewindTitle = "Rewind the current song before playing the previous one"
    override val playerMinimisedProgressionTitle = "Show the current song progression on the minimized player view"
    override val soulMixSettingsTitle = "Total of songs fetched from each folder for the Soul Mix"
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
    override val projectSiteTitle = "Project's website and source code"
    override val projectSiteText = "Source code, submit a suggestion"
    override val noNewMusics = "No new songs to add!"
    override val savingNewMusics = "Saving new songs…"

    override val songsSaved = "Songs saved successfully!"
    override val searchForNewSongs = "Search for new songs"

    override val appMigration = "Updating application data"

    override val multipleArtistsTitle = "Songs with multiple artists"
    override val multipleArtistsText =
        "Songs with multiple artists were found by the application. " +
                "Choose whether you want to split these artists into multiple ones or keep them as one artist."
    override val multipleArtistsSelectionTitle = "Selected artists to split:"
    override val noMultipleArtists = "No artists to split"

    override val multipleSelection = "Multiple selection"

    override val fieldCannotBeEmpty = "Field cannot be empty"
    override val fieldMustBeANumber = "Field must be a number"

    override val playerVolume = "Player volume"

    override val anErrorOccurred = "An error occurred"
    override val albumDoesNotExists = "This album doesn't exist"
    override val artistDoesNotExists = "This artist doesn't exist"
    override val playlistDoesNotExists = "This playlist doesn't exist"
    override val folderDoesNotExists = "This folder doesn't exist"
    override val monthPlaylistDoesNotExists = "This month playlist doesn't exist"

    override val artistDetailTitle = "Artist"
    override val monthDetailTitle = "Month"
    override val folderDetailTitle = "Folder"

    override val elementDetailEdit = "Edit"
    override val elementDetailPlay = "Play"
    override val elementDetailShuffle = "Shuffle"
    override val elementDetailTitles = "Title"

    override val and: String = "and"

    override fun musics(total: Int): String {
        return when (total) {
            0 -> "No songs"
            1 -> "1 song"
            else -> "$total songs"
        }
    }

    override fun deletedMusicsAutomatically(total: Int): String =
        when (total) {
            0 -> "No songs deleted"
            1 -> "1 song was deleted because it don't exist anymore on the device"
            else -> "$total songs were deleted because they don't exist anymore on the device"
        }

    override fun plays(total: Int): String =
        when (total) {
            0 -> "No plays"
            1 -> "1 play"
            else -> "$total plays"
        }

    override fun fetchingMusicsFrom(path: String): String =
        "Fetching songs in: $path"

    override fun selectedElements(total: Int): String =
        when (total) {
            0 -> "No selected elements"
            1 -> "1 selected element"
            else -> "$total selected elements"
        }

    override fun newReleaseAvailableText(releaseName: String): String =
        "A new release is available, ready to be downloaded: $releaseName"

    override fun artistCoverMethodExampleTitle(artist: String): String =
        "Path example with artist $artist:"
    override fun colorPaletteSeed(seed: ColorPaletteSeed): String =
        when (seed) {
            ColorPaletteSeed.DarkVibrant -> "Dark vibrant"
            ColorPaletteSeed.DarkMuted -> "Dark muted"
            ColorPaletteSeed.LightMuted -> "Light muted"
            ColorPaletteSeed.LightVibrant -> "Light vibrant"
            ColorPaletteSeed.Dominant -> "Dominant"
            ColorPaletteSeed.Muted -> "Muted"
            ColorPaletteSeed.Vibrant -> "Vibrant"
        }
    override fun hours(hours: Long): String =
        if (hours == 1L) "hour" else "hours"
}