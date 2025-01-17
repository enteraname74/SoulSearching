package com.github.enteraname74.localdb.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.enteraname74.domain.model.DataMode

object Migration18To19: Migration(18, 19) {
    private fun musicMigration(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN coverUrl TEXT")
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN dataMode TEXT NOT NULL DEFAULT '${DataMode.Local.value}'")
    }

    private fun mxnTableMigration(
        tableName: String,
        mColumnName: String,
        mTableName: String,
        nColumnName: String,
        nTableName: String,
        db: SupportSQLiteDatabase,
    ) {
        // Step 1: Create the new table with the correct schema
        db.execSQL("""
            CREATE TABLE ${tableName}_new (
                id TEXT PRIMARY KEY NOT NULL,
                $mColumnName BLOB NOT NULL,
                $nColumnName BLOB NOT NULL,
                FOREIGN KEY (${mColumnName}) REFERENCES ${mTableName}(${mColumnName}) ON DELETE CASCADE ON UPDATE NO ACTION,
                FOREIGN KEY (${nColumnName}) REFERENCES ${nTableName}(${nColumnName}) ON DELETE CASCADE ON UPDATE NO ACTION
            )
        """)

        // Step 2: Populate the new table with the concatenated id
        db.execSQL("""
            INSERT INTO ${tableName}_new (id, ${mColumnName}, ${nColumnName})
            SELECT hex(${mColumnName}) || hex(${nColumnName}), ${mColumnName}, $nColumnName FROM $tableName
        """)

        // Step 3: Drop the old table
        db.execSQL("DROP TABLE $tableName")

        // Step 4: Rename the new table to the original name
        db.execSQL("ALTER TABLE ${tableName}_new RENAME TO $tableName")

        // Step 5: Recreate indices for the new table
        db.execSQL("CREATE INDEX index_${tableName}_${mColumnName} ON ${tableName}(${nColumnName})")
        db.execSQL("CREATE INDEX index_${tableName}${nColumnName} ON ${tableName}(${nColumnName})")
    }

    private fun musicAlbumMigration(db: SupportSQLiteDatabase) {
        mxnTableMigration(
            tableName = "RoomMusicAlbum",
            mColumnName = "musicId",
            mTableName = "RoomMusic",
            nColumnName = "albumId",
            nTableName = "RoomAlbum",
            db = db,
        )
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

    private fun albumArtistMigration(db: SupportSQLiteDatabase) {
        mxnTableMigration(
            tableName = "RoomAlbumArtist",
            mColumnName = "albumId",
            mTableName = "RoomAlbum",
            nColumnName = "artistId",
            nTableName = "RoomArtist",
            db = db,
        )
    }

    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            println("DATABASE -- Start migrating from 18 to 19")
            musicMigration(db = db)
            musicAlbumMigration(db = db)
            musicArtistMigration(db = db)
            musicPlaylistMigration(db = db)
            albumArtistMigration(db = db)
        } catch (e: Exception) {
            println("DATABASE -- Error while migrating fro 18 to 19: ${e.message}")
        }
    }
}