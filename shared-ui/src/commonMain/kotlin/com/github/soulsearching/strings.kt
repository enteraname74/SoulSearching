package com.github.soulsearching

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

/**
 * French translation for application strings
 */
object FrStrings : Strings {
    override val appLogo = "Logo de l'application"
    override val noElements = "Aucun élément"
    override val backButton = "Bouton de retour"
    override val createPlaylistButton = "Bouton pour créer une playlist"
    override val headerBarRightButton = "Bouton droit de la bar d'état"
    override val image = "Image"
    override val moreButton = "Bouton pour plus d'options"
    override val more = "Plus"
    override val settingsAccessButton = "Bouton d'accès aux paramètres de l'application"
    override val shuffleButton = "Bouton pour jouer une liste de musique en aléatoire"
    override val favorite = "Favoris"
    override val missingPermissions = "Vous devez accepter toutes les permissions de l'application pour que cette dernière fonctionne correctement."
    override val playedList = "Liste jouée"

    override val completeApplicationTitle = "Une application de musique complète"
    override val completeApplicationText = "Écoutez toutes vos musiques, tous vos albums, artistes et playlists."
    override val quickAccessTitle = "Gardez ce que vous aimez proche de vous"
    override val quickAccessText = "Avec les accès rapides, vous pouvez facilement accéder à votre album favori, ou à vos musiques préférées par exemple."
    override val modifyElementsTitle = "Modifiez vos éléments musicaux"
    override val modifyElementsText = "Vous pouvez modifier les informations d'un album, d'un artiste, d'une musique…"
    override val dynamicThemeFeatureTitle = "Thème dynamique"
    override val dynamicThemeFeatureText = "Soul Searching vous offre la possibilité d'avoir un thème dynamique dépendant de la musique en cours de lecture"
    override val manageFoldersTitle = "Gérez vos dossiers"
    override val manageFoldersText = "Définissez quels dossiers sont utilisés par l'application."
    override val addNewMusicsTitle = "Ajoutez de nouvelles musiques"
    override val addNewMusicsText = "Vous pouvez facilement ajouter de nouvelles musiques provenant de votre appareil."
    override val personalizeMainPageTitle = "Définissez ce que vous voulez réellement"
    override val personalizeMainPageText = "Vous ne voulez pas avoir les accès rapides ou vous voudriez avoir une barre vous aidant à accéder à des éléments du menu principal ? Vous pouvez réaliser vos envies dans les paramètres de l'application."

    override val searchingSongsFromYourDevice = "Recherche de musiques sur votre appareil…"
    override val searchForMusics = "Rechercher une musique"
    override val searchAll = "Rechercher une musique, un album, un artiste, une playlist…"

    override val artists = "Artistes"
    override val musics = "Musiques"
    override val quickAccess = "Accès rapides"

    override val sortByDateAdded = "Trier par data d'ajout"
    override val sortByMostListened = "Trier par le nombre d'écoutes"
    override val sortByName = "Trier par nom"
    override val sortByAscOrDesc = "Tri ascendant ou descendant"

    override val removeFromQuickAccess = "Retirer des accès rapides"
    override val removeFromPlaylist = "Retirer de la playlist"
    override val removeFromPlayedList = "Retirer de la liste jouée"
    override val addToQuickAccess = "Ajouter aux accès rapides"
    override val addToPlaylist = "Ajouter à une playlist"

    override val create = "Créer"
    override val cancel = "Annuler"
    override val delete = "Supprimer"

    override val createPlaylistDialogTitle = "Créer une nouvelle playlist"
    override val playlistName = "Nom de la playlist"
    override val musicName = "Nom de la musique"
    override val albumName = "Nom de l'album"
    override val artistName = "Nom de l'artiste"
    override val playlistCover = "Couverture de la playlist"
    override val albumCover = "Couverture de l'album"
    override val artistCover = "Couverture de l'artiste"
    override val playlistInformation = "Informations de la playlist"
    override val musicInformation = "Informations de la musique"
    override val artistInformation = "Informations de l'artiste"
    override val albumInformation = "Informations de l'album"

