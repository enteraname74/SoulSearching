package com.github.enteraname74.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.data.model.AlbumArtist
import java.util.*

@Dao
internal interface AlbumArtistDao {
    @Upsert
    suspend fun insertAlbumIntoArtist(albumArtist: AlbumArtist)

    @Query("UPDATE AlbumArtist SET artistId = :newArtistId WHERE albumId = :albumId")
    suspend fun updateArtistOfAlbum(albumId: UUID, newArtistId: UUID)

    @Query("DELETE FROM AlbumArtist WHERE albumId = :albumId")
    suspend fun deleteAlbumFromArtist(albumId: UUID)
}