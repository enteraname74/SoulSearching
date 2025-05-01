package com.github.enteraname74.localdb.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.localdb.migration.ext.getId
import com.github.enteraname74.localdb.migration.ext.getString
import com.github.enteraname74.localdb.migration.ext.toSQLId
import com.github.enteraname74.localdb.migration.ext.toSQLValue
import com.github.enteraname74.localdb.model.RoomArtist
import com.github.enteraname74.localdb.model.RoomMusicArtist
import com.github.enteraname74.localdb.model.toRoomMusicArtist
import com.github.enteraname74.soulsearching.features.filemanager.util.MetadataField
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicMetadataHelper
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

private typealias AlbumArtistName = String
private typealias SqlUuid = ByteArray

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

    /**
     * Migrates musics and returns the list of [CachedMusicUpdate] that possess an album artist.
     */
    private fun musicMigration(db: SupportSQLiteDatabase): List<CachedMusicUpdate> {
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN albumPosition INTEGER")
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN albumArtist TEXT")

        val cursor = db.query("SELECT musicId, path FROM RoomMusic")

        val mainInformation: HashMap<String, SqlUuid> = hashMapOf()
        val cachedData: MutableList<CachedMusicUpdate> = mutableListOf()

        cursor.use {
            while (cursor.moveToNext()) {
                val musicIdAsBlob: SqlUuid = cursor.getId("musicId")
                val path: String = cursor.getString("path")

                mainInformation[path] = musicIdAsBlob
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
                val id: SqlUuid? = mainInformation[path]

                id?.let {
                    CachedMusicUpdate(
                        musicIdAsBlob = id,
                        albumPosition = fields[MetadataField.Track],
                        albumArtist = fields[MetadataField.AlbumArtist]
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
    ): Map<SqlUuid, SqlUuid> {
        val newArtistsToSave: ArrayList<RoomArtist> = arrayListOf()
        val newMusicArtistToSave: ArrayList<RoomMusicArtist> = arrayListOf()
        val musicsToArtists: HashMap<SqlUuid, SqlUuid> = hashMapOf()

        for (music in musicsWithAlbumArtist) {
            val correspondingAlbumArtistId: SqlUuid? = albumsArtistsIds[music.albumArtist!!]

            // The album artist does not exist, we will create it and link the music to it.
            if (correspondingAlbumArtistId == null) {
                val artist = RoomArtist(artistName = music.albumArtist)
                newArtistsToSave.add(artist)
                newMusicArtistToSave.add(
                    MusicArtist(
                        musicId = Uuid.fromByteArray(music.musicIdAsBlob).toJavaUuid(),
                        artistId = artist.artistId,
                    ).toRoomMusicArtist()
                )
                musicsToArtists[music.musicIdAsBlob] = artist.artistId.toKotlinUuid().toByteArray()
                continue
            }

            // The album artist does exist, we will check if we need to link it to the music.
            val isLinkedToMusic: Boolean = musicArtists.any {
                it.musicId.contentEquals(music.musicIdAsBlob) && it.artistId.contentEquals(correspondingAlbumArtistId)
            }

            if (!isLinkedToMusic) {
                newMusicArtistToSave.add(
                    MusicArtist(
                        musicId = Uuid.fromByteArray(music.musicIdAsBlob).toJavaUuid(),
                        artistId = Uuid.fromByteArray(correspondingAlbumArtistId).toJavaUuid(),
                    ).toRoomMusicArtist()
                )
                musicsToArtists[music.musicIdAsBlob] = correspondingAlbumArtistId
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

        return musicsToArtists
    }

    override fun migrate(db: SupportSQLiteDatabase) {
        musicArtistMigration(db)
        musicPlaylistMigration(db)
        val musicsWithAlbumArtist: List<CachedMusicUpdate> = musicMigration(db)
        val musicArtists: List<SQLMusicArtist> = getMusicArtists(
            db = db,
            cachedMusicUpdates = musicsWithAlbumArtist,
        )
        val albumsArtistsIds: Map<AlbumArtistName, SqlUuid> = getAlbumArtistsIds(
            db = db,
            artistNames = musicsWithAlbumArtist.mapNotNull { it.albumArtist },
        )

        val musicsToArtists: Map<SqlUuid, SqlUuid> = updateMusicArtists(
            db = db,
            musicsWithAlbumArtist = musicsWithAlbumArtist,
            musicArtists = musicArtists,
            albumsArtistsIds = albumsArtistsIds,
        )
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

    private data class CachedMusicUpdate(
        val musicIdAsBlob: SqlUuid,
        val albumPosition: String?,
        val albumArtist: String?,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CachedMusicUpdate

            if (!musicIdAsBlob.contentEquals(other.musicIdAsBlob)) return false
            if (albumPosition != other.albumPosition) return false
            if (albumArtist != other.albumArtist) return false

            return true
        }

        override fun hashCode(): Int {
            var result = musicIdAsBlob.contentHashCode()
            result = 31 * result + albumPosition.hashCode()
            result = 31 * result + albumArtist.hashCode()
            return result
        }
    }

    private companion object {
        const val CHUNK_SIZE = 500
    }
}