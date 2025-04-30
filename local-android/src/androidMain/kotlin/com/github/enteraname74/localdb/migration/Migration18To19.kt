package com.github.enteraname74.localdb.migration

import android.annotation.SuppressLint
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.enteraname74.soulsearching.features.filemanager.util.MetadataField
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicMetadataHelper
import kotlin.uuid.ExperimentalUuidApi

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

    @SuppressLint("Range")
    private fun musicMigration(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN albumPosition INTEGER")
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN albumArtist TEXT")

        val cursor = db.query("SELECT musicId, path FROM RoomMusic")

        val mainInformation: HashMap<String, ByteArray> = hashMapOf()
        val cachedData: MutableList<CachedMusicUpdate> = mutableListOf()

        while (cursor.moveToNext()) {
            val musicIdAsBlob: ByteArray = cursor.getBlob(cursor.getColumnIndex("musicId"))
            val path: String = cursor.getString(cursor.getColumnIndex("path"))

            mainInformation[path] = musicIdAsBlob
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
                val id: ByteArray? = mainInformation[path]

                id?.let {
                    CachedMusicUpdate(
                        musicIdAsBlob = id,
                        albumPosition = fields[MetadataField.Track],
                        albumArtist = fields[MetadataField.AlbumArtist]
                    )
                }
            }
        )

        cachedData.chunked(BATCH_SIZE).forEach { chunk ->
            val stringBuilder = StringBuilder()
            stringBuilder.append("UPDATE RoomMusic SET ")

            stringBuilder.append("albumPosition = CASE musicId ")
            chunk.forEach { cachedMusicUpdate ->
                stringBuilder.append("WHEN ${cachedMusicUpdate.musicIdAsBlob.toSQLiteHexLiteral()} THEN ${cachedMusicUpdate.albumPosition.formattedForDb()} ")
            }
            stringBuilder.append("END, ")

            stringBuilder.append("albumArtist = CASE musicId ")
            chunk.forEach { cachedMusicUpdate ->
                stringBuilder.append("WHEN ${cachedMusicUpdate.musicIdAsBlob.toSQLiteHexLiteral()} THEN ${cachedMusicUpdate.albumArtist.formattedForDb()} ")
            }
            stringBuilder.append("END ")

            val musicIds = chunk.joinToString { it.musicIdAsBlob.toSQLiteHexLiteral() }
            stringBuilder.append("WHERE musicId IN ($musicIds)")

            db.execSQL(stringBuilder.toString())
        }
    }

    private fun String?.formattedForDb(): String =
        this?.let { "'${it.replace("'", "''")}'" } ?: "NULL"

    private fun ByteArray.toSQLiteHexLiteral(): String {
        return "X'" + joinToString("") { "%02x".format(it) } + "'"
    }

    override fun migrate(db: SupportSQLiteDatabase) {
        musicArtistMigration(db)
        musicPlaylistMigration(db)
        musicMigration(db)
    }

    private data class CachedMusicUpdate(
        val musicIdAsBlob: ByteArray,
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
        const val BATCH_SIZE = 500
    }
}