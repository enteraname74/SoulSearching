package com.github.enteraname74.localdb.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.enteraname74.localdb.model.RoomCompleteMusic
import com.github.enteraname74.localdb.model.RoomMonthMusicPreview
import com.github.enteraname74.localdb.model.RoomMusic
import com.github.enteraname74.localdb.model.RoomMusicFolderPreview
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * DAO of a Music.
 */
@Dao
interface MusicDao {


    @Upsert
    suspend fun upsert(roomMusic : RoomMusic)

    @Upsert
    suspend fun upsertAll(roomMusics : List<RoomMusic>)

    @Delete
    suspend fun delete(roomMusic : RoomMusic)

    @Query("""
        DELETE FROM RoomMusic WHERE 
        folder IN (SELECT RoomFolder.folderPath FROM RoomFolder WHERE RoomFolder.isSelected = 0)
    """)
    suspend fun deleteFromUnselectedFolders()

    @Query("DELETE FROM RoomMusic WHERE musicId IN (:ids)")
    suspend fun deleteAll(ids: List<UUID>)

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE musicId = :musicId LIMIT 1")
    fun getFromId(musicId : UUID): Flow<RoomCompleteMusic?>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY name ASC")
    fun getAll(): Flow<List<RoomCompleteMusic>>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND isInQuickAccess = 1 ORDER BY name ASC")
    fun getAllFromQuickAccess(): Flow<List<RoomCompleteMusic>>

    @Query(
        """
            SELECT musicId FROM RoomMusic WHERE 
            folder IN (SELECT RoomFolder.folderPath FROM RoomFolder WHERE RoomFolder.isSelected = 0)
        """
    )
    suspend fun getAllIdsFromUnselectedFolders(): List<UUID>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY name ASC")
    fun getAllPagedByNameAsc(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY name DESC")
    fun getAllPagedByNameDesc(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY addedDate ASC")
    fun getAllPagedByDateAsc(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE isHidden = 0 
            AND albumId = :albumId
            ORDER BY addedDate ASC
        """
    )
    fun getAllPagedByDateAscOfAlbum(albumId: UUID): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE isHidden = 0 
            AND folder = :folder
            ORDER BY addedDate ASC
        """
    )
    fun getAllPagedByDateAscOfFolder(folder: String): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE isHidden = 0 
            AND strftime('%m/%Y', addedDate) = :month
            ORDER BY addedDate ASC
        """
    )
    fun getAllPagedByDateAscOfMonth(month: String): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music
            INNER JOIN RoomMusicPlaylist as musicPlaylist
            ON music.musicId = musicPlaylist.musicId 
            AND musicPlaylist.playlistId = :playlistId 
            AND music.isHidden = 0 
            ORDER BY addedDate ASC
        """
    )
    fun getAllPagedByDateAscOfPlaylist(playlistId: UUID): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music
            INNER JOIN RoomMusicArtist as musicArtist
            ON music.musicId = musicArtist.musicId 
            AND musicArtist.artistId = :artistId 
            AND music.isHidden = 0 
            ORDER BY addedDate ASC
        """
    )
    fun getAllPagedByDateAscOfArtist(artistId: UUID): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY addedDate DESC")
    fun getAllPagedByDateDesc(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY nbPlayed ASC")
    fun getAllPagedByNbPlayedAsc(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 ORDER BY nbPlayed DESC")
    fun getAllPagedByNbPlayedDesc(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE musicId IN (:ids)")
    suspend fun getAllFromId(ids: List<UUID>): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE albumId = :albumId AND RoomMusic.isHidden = 0")
    suspend fun getAllMusicFromAlbum(albumId : UUID) : List<RoomCompleteMusic>

    @Query("UPDATE RoomMusic SET albumId = :newAlbumId WHERE albumId = :legacyAlbumId")
    suspend fun updateMusicsAlbum(newAlbumId: UUID, legacyAlbumId: UUID)

    @Query("UPDATE RoomMusic SET coverId = NULL")
    suspend fun cleanAllMusicCovers()

    @Query("SELECT path FROM RoomMusic")
    suspend fun getAllMusicPath(): List<String>

    @Query("SELECT DISTINCT folder FROM RoomMusic")
    suspend fun getAllMusicFolders(): List<String>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE nbPlayed >= 1 AND isHidden = 0 
            ORDER BY nbPlayed DESC 
            LIMIT 11
        """
    )
    fun getMostListened(): Flow<List<RoomCompleteMusic>>

