package com.github.enteraname74.localdb.model.collection

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.github.enteraname74.localdb.model.RoomArtist
import kotlin.uuid.Uuid

@Entity(
    primaryKeys = ["collectionId", "artistId"],
    foreignKeys = [
        ForeignKey(
            entity = RoomCollection::class,
            parentColumns = ["collectionId"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RoomArtist::class,
            parentColumns = ["artistId"],
            childColumns = ["artistId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("artistId"),
    ],
)
data class RoomCollectionArtist(
    val collectionId: Uuid,
    val artistId: Uuid,
)