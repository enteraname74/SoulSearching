package com.github.enteraname74.soulsearching.remote.model.playlist

import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import com.github.enteraname74.soulsearching.repository.model.UploadedPlaylistResult
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RemoteUploadedPlaylistData(
    val playlist: RemotePlaylist,
    @Serializable(with = UUIDSerializer::class)
    val userPlaylistId: UUID,
) {
    fun toUploadedPlaylistData(): UploadedPlaylistResult = UploadedPlaylistResult(
        remotePlaylist = playlist.toPlaylist(),
        localPlaylistId = userPlaylistId,
    )
}
