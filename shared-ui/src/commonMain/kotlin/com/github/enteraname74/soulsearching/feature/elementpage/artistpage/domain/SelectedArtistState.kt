package com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.PlaylistWithMusics

/**
 * State for managing a selected artist.
 */
data class SelectedArtistState(
    val artistWithMusics: ArtistWithMusics = ArtistWithMusics(),
    val artistAlbums: List<Album> = emptyList(),
    val allPlaylists: List<PlaylistWithMusics> = emptyList(),
    val isDeleteMusicDialogShown: Boolean = false,
    val isMusicBottomSheetShown: Boolean = false,
    val isAddToPlaylistBottomSheetShown: Boolean = false,
    val isAlbumBottomSheetShown: Boolean = false,
    val isDeleteAlbumDialogShown: Boolean = false,
)