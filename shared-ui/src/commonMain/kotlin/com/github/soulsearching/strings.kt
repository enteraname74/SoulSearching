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
    val noElements: String
    val backButton: String
    val headerBarRightButton: String
    val image: String

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

}

/**
 * French translation for application strings
 */
object FrStrings : Strings {
    override val noElements = "Aucun élément"
    override val backButton = "Bouton de retour"
    override val headerBarRightButton = "Bouton droit de la bar d'état"
    override val image = "Image"

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

}

/**
 * English translation for application strings
 */
object EnStrings : Strings {
    override val noElements = "No elements"
    override val backButton = "Back button"
    override val headerBarRightButton = "Header bar right button"
    override val image = "Image"

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
}