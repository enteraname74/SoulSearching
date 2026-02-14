package com.github.enteraname74.localdb.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.github.enteraname74.localdb.model.RoomCompleteMusic
import com.github.enteraname74.localdb.model.RoomPlaylist
import com.github.enteraname74.localdb.model.RoomPlaylistPreview
import com.github.enteraname74.localdb.model.RoomPlaylistWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * DAO of a Playlist.
 */
@Dao
interface PlaylistDao {

    @Upsert
    suspend fun upsert(roomPlaylist : RoomPlaylist)

    @Upsert
    suspend fun upsertAll(roomPlaylists : List<RoomPlaylist>)

    @Delete
    suspend fun delete(roomPlaylist : RoomPlaylist)

    @Query("DELETE FROM RoomPlaylist WHERE playlistId IN (:ids) AND isFavorite=0")
    suspend fun deleteAll(ids: List<UUID>)

    @Transaction
    @Query("SELECT * FROM RoomPlaylist ORDER BY name ASC")
    fun getAllPlaylistWithMusics(): Flow<List<RoomPlaylistWithMusics>>

    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getFromId(playlistId: UUID) : Flow<RoomPlaylist?>

    @Transaction
    @Query("SELECT * FROM RoomPlaylist WHERE playlistId = :playlistId")
    fun getPlaylistWithMusics(playlistId : UUID): Flow<RoomPlaylistWithMusics?>

    @Query("UPDATE RoomPlaylist SET coverId = NULL")
    suspend fun cleanAllCovers()

    @Transaction
    @Query(
        """
            SELECT playlist.playlistId AS id, 
            playlist.name, 
            playlist.isFavorite,
            (
                SELECT COUNT(*) 
                FROM RoomMusicPlaylist AS musicPlaylist 
                WHERE musicPlaylist.playlistId = playlist.playlistId
            ) AS totalMusics, 
            (
                CASE WHEN playlist.coverId IS NULL THEN 
                    (
                        SELECT music.coverId FROM RoomMusic AS music 
                        INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                        ON music.musicId = musicPlaylist.musicId 
                        AND playlist.playlistId = musicPlaylist.playlistId 
                        AND music.isHidden = 0 
                        AND music.coverId IS NOT NULL 
                        LIMIT 1
                    )
                ELSE playlist.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                ON music.musicId = musicPlaylist.musicId 
                AND playlist.playlistId = musicPlaylist.playlistId 
                AND music.isHidden = 0 
                LIMIT 1
            ) AS musicCoverPath,
            playlist.isInQuickAccess, 
            playlist.nbPlayed 
            FROM RoomPlaylist AS playlist 
            ORDER BY name ASC
        """
    )
    fun getAllPagedByNameAsc(): PagingSource<Int, RoomPlaylistPreview>

    @Transaction
    @Query(
        """
            SELECT playlist.playlistId AS id, 
            playlist.name, 
            playlist.isFavorite,
            (
                SELECT COUNT(*) 
                FROM RoomMusicPlaylist AS musicPlaylist 
                WHERE musicPlaylist.playlistId = playlist.playlistId
            ) AS totalMusics, 
            (
                CASE WHEN playlist.coverId IS NULL THEN 
                    (
                        SELECT music.coverId FROM RoomMusic AS music 
                        INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                        ON music.musicId = musicPlaylist.musicId 
                        AND playlist.playlistId = musicPlaylist.playlistId 
                        AND music.isHidden = 0 
                        AND music.coverId IS NOT NULL 
                        LIMIT 1
                    )
                ELSE playlist.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                ON music.musicId = musicPlaylist.musicId 
                AND playlist.playlistId = musicPlaylist.playlistId 
                AND music.isHidden = 0 
                LIMIT 1
            ) AS musicCoverPath,
            playlist.isInQuickAccess, 
            playlist.nbPlayed 
            FROM RoomPlaylist AS playlist 
            ORDER BY name DESC
        """
    )
    fun getAllPagedByNameDesc(): PagingSource<Int, RoomPlaylistPreview>

    @Transaction
    @Query(
        """
            SELECT playlist.playlistId AS id, 
            playlist.name, 
            playlist.isFavorite,
            (
                SELECT COUNT(*) 
                FROM RoomMusicPlaylist AS musicPlaylist 
                WHERE musicPlaylist.playlistId = playlist.playlistId
            ) AS totalMusics, 
            (
                CASE WHEN playlist.coverId IS NULL THEN 
                    (
                        SELECT music.coverId FROM RoomMusic AS music 
                        INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                        ON music.musicId = musicPlaylist.musicId 
                        AND playlist.playlistId = musicPlaylist.playlistId 
                        AND music.isHidden = 0 
                        AND music.coverId IS NOT NULL 
                        LIMIT 1
                    )
                ELSE playlist.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                ON music.musicId = musicPlaylist.musicId 
                AND playlist.playlistId = musicPlaylist.playlistId 
                AND music.isHidden = 0 
                LIMIT 1
            ) AS musicCoverPath,
            playlist.isInQuickAccess, 
            playlist.nbPlayed 
            FROM RoomPlaylist AS playlist 
            ORDER BY addedDate ASC
        """
    )
    fun getAllPagedByDateAsc(): PagingSource<Int, RoomPlaylistPreview>

