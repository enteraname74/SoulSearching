package com.github.enteraname74.localdb.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.github.enteraname74.localdb.model.RoomCompleteMusic
import com.github.enteraname74.localdb.model.RoomMusic
import com.github.enteraname74.localdb.view.RoomMonthMusicPreview
import com.github.enteraname74.localdb.view.RoomMusicFolderPreview
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

/**
 * DAO of a Music.
 */
@Dao
interface MusicDao {

    @Upsert
    suspend fun upsert(roomMusic: RoomMusic)

    @Upsert
    suspend fun upsertAll(roomMusics: List<RoomMusic>)

    @Delete
    suspend fun delete(roomMusic: RoomMusic)

    @Query(
        """
        DELETE FROM RoomMusic WHERE 
        folder IN (SELECT RoomFolder.folderPath FROM RoomFolder WHERE RoomFolder.isSelected = 0)
    """
    )
    suspend fun deleteFromUnselectedFolders()

    @Query("DELETE FROM RoomMusic WHERE musicId IN (:ids)")
    suspend fun deleteAll(ids: List<Uuid>)

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE musicId = :musicId LIMIT 1")
    fun getFromId(musicId: Uuid): Flow<RoomCompleteMusic?>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getFromRemoteId(remoteId: String): RoomCompleteMusic?

    @Query("SELECT musicId FROM RoomMusic WHERE remoteId IN (:remoteIds)")
    suspend fun getIdsFromRemoteIds(remoteIds: List<String>): List<Uuid>

    @Query("SELECT remoteId FROM RoomMusic WHERE musicId IN (:ids) AND remoteId IS NOT NULL")
    suspend fun getRemoteIdsFromIds(ids: List<Uuid>): List<String>

    @Transaction
    @Query("SELECT DISTINCT * FROM RoomMusic WHERE musicId IN (:ids)")
    fun getFromIds(ids: List<Uuid>): Flow<List<RoomCompleteMusic>>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY name ASC")
    fun getAll(): Flow<List<RoomCompleteMusic>>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE localPath IS NOT NULL AND isHidden = 0 AND scope != 'SharedPlayedList'")
    suspend fun getAllLocalMusic(): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND isInQuickAccess = 1 AND scope != 'SharedPlayedList' ORDER BY name ASC")
    fun getAllFromQuickAccess(): Flow<List<RoomCompleteMusic>>

