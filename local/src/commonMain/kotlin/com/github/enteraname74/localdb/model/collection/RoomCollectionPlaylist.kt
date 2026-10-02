package com.github.enteraname74.localdb.model.collection

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.github.enteraname74.localdb.model.RoomPlaylist
import kotlin.uuid.Uuid

@Entity(
    primaryKeys = ["collectionId", "playlistId"],
    foreignKeys = [
        ForeignKey(
            entity = RoomCollection::class,
            parentColumns = ["collectionId"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RoomPlaylist::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("playlistId"),
    ],
)
data class RoomCollectionPlaylist(
    val collectionId: Uuid,
    val playlistId: Uuid,
)