    @Transaction
    @Query(
        """
            SELECT playlist.playlistId AS id, 
            playlist.name, 
            playlist.isFavorite,
            (
                SELECT COUNT(*) 
                FROM RoomMusicPlaylist AS musicPlaylist 
                WHERE musicPlaylist.playlistId = playlist.playlistId
            ) AS totalMusics, 
            (
                CASE WHEN playlist.coverId IS NULL THEN 
                    (
                        SELECT music.coverId FROM RoomMusic AS music 
                        INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                        ON music.musicId = musicPlaylist.musicId 
                        AND playlist.playlistId = musicPlaylist.playlistId 
                        AND music.isHidden = 0 
                        AND music.coverId IS NOT NULL 
                        LIMIT 1
                    )
                ELSE playlist.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                ON music.musicId = musicPlaylist.musicId 
                AND playlist.playlistId = musicPlaylist.playlistId 
                AND music.isHidden = 0 
                LIMIT 1
            ) AS musicCoverPath,
            playlist.isInQuickAccess, 
            playlist.nbPlayed 
            FROM RoomPlaylist AS playlist 
            ORDER BY addedDate DESC
        """
    )
    fun getAllPagedByDateDesc(): PagingSource<Int, RoomPlaylistPreview>

    @Transaction
    @Query(
        """SELECT playlist.playlistId AS id, 
            playlist.name, 
            playlist.isFavorite,
            (
                SELECT COUNT(*) 
                FROM RoomMusicPlaylist AS musicPlaylist 
                WHERE musicPlaylist.playlistId = playlist.playlistId
            ) AS totalMusics, 
            (
                CASE WHEN playlist.coverId IS NULL THEN 
                    (
                        SELECT music.coverId FROM RoomMusic AS music 
                        INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                        ON music.musicId = musicPlaylist.musicId 
                        AND playlist.playlistId = musicPlaylist.playlistId 
                        AND music.isHidden = 0 
                        AND music.coverId IS NOT NULL 
                        LIMIT 1
                    )
                ELSE playlist.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                ON music.musicId = musicPlaylist.musicId 
                AND playlist.playlistId = musicPlaylist.playlistId 
                AND music.isHidden = 0 
                LIMIT 1
            ) AS musicCoverPath,
            playlist.isInQuickAccess, 
            playlist.nbPlayed 
            FROM RoomPlaylist AS playlist 
            ORDER BY nbPlayed ASC
        """
    )
    fun getAllPagedByNbPlayedAsc(): PagingSource<Int, RoomPlaylistPreview>

    @Transaction
    @Query(
        """
            SELECT playlist.playlistId AS id, 
            playlist.name, 
            playlist.isFavorite,
            (
                SELECT COUNT(*) 
                FROM RoomMusicPlaylist AS musicPlaylist 
                WHERE musicPlaylist.playlistId = playlist.playlistId
            ) AS totalMusics, 
            (
                CASE WHEN playlist.coverId IS NULL THEN 
                    (
                        SELECT music.coverId FROM RoomMusic AS music 
                        INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                        ON music.musicId = musicPlaylist.musicId 
                        AND playlist.playlistId = musicPlaylist.playlistId 
                        AND music.isHidden = 0 
                        AND music.coverId IS NOT NULL 
                        LIMIT 1
                    )
                ELSE playlist.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                ON music.musicId = musicPlaylist.musicId 
                AND playlist.playlistId = musicPlaylist.playlistId 
                AND music.isHidden = 0 
                LIMIT 1
            ) AS musicCoverPath,
            playlist.isInQuickAccess, 
            playlist.nbPlayed  
            FROM RoomPlaylist AS playlist 
            ORDER BY nbPlayed DESC
        """
    )
    fun getAllPagedByNbPlayedDesc(): PagingSource<Int, RoomPlaylistPreview>

