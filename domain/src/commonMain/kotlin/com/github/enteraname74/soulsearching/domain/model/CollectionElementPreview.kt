package com.github.enteraname74.soulsearching.domain.model

import kotlin.uuid.Uuid

sealed interface CollectionElementPreview {
    val id: Uuid
    val name: String
    val cover: Cover?

    data class Album(val preview: AlbumPreview) : CollectionElementPreview {
        override val id: Uuid = preview.id
        override val name: String = preview.name
        override val cover: Cover? = preview.cover
    }

    data class Artist(val preview: ArtistPreview) : CollectionElementPreview {
        override val id: Uuid = preview.id
        override val name: String = preview.name
        override val cover: Cover? = preview.cover
    }
}
