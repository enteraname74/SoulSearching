package com.github.enteraname74.soulsearching.repository.model

import com.github.enteraname74.domain.model.Playlist
import java.util.UUID

data class UploadedPlaylistResult(
    val remotePlaylist: Playlist,
    val localPlaylistId: UUID,
)
