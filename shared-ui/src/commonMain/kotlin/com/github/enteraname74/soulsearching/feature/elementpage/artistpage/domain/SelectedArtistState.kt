package com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.PlaylistWithMusics

/**
 * State for managing a selected artist.
 */
data class SelectedArtistState(
    val artistWithMusics: ArtistWithMusics = ArtistWithMusics(),
    val artistAlbums: List<Album> = emptyList(),
    val allPlaylists: List<PlaylistWithMusics> = emptyList(),
)