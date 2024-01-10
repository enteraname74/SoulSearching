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
    val shuffleButton: String
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
    val deleteMusicDialogTitle: String
    val deleteAlbumDialogTitle: String
    val deletePlaylistDialogTitle: String
    val deleteArtistDialogTitle: String

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
    override val headerBarRightButton = "Bouton droit de la bar d'état"
    override val image = "Image"
    override val moreButton = "Bouton pour plus d'options"
    override val more = "Plus"
    override val settingsAccessButton = "Bouton d'accès aux paramètres de l'application"
    override val shuffleButton = "Bouton pour jouer une liste de musique en aléatoire"
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
    override val deleteMusicDialogTitle = "Are you sure to delete this song?"
    override val deleteAlbumDialogTitle = "Voulez-vous vraiment supprimer cet album ?"
    override val deletePlaylistDialogTitle = "Voulez-vous vraiment supprimer cette playlist ?"
    override val deleteArtistDialogTitle = "Voulez-vous vraiment supprimer cet artiste ?"

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

    override fun musics(total: Int): String {
        return if (total == 1) {
            "musique"
        } else {
            "musiques"
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
    override val headerBarRightButton = "Header bar right button"
    override val image = "Image"
    override val moreButton = "More button"
    override val more = "More"
    override val settingsAccessButton = "Settings access button"
    override val shuffleButton = "Shuffle button"
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
    override val deleteMusicDialogTitle = "Voulez-vous vraiment supprimer cette musique ?"
    override val deleteAlbumDialogTitle = "Are you sure to delete this album?"
    override val deletePlaylistDialogTitle = "Are you sure to delete this playlist?"
    override val deleteArtistDialogTitle = "Are you sure to delete this artist?"

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

    override fun musics(total: Int): String {
        return if (total == 1) {
            "song"
        } else {
            "songs"
        }
    }
}