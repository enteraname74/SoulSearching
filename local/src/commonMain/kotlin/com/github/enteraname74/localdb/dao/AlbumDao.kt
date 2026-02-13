package com.github.enteraname74.localdb.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.localdb.model.RoomAlbum
import com.github.enteraname74.localdb.model.RoomAlbumPreview
import com.github.enteraname74.localdb.model.RoomCompleteAlbum
import com.github.enteraname74.localdb.model.RoomCompleteAlbumWithMusics
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * DAO of an Album.
 */
@Dao
interface AlbumDao {

    @Upsert
    suspend fun upsert(roomAlbum: RoomAlbum)

    @Upsert
    suspend fun upsertAll(roomAlbums: List<RoomAlbum>)

    @Delete
    suspend fun delete(roomAlbum: RoomAlbum)

    @Query("DELETE FROM RoomAlbum WHERE albumId = :id")
    suspend fun delete(id: UUID)

    @Query("DELETE FROM RoomAlbum WHERE albumId IN (:ids)")
    suspend fun deleteAll(ids: List<UUID>)

    @Query(
        """
            DELETE FROM RoomAlbum
            WHERE (SELECT COUNT(*) FROM RoomMusic WHERE RoomMusic.albumId = RoomAlbum.albumId) = 0
        """
    )
    suspend fun deleteAllEmpty()

    @Query("SELECT albumName FROM RoomAlbum WHERE LOWER(albumName) LIKE LOWER('%' || :search || '%')")
    suspend fun getAlbumNamesContainingSearch(search: String): List<String>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE artistId = :artistId")
    fun getAllAlbumsFromArtist(artistId: UUID): Flow<List<RoomCompleteAlbum>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE artistId = :artistId")
    fun getAllAlbumsWithMusicsFromArtist(artistId: UUID): Flow<List<RoomCompleteAlbumWithMusics>>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY albumName ASC")
    fun getAll(): Flow<List<RoomCompleteAlbum>>

    @Transaction
    @Query(
        """
            SELECT album.albumId AS id, album.albumName AS name, 
            (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist, 
            (
                CASE WHEN album.coverId IS NULL THEN 
                        (
                            SELECT music.coverId FROM RoomMusic AS music 
                            WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                            music.albumPosition, 
                            CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END
                        )
                    ELSE album.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                music.albumPosition 
                LIMIT 1 
            ) AS musicCoverPath,
            album.isInQuickAccess 
            FROM RoomAlbum AS album 
            ORDER BY name ASC
        """
    )
    fun getAllPagedByNameAsc(): PagingSource<Int, RoomAlbumPreview>

    @Transaction
    @Query(
        """
            SELECT album.albumId AS id, album.albumName AS name, 
            (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist, 
            (
                CASE WHEN album.coverId IS NULL THEN 
                        (
                            SELECT music.coverId FROM RoomMusic AS music 
                            WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                            music.albumPosition, 
                            CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END
                        )
                    ELSE album.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                music.albumPosition 
                LIMIT 1
            ) AS musicCoverPath,
            album.isInQuickAccess 
            FROM RoomAlbum AS album 
            ORDER BY name DESC
        """
    )
    fun getAllPagedByNameDesc(): PagingSource<Int, RoomAlbumPreview>

    @Transaction
    @Query(
        """
            SELECT album.albumId AS id, album.albumName AS name, 
            (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist, 
            (
                CASE WHEN album.coverId IS NULL THEN 
                        (
                            SELECT music.coverId FROM RoomMusic AS music 
                            WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                            music.albumPosition, 
                            CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END
                        )
                    ELSE album.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                music.albumPosition 
                LIMIT 1
            ) AS musicCoverPath,
            album.isInQuickAccess 
            FROM RoomAlbum AS album 
            ORDER BY addedDate ASC
        """
    )
    fun getAllPagedByDateAsc(): PagingSource<Int, RoomAlbumPreview>

