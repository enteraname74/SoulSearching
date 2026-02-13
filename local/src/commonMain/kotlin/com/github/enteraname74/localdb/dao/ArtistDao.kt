package com.github.enteraname74.localdb.dao

import androidx.room.*
import com.github.enteraname74.localdb.model.RoomAlbumPreview
import com.github.enteraname74.localdb.model.RoomArtist
import com.github.enteraname74.localdb.model.RoomArtistPreview
import com.github.enteraname74.localdb.model.RoomArtistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * DAO of an Artist.
 */
@Dao
interface ArtistDao {

    @Upsert
    suspend fun upsert(roomArtist: RoomArtist)

    @Upsert
    suspend fun upsertAll(roomArtists: List<RoomArtist>)

    @Delete
    suspend fun delete(roomArtist: RoomArtist)

    @Query("DELETE FROM RoomArtist WHERE artistId IN (:ids)")
    suspend fun deleteAll(ids: List<UUID>)

    @Query(
        """
            DELETE FROM RoomArtist
            WHERE (SELECT COUNT(*) FROM RoomMusicArtist WHERE RoomMusicArtist.artistId = RoomArtist.artistId) = 0
        """
    )
    suspend fun deleteAllEmpty()

    @Query("UPDATE RoomArtist SET coverFolderKey = :key")
    suspend fun activateCoverFolderMode(key: String)

    @Query("UPDATE RoomArtist SET coverFolderKey = NULL")
    suspend fun deactivateCoverFolderMode()

    @Query("SELECT artistName FROM RoomArtist WHERE LOWER(artistName) LIKE LOWER('%' || :search || '%')")
    suspend fun getArtistNamesContainingSearch(search: String): List<String>

    @Query("SELECT * FROM RoomArtist WHERE artistId = :artistId")
    fun getFromId(artistId: UUID): Flow<RoomArtist?>

    @Query("SELECT * FROM RoomArtist ORDER BY artistName ASC")
    fun getAll(): Flow<List<RoomArtist>>

    @Transaction
    @Query("SELECT * FROM RoomArtist ORDER BY artistName ASC")
    fun getAllArtistWithMusics(): Flow<List<RoomArtistWithMusics>>

    @Query("SELECT * FROM RoomArtist WHERE artistName = :artistName LIMIT 1")
    suspend fun getFromName(artistName: String): RoomArtist?

    @Query("SELECT * FROM RoomArtist WHERE artistName in (:artistsNames)")
    suspend fun getAllFromName(artistsNames: List<String>): List<RoomArtist>

    @Transaction
    @Query("SELECT * FROM RoomArtist WHERE artistId = :artistId")
    fun getArtistWithMusics(artistId: UUID): Flow<RoomArtistWithMusics?>

    @Query(
        """
            SELECT artist.* FROM RoomArtist AS artist INNER JOIN RoomMusicArtist 
            WHERE artist.artistId = RoomMusicArtist.artistId 
            AND RoomMusicArtist.musicId = :musicId
        """
    )
    fun getArtistsOfMusic(musicId: UUID): Flow<List<RoomArtist>>

    @Transaction
    @Query(
        """
            SELECT artist.artistId AS id, 
            artist.artistName AS name, 
            artist.coverFolderKey,
            (SELECT COUNT(*) FROM RoomMusicArtist AS musicArtist WHERE musicArtist.artistId = artist.artistId) AS totalMusics, 
            (
                CASE WHEN artist.coverId IS NULL THEN 
                    (
                        SELECT music.coverId FROM RoomMusic AS music 
                        INNER JOIN RoomMusicArtist AS musicArtist 
                        ON music.musicId = musicArtist.musicId 
                        AND artist.artistId = musicArtist.artistId 
                        AND music.isHidden = 0 
                        AND music.coverId IS NOT NULL 
                        LIMIT 1
                    )
                ELSE artist.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                INNER JOIN RoomMusicArtist AS musicArtist 
                ON music.musicId = musicArtist.musicId 
                AND artist.artistId = musicArtist.artistId 
                AND music.isHidden = 0 
                LIMIT 1
            ) AS musicCoverPath,
            artist.isInQuickAccess 
            FROM RoomArtist AS artist 
            WHERE artist.isInQuickAccess = 1
        """
    )
    fun getAllFromQuickAccess(): Flow<List<RoomArtistPreview>>
}