    @Query(
        """
            SELECT musicId FROM RoomMusic WHERE 
            folder IN (SELECT RoomFolder.folderPath FROM RoomFolder WHERE RoomFolder.isSelected = 0)
        """
    )
    suspend fun getAllIdsFromUnselectedFolders(): List<Uuid>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY name ASC")
    fun getAllPagedByNameAsc(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY name ASC")
    suspend fun getAllByNameAsc(): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getAllByNameAsc(limit: Int, offset: Int): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY name DESC")
    fun getAllPagedByNameDesc(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY name DESC")
    suspend fun getAllByNameDesc(): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY name DESC LIMIT :limit OFFSET :offset")
    suspend fun getAllByNameDesc(limit: Int, offset: Int): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY addedDate ASC")
    fun getAllPagedByDateAsc(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY addedDate ASC")
    suspend fun getAllByDateAsc(): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY addedDate ASC LIMIT :limit OFFSET :offset")
    suspend fun getAllByDateAsc(limit: Int, offset: Int): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY addedDate DESC")
    fun getAllPagedByDateDesc(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY addedDate DESC")
    suspend fun getAllByDateDesc(): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY addedDate DESC LIMIT :limit OFFSET :offset")
    suspend fun getAllByDateDesc(limit: Int, offset: Int): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY nbPlayed ASC")
    fun getAllPagedByNbPlayedAsc(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY nbPlayed ASC")
    suspend fun getAllByNbPlayedAsc(): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY nbPlayed ASC LIMIT :limit OFFSET :offset")
    suspend fun getAllByNbPlayedAsc(limit: Int, offset: Int): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY nbPlayed DESC")
    fun getAllPagedByNbPlayedDesc(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY nbPlayed DESC")
    suspend fun getAllByNbPlayedDesc(): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY nbPlayed DESC LIMIT :limit OFFSET :offset")
    suspend fun getAllByNbPlayedDesc(limit: Int, offset: Int): List<RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE isHidden = 0 
            AND albumId = :albumId
            AND scope != 'SharedPlayedList'
            ORDER BY 
            CASE WHEN albumPosition IS NULL THEN 1 ELSE 0 END, 
            albumPosition,
            name
        """
    )
    fun getAllPagedOfAlbum(albumId: Uuid): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE isHidden = 0 
            AND folder = :folder
            AND scope != 'SharedPlayedList'
            ORDER BY name ASC
        """
    )
    fun getAllPagedByNameAscOfFolder(folder: String): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE isHidden = 0 
            AND scope != 'SharedPlayedList'
            AND strftime('%m/%Y', addedDate / 1000, 'unixepoch') = :month
            ORDER BY name ASC
        """
    )
    fun getAllPagedByNameAscOfMonth(month: String): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music
            INNER JOIN RoomMusicPlaylist as musicPlaylist
            ON music.musicId = musicPlaylist.musicId 
            AND musicPlaylist.playlistId = :playlistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList'
            ORDER BY name ASC
        """
    )
    fun getAllPagedByNameAscOfPlaylist(playlistId: Uuid): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music
            INNER JOIN RoomMusicArtist as musicArtist
            ON music.musicId = musicArtist.musicId 
            AND musicArtist.artistId = :artistId
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList'
            ORDER BY name ASC
        """
    )
    fun getAllPagedByNameAscOfArtist(artistId: Uuid): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE musicId IN (:ids)")
    suspend fun getAllFromId(ids: List<Uuid>): List<RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE isHidden = 0 
            AND albumId = :albumId 
            AND scope != 'SharedPlayedList' 
            ORDER BY 
            CASE WHEN albumPosition IS NULL THEN 1 ELSE 0 END, 
            albumPosition,
            name
        """
    )
    suspend fun getAllMusicFromAlbum(albumId: Uuid): List<RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE isHidden = 0 
            AND albumId = :albumId 
            AND scope != 'SharedPlayedList' 
            ORDER BY 
            CASE WHEN albumPosition IS NULL THEN 1 ELSE 0 END, 
            albumPosition,
            name
            LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getAllMusicFromAlbum(
        albumId: Uuid,
        limit: Int,
        offset: Int,
    ): List<RoomCompleteMusic>

    // TODO: Normalise with accents.
    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music
            WHERE music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
            AND (
                music.name LIKE '%' || :search || '%'
                COLLATE NOCASE 
                OR EXISTS(
                    SELECT 1 FROM RoomAlbum AS album 
                    WHERE album.albumId = music.albumId 
                    AND album.albumName LIKE '%' || :search || '%' 
                    COLLATE NOCASE 
                )
                OR EXISTS(
                    SELECT 1 FROM RoomArtist AS artist
                    INNER JOIN RoomMusicArtist AS musicArtist 
                    ON musicArtist.artistId = artist.artistId 
                    AND musicArtist.musicId = music.musicId 
                    AND artist.artistName LIKE '%' || :search || '%' 
                    COLLATE NOCASE
                )
            )
            AND albumId = :albumId
            ORDER BY 
            CASE WHEN albumPosition IS NULL THEN 1 ELSE 0 END, 
            albumPosition,
            name
        """
    )
    fun searchFromAlbum(
        albumId: Uuid,
        search: String,
    ): Flow<List<RoomCompleteMusic>>

    @Query(
        """
            SELECT COALESCE(SUM(duration), 0) FROM RoomMusic 
            WHERE isHidden = 0 
            AND scope != 'SharedPlayedList' 
            AND albumId = :albumId
        """
    )
    fun getAlbumDuration(albumId: Uuid): Flow<Long>

    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music
            INNER JOIN RoomMusicArtist as musicArtist
            ON music.musicId = musicArtist.musicId 
            AND musicArtist.artistId = :artistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
            ORDER BY name ASC
        """
    )
    suspend fun getAllMusicFromArtist(artistId: Uuid): List<RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music
            INNER JOIN RoomMusicArtist as musicArtist
            ON music.musicId = musicArtist.musicId 
            AND musicArtist.artistId = :artistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
            ORDER BY name ASC
            LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getAllMusicFromArtist(
        artistId: Uuid,
        limit: Int,
        offset: Int,
    ): List<RoomCompleteMusic>

    // TODO: Normalise with accents.
    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music
            INNER JOIN RoomMusicArtist as musicArtist
            ON music.musicId = musicArtist.musicId 
            AND musicArtist.artistId = :artistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
            AND (
                music.name LIKE '%' || :search || '%'
                COLLATE NOCASE 
                OR EXISTS(
                    SELECT 1 FROM RoomAlbum AS album 
                    WHERE album.albumId = music.albumId 
                    AND album.albumName LIKE '%' || :search || '%' 
                    COLLATE NOCASE 
                )
                OR EXISTS(
                    SELECT 1 FROM RoomArtist AS artist
                    INNER JOIN RoomMusicArtist AS musicArtist 
                    ON musicArtist.artistId = artist.artistId 
                    AND musicArtist.musicId = music.musicId 
                    AND artist.artistName LIKE '%' || :search || '%' 
                    COLLATE NOCASE
                )
            )
            ORDER BY name ASC
        """
    )
    fun searchFromArtist(
        artistId: Uuid,
        search: String,
    ): Flow<List<RoomCompleteMusic>>

    @Query(
        """
            SELECT COALESCE(SUM(music.duration), 0) FROM RoomMusic AS music
            INNER JOIN RoomMusicArtist as musicArtist
            ON music.musicId = musicArtist.musicId 
            AND musicArtist.artistId = :artistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList'
        """
    )
    fun getArtistDuration(artistId: Uuid): Flow<Long>

    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music
            INNER JOIN RoomMusicPlaylist as musicPlaylist
            ON music.musicId = musicPlaylist.musicId 
            AND musicPlaylist.playlistId = :playlistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList'
            ORDER BY name ASC
        """
    )
    suspend fun getAllMusicFromPlaylist(playlistId: Uuid): List<RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music
            INNER JOIN RoomMusicPlaylist as musicPlaylist
            ON music.musicId = musicPlaylist.musicId 
            AND musicPlaylist.playlistId = :playlistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList'
            ORDER BY name ASC
            LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getAllMusicFromPlaylist(
        playlistId: Uuid,
        limit: Int,
        offset: Int,
    ): List<RoomCompleteMusic>

    // TODO: Normalise with accents.
    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music
            INNER JOIN RoomMusicPlaylist as musicPlaylist
            ON music.musicId = musicPlaylist.musicId 
            AND musicPlaylist.playlistId = :playlistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
            AND (
                music.name LIKE '%' || :search || '%'
                COLLATE NOCASE 
                OR EXISTS(
                    SELECT 1 FROM RoomAlbum AS album 
                    WHERE album.albumId = music.albumId 
                    AND album.albumName LIKE '%' || :search || '%' 
                    COLLATE NOCASE 
                )
                OR EXISTS(
                    SELECT 1 FROM RoomArtist AS artist
                    INNER JOIN RoomMusicArtist AS musicArtist 
                    ON musicArtist.artistId = artist.artistId 
                    AND musicArtist.musicId = music.musicId 
                    AND artist.artistName LIKE '%' || :search || '%' 
                    COLLATE NOCASE
                )
            ) 
            ORDER BY name ASC
        """
    )
    fun searchFromPlaylist(
        playlistId: Uuid,
        search: String,
    ): Flow<List<RoomCompleteMusic>>

    @Query(
        """
            SELECT COALESCE(SUM(music.duration), 0) FROM RoomMusic AS music
            INNER JOIN RoomMusicPlaylist as musicPlaylist
            ON music.musicId = musicPlaylist.musicId 
            AND musicPlaylist.playlistId = :playlistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
        """
    )
    fun getPlaylistDuration(playlistId: Uuid): Flow<Long>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE isHidden = 0 
            AND scope != 'SharedPlayedList' 
            AND strftime('%m/%Y', addedDate / 1000, 'unixepoch') = :month
            ORDER BY name ASC
        """
    )
    suspend fun getAllMusicFromMonth(month: String): List<RoomCompleteMusic>