    override val deleteMusicDialogTitle = "Voulez-vous vraiment supprimer cette musique ?"
    override val deleteMusicDialogText: String = "Elle sera supprimée de l'application."
    override val deleteAlbumDialogTitle = "Voulez-vous vraiment supprimer cet album ?"
    override val deletePlaylistDialogTitle = "Voulez-vous vraiment supprimer cette playlist ?"
    override val deleteArtistDialogTitle = "Voulez-vous vraiment supprimer cet artiste ?"
    override val removeMusicFromPlaylistTitle = "Voulez-vous vraiment supprimer cette musique de cette playlist ?"
    override val removeMusicFromPlaylistText = "Cette musique sera retirée de cette playlist mais ne sera pas supprimée de l'application."

    override val modifyAlbum = "Modifier cet album"
    override val modifyArtist = "Modifier cet artiste"
    override val modifyMusic = "Modifier cette musique"
    override val modifyPlaylist = "Modifier cette playlist"

    override val deleteAlbum = "Supprimer cet album"
    override val deleteArtist = "Supprimer cet artiste"
    override val deleteMusic = "Supprimer cette musique"
    override val deletePlaylist = "Supprimer cette playlist"

    override val playNext = "Jouer ensuite"

    override val personalizedThemeTitle = "Thème personnalisé"
    override val personalizedThemeText = "Définissez précisémment votre thème de couleur"
    override val dynamicPlayerView = "Utiliser le thème dynamique pour la vue du player"
    override val dynamicPlaylistView = "Utiliser le thème dynamique pour la vue des playlists, albums et artistes"
    override val dynamicOtherView = "Utiliser le thème dynamique pour les autres vues"

    override val settings = "Paramètres"
    override val manageMusicsTitle = "Gérer les musiques"
    override val manageMusicsText = "Ajouter de nouvelles musiques et gérer les dossiers utilisés"
    override val colorThemeTitle = "Thème de couleur"
    override val colorThemeText = "Thème dynamique, personnalisé"
    override val personalizationTitle = "Personnalisation"
    override val personalizationText = "Gérer la vue de la page principale"
    override val aboutTitle = "À propos de Soul Searching"
    override val aboutText = "Développeurs, nom de la version de l'application"

    override val mainPageTitle = "Page principale"
    override val mainPageText = "Gérer la vue de la page principale"
    override val showQuickAccess = "Afficher les accès rapides"
    override val showPlaylists = "Afficher les playlists"
    override val showAlbums = "Afficher les albums"
    override val showArtists = "Afficher les artistes"
    override val showVerticalAccessBarTitle = "Afficher la barre d'accès verticale"
    override val showVerticalAccessBarText = "Donne un accès rapide à tous les éléments de la page principale"

    override val usedFoldersTitle = "Dossiers utilisés"
    override val usedFoldersText = "Définissez les dossiers utilisés par l'application"
    override val addMusicsTitle = "Ajouter des musiques"
    override val addMusicsText = "Ajouter des nouvelles musiques de votre téléphone dans l'application"
    override val fetchingFolders = "Récupération des dossiers"
    override val deletingMusicsFromUnselectedFolders = "Suppression des musiques des dossiers désélectionnés…"

    override val dynamicThemeTitle = "Thème dynamique"
    override val dynamicThemeText = "Thème de couleur global basé sur la pochette d\\'album de la musique jouée courante"
    override val systemThemeTitle = "Thème du système"
    override val systemThemeText = "Thème de couleur global basé sur le mode de couleur de votre système"

    override val leadDeveloper = "Développeur principal"
    override val developersTitle = "Développeurs"
    override val developersText = "Les développeurs derrière le projet Soul Searching"
    override val versionNameTitle = "Nom de la version de l'application"

    override val noNewMusics = "Aucune nouvelle musique à ajouter !"
    override val savingNewMusics = "Sauvegarde des nouvelles musiques…"

    override fun musics(total: Int): String {
        return when (total) {
            1 -> "1 musique"
            0 -> "Aucune musique"
            else -> "$total musiques"
        }
    }
}

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