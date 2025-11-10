package com.github.enteraname74.localdb.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.github.enteraname74.localdb.migration.ext.getId
import com.github.enteraname74.localdb.migration.ext.getString
import com.github.enteraname74.localdb.migration.ext.toSQLId
import com.github.enteraname74.localdb.migration.ext.toSQLValue
import com.github.enteraname74.soulsearching.features.filemanager.util.MetadataField
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicMetadataHelper

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
        connection: SQLiteConnection,
    ) {
        connection.execSQL(
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

        connection.execSQL(
            """
            INSERT INTO ${tableName}_new (id, ${mColumnName}, ${nColumnName})
            SELECT hex(${mColumnName}) || hex(${nColumnName}), ${mColumnName}, $nColumnName FROM $tableName
        """
        )

        connection.execSQL("DROP TABLE $tableName")
        connection.execSQL("ALTER TABLE ${tableName}_new RENAME TO $tableName")

        connection.execSQL("CREATE INDEX index_${tableName}_${mColumnName} ON ${tableName}(${mColumnName})")
        connection.execSQL("CREATE INDEX index_${tableName}_${nColumnName} ON ${tableName}(${nColumnName})")
    }

    private fun musicArtistMigration(connection: SQLiteConnection) {
        mxnTableMigration(
            tableName = "RoomMusicArtist",
            mColumnName = "musicId",
            mTableName = "RoomMusic",
            nColumnName = "artistId",
            nTableName = "RoomArtist",
            connection = connection,
        )
    }

    private fun musicPlaylistMigration(connection: SQLiteConnection) {
        mxnTableMigration(
            tableName = "RoomMusicPlaylist",
            mColumnName = "musicId",
            mTableName = "RoomMusic",
            nColumnName = "playlistId",
            nTableName = "RoomPlaylist",
            connection = connection,
        )
    }

    private fun artistMigration(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE RoomArtist ADD COLUMN coverFolderKey TEXT")
    }

    /**
     * Migrates musics and returns the list of [CachedMusicUpdate] that possess an album artist.
     */
    private fun musicMigration(connection: SQLiteConnection) {
        // We will add the album id to the music directly.
        connection.execSQL(
            """
            CREATE TABLE RoomMusic_new (
                musicId BLOB PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
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

        connection.execSQL(
            """
            INSERT INTO RoomMusic_new (
                musicId, name, coverId, duration, path, folder, addedDate, nbPlayed, isInQuickAccess, isHidden, albumId
            )
            SELECT 
                RoomMusic.musicId, RoomMusic.name, 
                RoomMusic.coverId, RoomMusic.duration, RoomMusic.path, RoomMusic.folder, 
                RoomMusic.addedDate, RoomMusic.nbPlayed, RoomMusic.isInQuickAccess, RoomMusic.isHidden,
                (SELECT albumId FROM RoomMusicAlbum WHERE RoomMusic.musicId = RoomMusicAlbum.musicId)
            FROM RoomMusic
        """
        )

        connection.execSQL("DROP TABLE RoomMusic")
        connection.execSQL("ALTER TABLE RoomMusic_new RENAME TO RoomMusic")
        connection.execSQL("CREATE INDEX index_RoomMusic_albumId ON RoomMusic(albumId)")
        connection.execSQL("DROP TABLE RoomMusicAlbum")

        connection.execSQL("ALTER TABLE RoomMusic ADD COLUMN albumPosition INTEGER")

        val cursor = connection.prepare("SELECT musicId, albumId, path FROM RoomMusic")

        val mainInformation: HashMap<String, MusicInformation> = hashMapOf()
        val cachedData: MutableList<CachedMusicUpdate> = mutableListOf()

        cursor.use {
            while (cursor.step()) {
                val path: String = cursor.getString("path")

                mainInformation[path] = MusicInformation(
                    musicId = cursor.getId("musicId"),
                    albumId = cursor.getId("albumId"),
                )
            }
        }

        val metadata: Map<String, Map<MetadataField, String>> = musicMetadataHelper.getMetadataFromPaths(
            musicPaths = mainInformation.keys.toList(),
            fields = listOf(
                MetadataField.Track,
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
            stringBuilder.append("END ")

            val musicIds = chunk.joinToString { it.musicIdAsBlob.toSQLId() }
            stringBuilder.append("WHERE musicId IN ($musicIds)")

            connection.execSQL(stringBuilder.toString())
        }
    }

    private fun albumMigration(connection: SQLiteConnection) {
        connection.execSQL(
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

        connection.execSQL(
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

        connection.execSQL("DROP TABLE RoomAlbum")
        connection.execSQL("ALTER TABLE RoomAlbum_new RENAME TO RoomAlbum")
        connection.execSQL("CREATE INDEX index_RoomAlbum_artistId ON RoomAlbum(artistId)")
        connection.execSQL("DROP TABLE RoomAlbumArtist")
    }

    override fun migrate(connection: SQLiteConnection) {
        println("Doint migration")
        artistMigration(connection)
        musicArtistMigration(connection)
        musicPlaylistMigration(connection)
        albumMigration(connection)
        println("just before music")

        musicMigration(connection)
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
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MusicInformation

            if (!musicId.contentEquals(other.musicId)) return false
            if (!albumId.contentEquals(other.albumId)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = musicId.contentHashCode()
            result = 31 * result + albumId.contentHashCode()
            return result
        }

    }

    private data class CachedMusicUpdate(
        val musicIdAsBlob: SqlUuid,
        val albumPosition: String?,
        val albumAsBlobId: SqlUuid,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CachedMusicUpdate

            if (!musicIdAsBlob.contentEquals(other.musicIdAsBlob)) return false
            if (albumPosition != other.albumPosition) return false
            if (!albumAsBlobId.contentEquals(other.albumAsBlobId)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = musicIdAsBlob.contentHashCode()
            result = 31 * result + (albumPosition?.hashCode() ?: 0)
            result = 31 * result + albumAsBlobId.contentHashCode()
            return result
        }

    }

    private companion object {
        const val CHUNK_SIZE = 500
    }
}