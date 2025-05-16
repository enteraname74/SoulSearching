package com.github.enteraname74.localdb.migration

import android.database.Cursor
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.localdb.converters.Converters
import com.github.enteraname74.localdb.migration.ext.*
import com.github.enteraname74.localdb.model.RoomAlbum
import com.github.enteraname74.localdb.model.RoomArtist
import com.github.enteraname74.localdb.model.RoomMusicArtist
import com.github.enteraname74.localdb.model.toRoomMusicArtist
import com.github.enteraname74.soulsearching.features.filemanager.util.MetadataField
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicMetadataHelper
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

private typealias AlbumArtistName = String
private typealias SqlUuid = ByteArray

@OptIn(ExperimentalUuidApi::class)
private fun SqlUuid.toUUID(): UUID =
    Uuid.fromByteArray(this).toJavaUuid()

@OptIn(ExperimentalUuidApi::class)
private fun UUID.toSqlUuid(): SqlUuid =
    this.toKotlinUuid().toByteArray()

class Migration18To19(
    private val musicMetadataHelper: MusicMetadataHelper,
) : Migration(18, 19) {
    private fun mxnTableMigration(
        tableName: String,
        mColumnName: String,
        mTableName: String,
        nColumnName: String,
        nTableName: String,
        db: SupportSQLiteDatabase,
    ) {
        db.execSQL(
            """
            CREATE TABLE ${tableName}_new (
                id TEXT PRIMARY KEY NOT NULL,
                $mColumnName BLOB NOT NULL,
                $nColumnName BLOB NOT NULL,
                FOREIGN KEY (${mColumnName}) REFERENCES ${mTableName}(${mColumnName}) ON DELETE CASCADE,
                FOREIGN KEY (${nColumnName}) REFERENCES ${nTableName}(${nColumnName}) ON DELETE CASCADE
            )
        """
        )

        db.execSQL(
            """
            INSERT INTO ${tableName}_new (id, ${mColumnName}, ${nColumnName})
            SELECT hex(${mColumnName}) || hex(${nColumnName}), ${mColumnName}, $nColumnName FROM $tableName
        """
        )

        db.execSQL("DROP TABLE $tableName")
        db.execSQL("ALTER TABLE ${tableName}_new RENAME TO $tableName")

        db.execSQL("CREATE INDEX index_${tableName}_${mColumnName} ON ${tableName}(${mColumnName})")
        db.execSQL("CREATE INDEX index_${tableName}_${nColumnName} ON ${tableName}(${nColumnName})")
    }

    private fun musicArtistMigration(db: SupportSQLiteDatabase) {
        mxnTableMigration(
            tableName = "RoomMusicArtist",
            mColumnName = "musicId",
            mTableName = "RoomMusic",
            nColumnName = "artistId",
            nTableName = "RoomArtist",
            db = db,
        )
    }

    private fun musicPlaylistMigration(db: SupportSQLiteDatabase) {
        mxnTableMigration(
            tableName = "RoomMusicPlaylist",
            mColumnName = "musicId",
            mTableName = "RoomMusic",
            nColumnName = "playlistId",
            nTableName = "RoomPlaylist",
            db = db,
        )
    }

    private fun artistMigration(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE RoomArtist ADD COLUMN coverFolderKey TEXT")
    }

    /**
     * Migrates musics and returns the list of [CachedMusicUpdate] that possess an album artist.
     */
    private fun musicMigration(db: SupportSQLiteDatabase): List<CachedMusicUpdate> {
        // We will add the album id to the music directly.
        db.execSQL(
            """
            CREATE TABLE RoomMusic_new (
                musicId BLOB PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                album TEXT NOT NULL,
                artist TEXT NOT NULL,
                coverId BLOB,
                duration INTEGER NOT NULL,
                path TEXT NOT NULL,
                folder TEXT NOT NULL,
                addedDate TEXT NOT NULL,
                nbPlayed INTEGER NOT NULL,
                isInQuickAccess INTEGER NOT NULL,
                isHidden INTEGER NOT NULL,
                albumId BLOB NOT NULL,
                FOREIGN KEY (albumId) REFERENCES RoomAlbum(albumId) ON DELETE CASCADE
            )
        """
        )

        db.execSQL(
            """
            INSERT INTO RoomMusic_new (
                musicId, name, album, artist, coverId, duration, path, folder, addedDate, nbPlayed, isInQuickAccess, isHidden, albumId
            )
            SELECT 
                RoomMusic.musicId, RoomMusic.name, RoomMusic.album, RoomMusic.artist, 
                RoomMusic.coverId, RoomMusic.duration, RoomMusic.path, RoomMusic.folder, 
                RoomMusic.addedDate, RoomMusic.nbPlayed, RoomMusic.isInQuickAccess, RoomMusic.isHidden,
                (SELECT albumId FROM RoomMusicAlbum WHERE RoomMusic.musicId = RoomMusicAlbum.musicId)
            FROM RoomMusic
        """
        )

        db.execSQL("DROP TABLE RoomMusic")
        db.execSQL("ALTER TABLE RoomMusic_new RENAME TO RoomMusic")
        db.execSQL("CREATE INDEX index_RoomMusic_albumId ON RoomMusic(albumId)")
        db.execSQL("DROP TABLE RoomMusicAlbum")

        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN albumPosition INTEGER")
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN albumArtist TEXT")

        val cursor = db.query("SELECT musicId, album, albumId, path FROM RoomMusic")

        val mainInformation: HashMap<String, MusicInformation> = hashMapOf()
        val cachedData: MutableList<CachedMusicUpdate> = mutableListOf()

        cursor.use {
            while (cursor.moveToNext()) {
                val path: String = cursor.getString("path")

                mainInformation[path] = MusicInformation(
                    musicId = cursor.getId("musicId"),
                    albumId = cursor.getId("albumId"),
                    albumName = cursor.getString("album"),
                )
            }
        }

        val metadata: Map<String, Map<MetadataField, String>> = musicMetadataHelper.getMetadataFromPaths(
            musicPaths = mainInformation.keys.toList(),
            fields = listOf(
                MetadataField.Track,
                MetadataField.AlbumArtist,
            )
        )

        cachedData.addAll(
            metadata.mapNotNull { (path, fields) ->
                val information: MusicInformation? = mainInformation[path]

                information?.let {
                    CachedMusicUpdate(
                        musicIdAsBlob = it.musicId,
                        albumAsBlobId = it.albumId,
                        albumPosition = fields[MetadataField.Track],
                        albumArtist = fields[MetadataField.AlbumArtist],
                        albumName = it.albumName,
                    )
                }
            }
        )

        cachedData.chunked(CHUNK_SIZE).forEach { chunk ->
            val stringBuilder = StringBuilder()
            stringBuilder.append("UPDATE RoomMusic SET ")

            stringBuilder.append("albumPosition = CASE musicId ")
            chunk.forEach { cachedMusicUpdate ->
                stringBuilder.append("WHEN ${cachedMusicUpdate.musicIdAsBlob.toSQLId()} THEN ${cachedMusicUpdate.albumPosition.toSQLValue()} ")
            }
            stringBuilder.append("END, ")

            stringBuilder.append("albumArtist = CASE musicId ")
            chunk.forEach { cachedMusicUpdate ->
                stringBuilder.append("WHEN ${cachedMusicUpdate.musicIdAsBlob.toSQLId()} THEN ${cachedMusicUpdate.albumArtist.toSQLValue()} ")
            }
            stringBuilder.append("END ")

            val musicIds = chunk.joinToString { it.musicIdAsBlob.toSQLId() }
            stringBuilder.append("WHERE musicId IN ($musicIds)")

            db.execSQL(stringBuilder.toString())
        }

        return cachedData.filter { it.albumArtist != null }
    }

    private fun albumMigration(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE RoomAlbum_new (
                albumId BLOB PRIMARY KEY NOT NULL,
                albumName TEXT NOT NULL,
                coverId BLOB,
                addedDate TEXT NOT NULL,
                nbPlayed INTEGER NOT NULL,
                isInQuickAccess INTEGER NOT NULL,
                artistId BLOB NOT NULL,
                FOREIGN KEY (artistId) REFERENCES RoomArtist(artistId) ON DELETE CASCADE
            )
        """
        )

        db.execSQL(
            """
            INSERT INTO RoomAlbum_new (
                albumId, albumName, coverId, addedDate, nbPlayed, isInQuickAccess, artistId
            )
            SELECT 
                RoomAlbum.albumId, RoomAlbum.albumName, RoomAlbum.coverId, 
                RoomAlbum.addedDate, RoomAlbum.nbPlayed, RoomAlbum.isInQuickAccess,
                (SELECT artistId FROM RoomAlbumArtist WHERE RoomAlbum.albumId = RoomAlbumArtist.albumId)
            FROM RoomAlbum
        """
        )

        db.execSQL("DROP TABLE RoomAlbum")
        db.execSQL("ALTER TABLE RoomAlbum_new RENAME TO RoomAlbum")
        db.execSQL("CREATE INDEX index_RoomAlbum_artistId ON RoomAlbum(artistId)")
        db.execSQL("DROP TABLE RoomAlbumArtist")
    }

    private fun getMusicArtists(
        db: SupportSQLiteDatabase,
        cachedMusicUpdates: List<CachedMusicUpdate>
    ): List<SQLMusicArtist> {
        val musicArtists = ArrayList<SQLMusicArtist>()
        cachedMusicUpdates.chunked(CHUNK_SIZE).forEach { chunk ->
            val ids = chunk.joinToString { it.musicIdAsBlob.toSQLId() }
            val cursor = db.query("SELECT musicId, artistId FROM RoomMusicArtist WHERE musicId IN ($ids)")
            cursor.use {
                while (cursor.moveToNext()) {
                    val musicId: SqlUuid = cursor.getId("musicId")
                    val artistId: SqlUuid = cursor.getId("artistId")
                    musicArtists.add(
                        SQLMusicArtist(
                            musicId = musicId,
                            artistId = artistId,
                        )
                    )
                }
            }
        }

        return musicArtists
    }

    private fun getAlbum(
        db: SupportSQLiteDatabase,
        name: String,
        artistId: SqlUuid,
    ): RoomAlbum? {
        val cursor = db.query("SELECT * FROM RoomAlbum WHERE artistId = ${artistId.toSQLId()} AND albumName = ${name.toSQLValue()}")
        cursor.use {
            while (cursor.moveToNext()) {
                cursor.toRoomAlbum()?.let {
                    return it
                }
            }
        }

        return null
    }

    private fun getAlbums(
        db: SupportSQLiteDatabase,
        albumIds: List<SqlUuid>,
    ): List<RoomAlbum> {
        val albums = ArrayList<RoomAlbum>()
        albumIds.chunked(CHUNK_SIZE).forEach { chunk ->
            val ids = chunk.joinToString { it.toSQLId() }
            val cursor = db.query("SELECT * FROM RoomAlbum WHERE albumId IN ($ids)")
            cursor.use {
                while (cursor.moveToNext()) {
                    cursor.toRoomAlbum()?.let {
                        albums.add(it)
                    }
                }
            }
        }

        return albums
    }

    private fun Cursor.toRoomAlbum(): RoomAlbum? =
        runCatching {
            RoomAlbum(
                albumId = getId("albumId").toUUID(),
                albumName = getString("albumName"),
                coverId = getId("coverId").toUUID(),
                addedDate = Converters.stringToLocalDate(getString("addedDate")),
                nbPlayed = getInt("nbPlayed"),
                isInQuickAccess = getBool("isInQuickAccess"),
                artistId = getId("artistId").toUUID(),
            )
        }.getOrNull()

    /**
     * Retrieves a mapping of an AlbumArtist name and its id in the db.
     */
    private fun getAlbumArtistsIds(
        db: SupportSQLiteDatabase,
        artistNames: List<String>
    ): Map<AlbumArtistName, SqlUuid> {
        val hashMap: HashMap<String, SqlUuid> = hashMapOf()
        artistNames.chunked(CHUNK_SIZE).forEach { chunk ->
            val names = chunk.joinToString { it.toSQLValue() }
            val cursor = db.query("SELECT artistId, artistName FROM RoomArtist WHERE artistName IN ($names)")
            cursor.use {
                while (cursor.moveToNext()) {
                    val artistId: SqlUuid = cursor.getId("artistId")
                    val artistName: String = cursor.getString("artistName")

                    hashMap[artistName] = artistId
                }
            }
        }

        return hashMap
    }


    /**
     * Updates the link between album artist and music.
     * Returns a map of Music id to its Artist id.
     */
    @OptIn(ExperimentalUuidApi::class)
    private fun updateMusicArtists(
        db: SupportSQLiteDatabase,
        musicsWithAlbumArtist: List<CachedMusicUpdate>,
        musicArtists: List<SQLMusicArtist>,
        albumsArtistsIds: Map<AlbumArtistName, SqlUuid>,
    ): Map<CachedMusicUpdate, SqlUuid> {
        val newArtistsToSave: ArrayList<RoomArtist> = arrayListOf()
        val newMusicArtistToSave: ArrayList<RoomMusicArtist> = arrayListOf()
        val musicsToAlbumArtists: HashMap<CachedMusicUpdate, SqlUuid> = hashMapOf()

        for (music in musicsWithAlbumArtist) {
            val correspondingAlbumArtistId: SqlUuid? = albumsArtistsIds[music.albumArtist!!]

            // The album artist does not exist, we will create it and link the music to it.
            if (correspondingAlbumArtistId == null) {
                val artist = RoomArtist(artistName = music.albumArtist)
                newArtistsToSave.add(artist)
                newMusicArtistToSave.add(
                    MusicArtist(
                        musicId = music.musicIdAsBlob.toUUID(),
                        artistId = artist.artistId,
                    ).toRoomMusicArtist()
                )
                musicsToAlbumArtists[music] = artist.artistId.toKotlinUuid().toByteArray()
                continue
            }

            // The album artist does exist, we will check if we need to link it to the music.
            val isLinkedToMusic: Boolean = musicArtists.any {
                it.musicId.contentEquals(music.musicIdAsBlob) && it.artistId.contentEquals(correspondingAlbumArtistId)
            }

            if (!isLinkedToMusic) {
                newMusicArtistToSave.add(
                    MusicArtist(
                        musicId = music.musicIdAsBlob.toUUID(),
                        artistId = correspondingAlbumArtistId.toUUID(),
                    ).toRoomMusicArtist()
                )
                musicsToAlbumArtists[music] = correspondingAlbumArtistId
            }
        }

        // Finally, we save our information
        newArtistsToSave.chunked(CHUNK_SIZE).forEach { chunk ->
            val insertStatement = StringBuilder()
            insertStatement.append("INSERT INTO RoomArtist (artistId, artistName, coverId, addedDate, nbPlayed, isInQuickAccess) VALUES ")

            val values = chunk.joinToString(", ") { artist ->
                val artistId = artist.artistId.toKotlinUuid().toByteArray().toSQLId()
                val name = artist.artistName.toSQLValue()
                val coverId = artist.coverId?.toKotlinUuid()?.toByteArray()?.toSQLId() ?: "NULL"
                val addedDate = artist.addedDate.toString().toSQLValue()
                val nbPlayed = artist.nbPlayed
                val isInQuickAccess = artist.isInQuickAccess.toSQLValue()

                "($artistId, $name, $coverId, $addedDate, $nbPlayed, $isInQuickAccess)"
            }

            insertStatement.append(values)

            insertStatement.append(
                """
                ON CONFLICT(artistId) DO UPDATE SET 
                    artistName=excluded.artistName,
                    coverId=excluded.coverId,
                    addedDate=excluded.addedDate,
                    nbPlayed=excluded.nbPlayed,
                    isInQuickAccess=excluded.isInQuickAccess
            """
            )

            db.execSQL(insertStatement.toString())
        }

        newMusicArtistToSave.chunked(CHUNK_SIZE).forEach { chunk ->
            val insertStatement = StringBuilder()
            insertStatement.append("INSERT INTO RoomArtist (id, artistId, musicId) VALUES ")

            val values = chunk.joinToString(", ") { musicArtist ->
                val id = "${musicArtist.musicId}${musicArtist.artistId}".toSQLValue()
                val artistId = musicArtist.artistId.toKotlinUuid().toByteArray().toSQLId()
                val musicId = musicArtist.musicId.toKotlinUuid().toByteArray().toSQLId()

                "($id, $artistId, $musicId)"
            }

            insertStatement.append(values)
            insertStatement.append("ON CONFLICT(id) DO NOTHING")
            db.execSQL(insertStatement.toString())
        }

        return musicsToAlbumArtists
    }

    /**
     * Updates the link between each music in the [musicsToAlbumArtists] list and their album,
     * based on the albumPosition field.
     *
     * @return the list of album to check for deletion if empty.
     */
    @OptIn(ExperimentalUuidApi::class)
    private fun updateMusicAlbum(
        db: SupportSQLiteDatabase,
        musicsToAlbumArtists: Map<CachedMusicUpdate, SqlUuid>,
        albums: List<RoomAlbum>
    ) {
        val musicIdToNewAlbumId = HashMap<SqlUuid, SqlUuid>()
        val newAlbumsToSave = ArrayList<RoomAlbum>()
        for (musicToAlbumArtist in musicsToAlbumArtists) {
            // We retrieve the current album of the song.
            val musicAlbum: RoomAlbum = albums.firstOrNull { it.albumId == musicToAlbumArtist.key.albumAsBlobId.toUUID() } ?: continue
            // If the album artist id is not the same as the artist of the song's album, we will need to move the song.
            val shouldMoveAlbumOfMusic = !musicAlbum.artistId.toSqlUuid().contentEquals(musicToAlbumArtist.value)

            if (!shouldMoveAlbumOfMusic) {
                continue
            }

            val existingAlbumWithAlbumArtist: RoomAlbum? = newAlbumsToSave.firstOrNull {
                it.artistId == musicToAlbumArtist.value.toUUID() && it.albumName == musicToAlbumArtist.key.albumName
            } ?: getAlbum(
                db = db,
                artistId = musicToAlbumArtist.value,
                name = musicToAlbumArtist.key.albumName,
            )

            // There is no album
            if (existingAlbumWithAlbumArtist == null) {
                val album = RoomAlbum(
                    albumName = musicAlbum.albumName,
                    albumId = UUID.randomUUID(),
                    artistId = musicToAlbumArtist.value.toUUID(),
                )
                newAlbumsToSave.add(album)
                musicIdToNewAlbumId[musicToAlbumArtist.key.musicIdAsBlob] = album.albumId.toSqlUuid()
                continue
            }

            // There is already an album existing, we will just link the music to it.
            musicIdToNewAlbumId[musicToAlbumArtist.key.musicIdAsBlob] = existingAlbumWithAlbumArtist.albumId.toSqlUuid()
        }

        // We then save the new albums
        newAlbumsToSave.chunked(CHUNK_SIZE).forEach { chunk ->
            val insertStatement = StringBuilder()
            insertStatement.append("INSERT INTO RoomAlbum (albumId, albumName, coverId, addedDate, nbPlayed, isInQuickAccess, artistId) VALUES ")

            val values = chunk.joinToString(", ") { album ->
                val albumId = album.albumId.toKotlinUuid().toByteArray().toSQLId()
                val name = album.albumName.toSQLValue()
                val coverId = album.coverId?.toKotlinUuid()?.toByteArray()?.toSQLId() ?: "NULL"
                val addedDate = album.addedDate.toString().toSQLValue()
                val nbPlayed = album.nbPlayed
                val isInQuickAccess = album.isInQuickAccess.toSQLValue()
                val artistId = album.albumId.toKotlinUuid().toByteArray().toSQLId()

                "($albumId, $name, $coverId, $addedDate, $nbPlayed, $isInQuickAccess, $artistId)"
            }

            insertStatement.append(values)

            insertStatement.append(
                """
                ON CONFLICT(albumId) DO UPDATE SET 
                    albumName=excluded.albumName,
                    coverId=excluded.coverId,
                    addedDate=excluded.addedDate,
                    nbPlayed=excluded.nbPlayed,
                    isInQuickAccess=excluded.isInQuickAccess,
                    artistId=excluded.artistId
            """
            )

            db.execSQL(insertStatement.toString())
        }

        // We also update the music-album links for the concerned musics :
        musicIdToNewAlbumId.entries.chunked(CHUNK_SIZE).forEach { chunk ->
            val stringBuilder = StringBuilder()
            stringBuilder.append("UPDATE RoomMusic SET ")

            stringBuilder.append("albumId = CASE musicId ")
            chunk.forEach { cachedMusicUpdate ->
                stringBuilder.append("WHEN ${cachedMusicUpdate.key.toSQLId()} THEN ${cachedMusicUpdate.value.toSQLId()} ")
            }
            stringBuilder.append("END ")

            val musicIds = chunk.joinToString { it.key.toSQLId() }
            stringBuilder.append("WHERE musicId IN ($musicIds)")

            db.execSQL(stringBuilder.toString())
        }

        // Finally, we check if we can delete empty albums
        db.execSQL(
            """
                DELETE FROM RoomAlbum
                WHERE albumId NOT IN (
                    SELECT DISTINCT albumId
                    FROM RoomMusic
                    WHERE albumId IS NOT NULL
                );
            """
        )
    }

    private fun musicMigrationAndUpdateData(db: SupportSQLiteDatabase) {
        val musicsWithAlbumArtist: List<CachedMusicUpdate> = musicMigration(db)

        // Update the links of music and artists with new album artist field.
        val musicArtists: List<SQLMusicArtist> = getMusicArtists(
            db = db,
            cachedMusicUpdates = musicsWithAlbumArtist,
        )
        val albumsArtistsIds: Map<AlbumArtistName, SqlUuid> = getAlbumArtistsIds(
            db = db,
            artistNames = musicsWithAlbumArtist.mapNotNull { it.albumArtist },
        )
        val musicsToAlbumArtists: Map<CachedMusicUpdate, SqlUuid> = updateMusicArtists(
            db = db,
            musicsWithAlbumArtist = musicsWithAlbumArtist,
            musicArtists = musicArtists,
            albumsArtistsIds = albumsArtistsIds,
        )

        // Update the music-album link with the new album artist field.
        val albumsOfMusicsWithAlbumArtist: List<RoomAlbum> = getAlbums(
            db = db,
            albumIds = musicsWithAlbumArtist.map { it.albumAsBlobId },
        )

        updateMusicAlbum(
            db = db,
            musicsToAlbumArtists = musicsToAlbumArtists,
            albums = albumsOfMusicsWithAlbumArtist,
        )
    }

    override fun migrate(db: SupportSQLiteDatabase) {
        artistMigration(db)
        musicArtistMigration(db)
        musicPlaylistMigration(db)
        albumMigration(db)
        musicMigrationAndUpdateData(db)
    }

    private data class SQLMusicArtist(
        val musicId: SqlUuid,
        val artistId: SqlUuid,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SQLMusicArtist

            if (!musicId.contentEquals(other.musicId)) return false
            if (!artistId.contentEquals(other.artistId)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = musicId.contentHashCode()
            result = 31 * result + artistId.contentHashCode()
            return result
        }
    }

    private data class MusicInformation(
        val musicId: SqlUuid,
        val albumId: SqlUuid,
        val albumName: String,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MusicInformation

            if (!musicId.contentEquals(other.musicId)) return false
            if (!albumId.contentEquals(other.albumId)) return false
            if (albumName != other.albumName) return false

            return true
        }

        override fun hashCode(): Int {
            var result = musicId.contentHashCode()
            result = 31 * result + albumId.contentHashCode()
            result = 31 * result + albumName.hashCode()
            return result
        }

    }

    private data class CachedMusicUpdate(
        val musicIdAsBlob: SqlUuid,
        val albumPosition: String?,
        val albumName: String,
        val albumArtist: String?,
        val albumAsBlobId: SqlUuid,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CachedMusicUpdate

            if (!musicIdAsBlob.contentEquals(other.musicIdAsBlob)) return false
            if (albumPosition != other.albumPosition) return false
            if (albumArtist != other.albumArtist) return false
            if (!albumAsBlobId.contentEquals(other.albumAsBlobId)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = musicIdAsBlob.contentHashCode()
            result = 31 * result + (albumPosition?.hashCode() ?: 0)
            result = 31 * result + (albumArtist?.hashCode() ?: 0)
            result = 31 * result + albumAsBlobId.contentHashCode()
            return result
        }

    }

    private companion object {
        const val CHUNK_SIZE = 500
    }
}