    @Transaction
    @Query(
        """
            SELECT album.albumId AS id, album.albumName AS name, 
            (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist, 
            (
                CASE WHEN album.coverId IS NULL THEN 
                        (
                            SELECT music.coverId FROM RoomMusic AS music 
                            WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                            music.albumPosition, 
                            CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END
                        )
                    ELSE album.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                music.albumPosition 
                LIMIT 1 
            ) AS musicCoverPath,
            album.isInQuickAccess 
            FROM RoomAlbum AS album 
            ORDER BY addedDate DESC
        """
    )
    fun getAllPagedByDateDesc(): PagingSource<Int, RoomAlbumPreview>

    @Transaction
    @Query(
        """
            SELECT album.albumId AS id, album.albumName AS name, 
            (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist, 
            (
                CASE WHEN album.coverId IS NULL THEN 
                        (
                            SELECT music.coverId FROM RoomMusic AS music 
                            WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                            music.albumPosition, 
                            CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END
                        )
                    ELSE album.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                music.albumPosition 
                LIMIT 1 
            ) AS musicCoverPath,
            album.isInQuickAccess 
            FROM RoomAlbum AS album 
            ORDER BY nbPlayed ASC
        """
    )
    fun getAllPagedByNbPlayedAsc(): PagingSource<Int, RoomAlbumPreview>

    @Transaction
    @Query(
        """
            SELECT album.albumId AS id, album.albumName AS name, 
            (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist, 
            (
                CASE WHEN album.coverId IS NULL THEN 
                        (
                            SELECT music.coverId FROM RoomMusic AS music 
                            WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                            music.albumPosition, 
                            CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END
                        )
                    ELSE album.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                music.albumPosition 
                LIMIT 1 
            ) AS musicCoverPath,
            album.isInQuickAccess 
            FROM RoomAlbum AS album 
            ORDER BY nbPlayed DESC
        """
    )
    fun getAllPagedByNbPlayedDesc(): PagingSource<Int, RoomAlbumPreview>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE albumId = :albumId")
    fun getFromId(albumId: UUID): Flow<RoomCompleteAlbum?>

    @Transaction
    @Query("SELECT * FROM RoomAlbum WHERE albumId = :albumId")
    fun getAlbumWithMusics(albumId: UUID): Flow<RoomCompleteAlbumWithMusics?>

    @Transaction
    @Query("SELECT * FROM RoomAlbum ORDER BY albumName ASC")
    fun getAllAlbumWithMusics(): Flow<List<RoomCompleteAlbumWithMusics>>

    @Transaction
    @Query(
        """
            SELECT album.albumId AS id, album.albumName AS name, 
            (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist, 
            (
                CASE WHEN album.coverId IS NULL THEN 
                        (
                            SELECT music.coverId FROM RoomMusic AS music 
                            WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                            music.albumPosition, 
                            CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END
                        )
                    ELSE album.coverId END
            ) AS coverId,
            (
                SELECT music.path FROM RoomMusic AS music 
                WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                music.albumPosition 
                LIMIT 1 
            ) AS musicCoverPath,
            album.isInQuickAccess 
            FROM RoomAlbum AS album 
            WHERE album.isInQuickAccess = 1
        """
    )
    fun getAllFromQuickAccess(): Flow<List<RoomAlbumPreview>>

    @Query("UPDATE RoomAlbum SET coverId = NULL")
    suspend fun cleanAllMusicCovers()

    @Transaction
    @Query(
        """
            SELECT * FROM RoomAlbum 
            WHERE albumName = :albumName 
            AND artistId = :artistId 
            AND albumId != :albumId 
            LIMIT 1
        """
    )
    suspend fun getDuplicatedAlbum(
        albumId: UUID,
        albumName: String,
        artistId: UUID
    ): RoomCompleteAlbum?

    @Transaction
    @Query(
        """
            SELECT album.* FROM RoomAlbum AS album
            WHERE album.albumName = :albumName 
            AND (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) = :artistName 
            LIMIT 1
        """
    )
    suspend fun getFromInformation(
        albumName: String,
        artistName: String,
    ): RoomCompleteAlbum?

    @Transaction
    @Query(
        """
            SELECT * FROM RoomAlbum 
            WHERE albumName = :albumName 
            AND artistId = :artistId
            LIMIT 1
        """
    )
    suspend fun getFromArtistId(
        albumName: String,
        artistId: UUID,
    ): RoomCompleteAlbum?
}