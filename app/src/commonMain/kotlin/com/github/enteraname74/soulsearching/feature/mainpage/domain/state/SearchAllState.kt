package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistPreview

data class SearchAllState(
    val musics: List<Music> = emptyList(),
    val albums: List<AlbumPreview> = emptyList(),
    val playlists: List<PlaylistPreview> = emptyList(),
    val artists: List<ArtistPreview> = emptyList(),
)
