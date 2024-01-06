package com.github.enteraname74.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.enteraname74.data.model.RoomMusicAlbum
import java.util.UUID

@Dao
internal interface MusicAlbumDao {
    @Upsert
    suspend fun insertMusicIntoAlbum(roomMusicAlbum : RoomMusicAlbum)

    @Query("DELETE FROM RoomMusicAlbum WHERE musicId = :musicId")
    suspend fun deleteMusicFromAlbum(musicId : UUID)

    @Query("UPDATE RoomMusicAlbum SET albumId = :newAlbumId WHERE musicId = :musicId")
    suspend fun updateAlbumOfMusic(musicId: UUID, newAlbumId: UUID)

    @Query("UPDATE RoomMusicAlbum SET albumId = :newAlbumId WHERE albumId = :legacyAlbumId")
    suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId : UUID)

    @Query("SELECT musicId FROM RoomMusicAlbum WHERE albumId = :albumId")
    fun getMusicsIdsFromAlbumId(albumId : UUID) : List<UUID>

    @Query("SELECT albumId FROM RoomMusicAlbum WHERE musicId = :musicId")
    fun getAlbumIdFromMusicId(musicId: UUID) : UUID?

    @Query("SELECT COUNT(*) FROM RoomMusicAlbum WHERE albumId = :albumId")
    fun getNumberOfMusicsFromAlbum(albumId : UUID) : Int
}