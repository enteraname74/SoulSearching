package com.github.enteraname74.soulsearching.repository.model

import com.github.enteraname74.domain.model.Music
import java.util.UUID

data class MusicWithAlbumId(
    val music: Music,
    val albumId: UUID?,
)