    // TODO: Normalise with accents.
    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music
            WHERE music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
            AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') = :month
            AND (
                music.name LIKE '%' || :search || '%'
                COLLATE NOCASE 
                OR EXISTS(
                    SELECT 1 FROM RoomAlbum AS album 
                    WHERE album.albumId = music.albumId 
                    AND album.albumName LIKE '%' || :search || '%' 
                    COLLATE NOCASE 
                )
                OR EXISTS(
                    SELECT 1 FROM RoomArtist AS artist
                    INNER JOIN RoomMusicArtist AS musicArtist 
                    ON musicArtist.artistId = artist.artistId 
                    AND musicArtist.musicId = music.musicId 
                    AND artist.artistName LIKE '%' || :search || '%' 
                    COLLATE NOCASE
                )
            ) 
            ORDER BY music.name ASC
        """
    )
    fun searchFromMonth(
        month: String,
        search: String,
    ): Flow<List<RoomCompleteMusic>>

    @Query(
        """
            SELECT COALESCE(SUM(duration), 0) FROM RoomMusic 
            WHERE isHidden = 0 
            AND scope != 'SharedPlayedList' 
            AND strftime('%m/%Y', addedDate / 1000, 'unixepoch') = :month
        """
    )
    fun getMonthMusicsDuration(month: String): Flow<Long>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE isHidden = 0 
            AND scope != 'SharedPlayedList' 
            AND folder = :folder
            ORDER BY name ASC
        """
    )
    suspend fun getAllMusicFromFolder(folder: String): List<RoomCompleteMusic>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE isHidden = 0 
            AND scope != 'SharedPlayedList' 
            AND folder = :folder
            ORDER BY name ASC
            LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getAllMusicFromFolder(
        folder: String,
        limit: Int,
        offset: Int,
    ): List<RoomCompleteMusic>

    // TODO: Normalise with accents.
    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music 
            WHERE music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
            AND music.folder = :folder
            AND (
                music.name LIKE '%' || :search || '%'
                COLLATE NOCASE 
                OR EXISTS(
                    SELECT 1 FROM RoomAlbum AS album 
                    WHERE album.albumId = music.albumId 
                    AND album.albumName LIKE '%' || :search || '%' 
                    COLLATE NOCASE 
                )
                OR EXISTS(
                    SELECT 1 FROM RoomArtist AS artist
                    INNER JOIN RoomMusicArtist AS musicArtist 
                    ON musicArtist.artistId = artist.artistId 
                    AND musicArtist.musicId = music.musicId 
                    AND artist.artistName LIKE '%' || :search || '%' 
                    COLLATE NOCASE
                )
            ) 
            ORDER BY music.name ASC
        """
    )
    fun searchFromFolder(
        folder: String,
        search: String,
    ): Flow<List<RoomCompleteMusic>>

    // TODO: Normalise with accents.
    @Transaction
    @Query(
        """
            SELECT music.* FROM RoomMusic AS music 
            WHERE music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
            AND (
                music.name LIKE '%' || :search || '%'
                COLLATE NOCASE 
                OR EXISTS(
                    SELECT 1 FROM RoomAlbum AS album 
                    WHERE album.albumId = music.albumId 
                    AND album.albumName LIKE '%' || :search || '%' 
                    COLLATE NOCASE 
                )
                OR EXISTS(
                    SELECT 1 FROM RoomArtist AS artist
                    INNER JOIN RoomMusicArtist AS musicArtist 
                    ON musicArtist.artistId = artist.artistId 
                    AND musicArtist.musicId = music.musicId 
                    AND artist.artistName LIKE '%' || :search || '%' 
                    COLLATE NOCASE
                )
            ) 
            ORDER BY music.name ASC
        """
    )
    fun searchAll(
        search: String,
    ): Flow<List<RoomCompleteMusic>>

    @Query(
        """
            SELECT COALESCE(SUM(duration), 0) FROM RoomMusic 
            WHERE isHidden = 0 
            AND scope != 'SharedPlayedList' 
            AND folder = :folder
        """
    )
    fun getFolderMusicsDuration(folder: String): Flow<Long>

    @Query("UPDATE RoomMusic SET albumId = :newAlbumId WHERE albumId = :legacyAlbumId")
    suspend fun updateMusicsAlbum(newAlbumId: Uuid, legacyAlbumId: Uuid)

    @Query("UPDATE RoomMusic SET coverId = NULL")
    suspend fun cleanAllMusicCovers()

    @Query("SELECT localPath FROM RoomMusic WHERE localPath IS NOT NULL AND isHidden = 0 AND scope != 'SharedPlayedList'")
    suspend fun getAllMusicLocalPath(): List<String>

    @Query("SELECT DISTINCT folder FROM RoomMusic WHERE isHidden = 0 AND scope != 'SharedPlayedList'")
    suspend fun getAllMusicFolders(): List<String>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE nbPlayed >= 1 AND isHidden = 0 
            AND scope != 'SharedPlayedList' 
            ORDER BY nbPlayed DESC 
        """
    )
    fun getMostPlayed(): PagingSource<Int, RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMonthMusicPreview")
    fun getAllMonthMusics(): Flow<List<RoomMonthMusicPreview>>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMonthMusicPreview 
            WHERE month = :month
            LIMIT 1
        """
    )
    fun getMonthMusicPreview(month: String): Flow<RoomMonthMusicPreview?>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusicFolderPreview 
            ORDER BY totalMusics DESC
        """
    )
    fun getAllMusicFoldersPreview(): Flow<List<RoomMusicFolderPreview>>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusicFolderPreview 
            ORDER BY totalMusics DESC
            LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getAllMusicFoldersPreview(
        limit: Int,
        offset: Int,
    ): List<RoomMusicFolderPreview>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusicFolderPreview 
            WHERE folder = :folder
            LIMIT 1
        """
    )
    fun getMusicFolderPreview(folder: String): Flow<RoomMusicFolderPreview?>

    @Transaction
    @Query(
        """
            SELECT * FROM RoomMusic 
            WHERE folder = :folder 
            AND isHidden = 0 
            AND scope != 'SharedPlayedList' 
            LIMIT :totalPerFolder
        """
    )
    suspend fun getSoulMixMusics(
        totalPerFolder: Int,
        folder: String,
    ): List<RoomCompleteMusic>

    @Query(
        """
            SELECT remoteId FROM RoomMusic 
            WHERE remoteId IS NOT NULL AND scope != 'SharedPlayedList'
        """
    )
    suspend fun getAllRemoteIdsPossessedByUser(): List<String>

    @Transaction
    @Query(
        """
            SELECT m.* FROM RoomMusic m 
            CROSS JOIN RoomCloudPreferences cp
            WHERE scope != 'SharedPlayedList' AND m.lastUpdateMillis IS NULL
               OR cp.lastSyncMillis IS NULL
               OR m.lastUpdateMillis > cp.lastSyncMillis 
               OR m.remoteId IS NULL
        """
    )
    suspend fun getAllToSendToCloud(): List<RoomCompleteMusic>

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE name = :musicName AND albumId = :albumId")
    suspend fun getFromInformation(
        musicName: String,
        albumId: Uuid,
    ): RoomCompleteMusic?

    @Query("UPDATE RoomMusic SET remoteId = NULL, coverUrl = NULL WHERE remoteId IN (:remoteIds)")
    suspend fun deleteAllRemoteFieldsOfIds(remoteIds: List<String>)

    @Query("UPDATE RoomMusic SET remoteId = NULL, coverUrl = NULL")
    suspend fun deleteAllRemoteFields()

    @Query("DELETE FROM RoomMusic WHERE localPath IS NULL AND remoteId IS NULL")
    suspend fun deleteNotExisting()

    @Query("DELETE FROM RoomMusic WHERE scope = 'SharedPlayedList'")
    suspend fun deleteSharedPlayedListMusics()

    @Transaction
    @Query("SELECT * FROM RoomMusic WHERE path = :path")
    suspend fun getFromPath(path: String): RoomCompleteMusic?

    @Query(
        """
            UPDATE RoomMusic 
            SET lastUpdateMillis = :updatedAt 
            WHERE musicId IN (:musicIds) AND lastUpdateMillis < :updatedAt
        """
    )
    suspend fun updateLastUpdatedAtField(
        musicIds: List<Uuid>,
        updatedAt: Long,
    )

    @Query(
        """
            SELECT DISTINCT music.musicId FROM RoomMusic AS music
            INNER JOIN RoomMusicArtist AS musicArtist 
            ON music.musicId = musicArtist.musicId 
            WHERE musicArtist.artistId IN (:artistIds)
        """
    )
    suspend fun getMusicIdsOfArtists(artistIds: List<Uuid>): List<Uuid>

    @Query(
        """
            SELECT DISTINCT musicId FROM RoomMusic 
            WHERE albumId IN (:albumIds)
        """
    )
    suspend fun getMusicIdsOfAlbum(albumIds: List<Uuid>): List<Uuid>
}
