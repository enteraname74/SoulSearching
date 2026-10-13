package com.github.enteraname74.localdb.model.collection

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.github.enteraname74.localdb.model.RoomAlbum
import kotlin.uuid.Uuid

@Entity(
    primaryKeys = ["collectionId", "albumId"],
    foreignKeys = [
        ForeignKey(
            entity = RoomCollection::class,
            parentColumns = ["collectionId"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RoomAlbum::class,
            parentColumns = ["albumId"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("albumId"),
    ],
)
data class RoomCollectionAlbum(
    val collectionId: Uuid,
    val albumId: Uuid,
)