    @Transaction
    @Query(
        """
            SELECT 
                strftime('%m/%Y', monthMusic.addedDate) AS month,
                COUNT(*) AS totalMusics, 
                (
                    SELECT music.coverId FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND music.coverId IS NOT NULL 
                    AND strftime('%m/%Y', music.addedDate) = strftime('%m/%Y', monthMusic.addedDate)
                    ORDER BY
                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, 
                    addedDate DESC 
                    LIMIT 1
                ) AS coverId,
                (
                    SELECT music.path FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND strftime('%m/%Y', music.addedDate) = strftime('%m/%Y', monthMusic.addedDate) 
                    ORDER BY addedDate DESC 
                    LIMIT 1 
                ) AS musicCoverPath 
            FROM RoomMusic AS monthMusic
            WHERE isHidden = 0
            GROUP BY strftime('%Y-%m', addedDate)
            ORDER BY strftime('%Y-%m', addedDate) DESC
        """
    )
    fun getAllMonthMusics(): Flow<List<RoomMonthMusicPreview>>

    @Transaction
    @Query(
        """
            SELECT 
                strftime('%m/%Y', monthMusic.addedDate) AS month,
                COUNT(*) AS totalMusics, 
                (
                    SELECT music.coverId FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND music.coverId IS NOT NULL 
                    AND strftime('%m/%Y', music.addedDate) = strftime('%m/%Y', monthMusic.addedDate)
                    ORDER BY
                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, 
                    addedDate DESC 
                    LIMIT 1
                ) AS coverId,
                (
                    SELECT music.path FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND strftime('%m/%Y', music.addedDate) = strftime('%m/%Y', monthMusic.addedDate) 
                    ORDER BY addedDate DESC 
                    LIMIT 1 
                ) AS musicCoverPath 
            FROM RoomMusic AS monthMusic
            WHERE isHidden = 0 
            AND month = :month
            GROUP BY strftime('%Y-%m', addedDate)
            LIMIT 1
        """
    )
    fun getMonthMusicPreview(month: String): Flow<RoomMonthMusicPreview?>

    @Transaction
    @Query(
        """
            SELECT 
                folderMusic.folder,
                COUNT(*) AS totalMusics, 
                (
                    SELECT music.coverId FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND music.coverId IS NOT NULL 
                    AND music.folder = folderMusic.folder 
                    ORDER BY
                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, 
                    addedDate DESC 
                    LIMIT 1
                ) AS coverId,
                (
                    SELECT music.path FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND music.folder = folderMusic.folder 
                    ORDER BY addedDate DESC 
                    LIMIT 1 
                ) AS musicCoverPath 
            FROM RoomMusic As folderMusic
            WHERE isHidden = 0 
            GROUP BY folderMusic.folder
            ORDER BY totalMusics DESC
        """
    )
    fun getAllMusicFoldersPreview(): Flow<List<RoomMusicFolderPreview>>

    @Transaction
    @Query(
        """
            SELECT 
                folderMusic.folder,
                COUNT(*) AS totalMusics, 
                (
                    SELECT music.coverId FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND music.coverId IS NOT NULL 
                    AND music.folder = folderMusic.folder 
                    ORDER BY
                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, 
                    addedDate DESC 
                    LIMIT 1
                ) AS coverId,
                (
                    SELECT music.path FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND music.folder = folderMusic.folder 
                    ORDER BY addedDate DESC 
                    LIMIT 1 
                ) AS musicCoverPath 
            FROM RoomMusic As folderMusic
            WHERE isHidden = 0 
            AND folderMusic.folder = :folder
            GROUP BY folderMusic.folder 
            LIMIT 1
        """
    )
    fun getMusicFolderPreview(folder: String): Flow<RoomMusicFolderPreview?>

    @Transaction
    @Query(
        """
            SELECT * FROM ROOMMUSIC 
            WHERE folder = :folder 
            AND isHidden = 0 
            LIMIT :totalPerFolder
        """
    )
    suspend fun getSoulMixMusics(
        totalPerFolder: Int,
        folder: String,
    ): List<RoomCompleteMusic>
}