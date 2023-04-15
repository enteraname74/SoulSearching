package com.github.soulsearching.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.github.soulsearching.database.model.MusicAlbum
import java.util.UUID

@Dao
interface MusicAlbumDao {
    @Upsert
    suspend fun insertMusicIntoAlbum(musicAlbum : MusicAlbum)

    @Query("DELETE FROM MusicAlbum WHERE musicId = :musicId")
    suspend fun deleteMusicFromAlbum(musicId : UUID)

    @Query("UPDATE MusicAlbum SET albumId = :newAlbumId WHERE musicId = :musicId")
    suspend fun updateAlbumOfMusic(musicId: UUID, newAlbumId: UUID)

    @Query("SELECT musicId FROM MusicAlbum WHERE albumId = :albumId")
    fun getMusicsIdsFromAlbumId(albumId : UUID) : List<UUID>

    @Query("SELECT albumId FROM MusicAlbum WHERE musicId = :musicId")
    fun getAlbumIdFromMusicId(musicId: UUID) : UUID?

    @Query("SELECT COUNT(*) FROM MusicAlbum WHERE albumId = :albumId")
    fun getNumberOfMusicsFromAlbum(albumId : UUID) : Int
}