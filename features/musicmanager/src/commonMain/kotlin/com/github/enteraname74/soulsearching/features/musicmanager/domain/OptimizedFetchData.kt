package com.github.enteraname74.soulsearching.features.musicmanager.domain

import com.github.enteraname74.domain.model.*
import java.util.ArrayList
import java.util.HashMap

/**
 * Holds optimized data access to fetched elements for quick access.
 */
data class OptimizedFetchData(
    var musicsByPath: HashMap<String, Music> = hashMapOf(),
    var artistsByName: HashMap<String, Artist> = hashMapOf(),
    var albumsByInfo: HashMap<AlbumInformation, Album> = hashMapOf(),
    val albumArtists: ArrayList<AlbumArtist> = arrayListOf(),
    val musicArtists: ArrayList<MusicArtist> = arrayListOf(),
    val musicAlbums: ArrayList<MusicAlbum> = arrayListOf(),
) {
    fun clear() {
        musicsByPath.clear()
        artistsByName.clear()
        albumsByInfo.clear()
        albumArtists.clear()
        musicArtists.clear()
        musicAlbums.clear()
    }
}
