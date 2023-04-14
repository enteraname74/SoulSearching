package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.soulsearching.database.model.AlbumArtist
import java.util.*

@Dao
interface AlbumArtistDao {
    @Upsert
    suspend fun insertAlbumIntoArtist(albumArtist: AlbumArtist)

    @Query("DELETE FROM AlbumArtist WHERE albumId = :albumId")
    suspend fun deleteAlbumFromArtist(albumId: UUID)
}