    @Transaction
    @Query(
        """
            SELECT playlist.playlistId AS id, 
            playlist.name, 
            playlist.isFavorite,
            (
                SELECT COUNT(*) 
                FROM RoomMusicPlaylist AS musicPlaylist 
                WHERE musicPlaylist.playlistId = playlist.playlistId
            ) AS totalMusics, 
            (
                CASE WHEN playlist.coverId IS NULL THEN 
                    (
                        SELECT music.coverId FROM RoomMusic AS music 
                        INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                        ON music.musicId = musicPlaylist.musicId 
                        AND playlist.playlistId = musicPlaylist.playlistId 
                        AND music.isHidden = 0 
                        AND music.coverId IS NOT NULL 
                        LIMIT 1
                    )
                ELSE playlist.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                ON music.musicId = musicPlaylist.musicId 
                AND playlist.playlistId = musicPlaylist.playlistId 
                AND music.isHidden = 0 
                LIMIT 1
            ) AS musicCoverPath,
            playlist.isInQuickAccess, 
            playlist.nbPlayed 
            FROM RoomPlaylist AS playlist 
            WHERE playlist.isInQuickAccess = 1
        """
    )
    fun getAllFromQuickAccess(): Flow<List<RoomPlaylistPreview>>

    @Transaction
    @Query(
        """
            SELECT playlist.playlistId AS id, 
            playlist.name, 
            playlist.isFavorite,
            (
                SELECT COUNT(*) 
                FROM RoomMusicPlaylist AS musicPlaylist 
                WHERE musicPlaylist.playlistId = playlist.playlistId
            ) AS totalMusics, 
            (
                CASE WHEN playlist.coverId IS NULL THEN 
                    (
                        SELECT music.coverId FROM RoomMusic AS music 
                        INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                        ON music.musicId = musicPlaylist.musicId 
                        AND playlist.playlistId = musicPlaylist.playlistId 
                        AND music.isHidden = 0 
                        AND music.coverId IS NOT NULL 
                        LIMIT 1
                    )
                ELSE playlist.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                ON music.musicId = musicPlaylist.musicId 
                AND playlist.playlistId = musicPlaylist.playlistId 
                AND music.isHidden = 0 
                LIMIT 1
            ) AS musicCoverPath,
            playlist.isInQuickAccess,
            playlist.nbPlayed 
            FROM RoomPlaylist AS playlist 
            WHERE nbPlayed >= 1 
            ORDER BY nbPlayed DESC 
            LIMIT 11
        """
    )
    fun getMostListened(): Flow<List<RoomPlaylistPreview>>

    @Transaction
    @Query(
        """
            SELECT playlist.playlistId AS id, 
            playlist.name, 
            playlist.isFavorite,
            (
                SELECT COUNT(*) 
                FROM RoomMusicPlaylist AS musicPlaylist 
                WHERE musicPlaylist.playlistId = playlist.playlistId
            ) AS totalMusics, 
            (
                CASE WHEN playlist.coverId IS NULL THEN 
                    (
                        SELECT music.coverId FROM RoomMusic AS music 
                        INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                        ON music.musicId = musicPlaylist.musicId 
                        AND playlist.playlistId = musicPlaylist.playlistId 
                        AND music.isHidden = 0 
                        AND music.coverId IS NOT NULL 
                        LIMIT 1
                    )
                ELSE playlist.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                ON music.musicId = musicPlaylist.musicId 
                AND playlist.playlistId = musicPlaylist.playlistId 
                AND music.isHidden = 0 
                LIMIT 1
            ) AS musicCoverPath,
            playlist.isInQuickAccess,
            playlist.nbPlayed 
            FROM RoomPlaylist AS playlist 
            WHERE playlist.playlistId = :playlistId 
            LIMIT 1
        """
    )
    fun getPlaylistPreview(playlistId: UUID): Flow<RoomPlaylistPreview?>

    // TODO: Normalise with accents.
    @Transaction
    @Query(
        """
            SELECT playlist.playlistId AS id, 
            playlist.name, 
            playlist.isFavorite,
            (
                SELECT COUNT(*) 
                FROM RoomMusicPlaylist AS musicPlaylist 
                WHERE musicPlaylist.playlistId = playlist.playlistId
            ) AS totalMusics, 
            (
                CASE WHEN playlist.coverId IS NULL THEN 
                    (
                        SELECT music.coverId FROM RoomMusic AS music 
                        INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                        ON music.musicId = musicPlaylist.musicId 
                        AND playlist.playlistId = musicPlaylist.playlistId 
                        AND music.isHidden = 0 
                        AND music.coverId IS NOT NULL 
                        LIMIT 1
                    )
                ELSE playlist.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                ON music.musicId = musicPlaylist.musicId 
                AND playlist.playlistId = musicPlaylist.playlistId 
                AND music.isHidden = 0 
                LIMIT 1
            ) AS musicCoverPath,
            playlist.isInQuickAccess, 
            playlist.nbPlayed 
            FROM RoomPlaylist AS playlist 
            WHERE playlist.name LIKE '%' || :search || '%' COLLATE NOCASE 
        """
    )
    fun searchAll(search: String): Flow<List<RoomPlaylistPreview>>
}