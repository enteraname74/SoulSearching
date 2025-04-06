package com.github.enteraname74.localdb.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.enteraname74.domain.model.DataMode

object Migration18To19 : Migration(18, 19) {
    private fun artistMigration(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE RoomArtist ADD COLUMN coverUrl TEXT")
        db.execSQL("ALTER TABLE RoomArtist ADD COLUMN dataMode TEXT NOT NULL DEFAULT '${DataMode.Local.value}'")
    }

    private fun playlistMigration(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE RoomPlaylist ADD COLUMN dataMode TEXT NOT NULL DEFAULT '${DataMode.Local.value}'")
    }

    private fun musicMigration(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN coverUrl TEXT")
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN dataMode TEXT NOT NULL DEFAULT '${DataMode.Local.value}'")

        db.execSQL(
            """
            CREATE TABLE RoomMusic_new (
                musicId BLOB PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                album TEXT NOT NULL,
                artist TEXT NOT NULL,
                coverId BLOB,
                coverUrl TEXT,
                duration INTEGER NOT NULL,
                path TEXT NOT NULL,
                folder TEXT NOT NULL,
                addedDate TEXT NOT NULL,
                nbPlayed INTEGER NOT NULL,
                isInQuickAccess INTEGER NOT NULL,
                isHidden INTEGER NOT NULL,
                dataMode TEXT NOT NULL,
                albumId BLOB NOT NULL,
                FOREIGN KEY (albumId) REFERENCES RoomAlbum(albumId) ON DELETE CASCADE
            )
        """
        )

        db.execSQL(
            """
            INSERT INTO RoomMusic_new (
                musicId, name, album, artist, coverId, coverUrl, duration, path, folder, addedDate, nbPlayed, isInQuickAccess, isHidden, dataMode, albumId
            )
            SELECT 
                RoomMusic.musicId, RoomMusic.name, RoomMusic.album, RoomMusic.artist, 
                RoomMusic.coverId, RoomMusic.coverUrl, RoomMusic.duration, RoomMusic.path, RoomMusic.folder, 
                RoomMusic.addedDate, RoomMusic.nbPlayed, RoomMusic.isInQuickAccess, RoomMusic.isHidden, RoomMusic.dataMode,
                (SELECT albumId FROM RoomMusicAlbum WHERE RoomMusic.musicId = RoomMusicAlbum.musicId)
            FROM RoomMusic
        """
        )

        db.execSQL("DROP TABLE RoomMusic")
        db.execSQL("ALTER TABLE RoomMusic_new RENAME TO RoomMusic")

        db.execSQL("CREATE INDEX index_RoomMusic_albumId ON RoomMusic(albumId)")

        db.execSQL("DROP TABLE RoomMusicAlbum")
    }

    private fun albumMigration(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE RoomAlbum ADD COLUMN dataMode TEXT NOT NULL DEFAULT '${DataMode.Local.value}'")

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
                dataMode TEXT NOT NULL,
                FOREIGN KEY (artistId) REFERENCES RoomArtist(artistId) ON DELETE CASCADE
            )
        """
        )

        db.execSQL(
            """
            INSERT INTO RoomAlbum_new (
                albumId, albumName, coverId, addedDate, nbPlayed, isInQuickAccess, artistId, dataMode
            )
            SELECT 
                RoomAlbum.albumId, RoomAlbum.albumName, RoomAlbum.coverId, 
                RoomAlbum.addedDate, RoomAlbum.nbPlayed, RoomAlbum.isInQuickAccess,
                (SELECT artistId FROM RoomAlbumArtist WHERE RoomAlbum.albumId = RoomAlbumArtist.albumId),
                RoomAlbum.dataMode
            FROM RoomAlbum
        """
        )

        db.execSQL("DROP TABLE RoomAlbum")
        db.execSQL("ALTER TABLE RoomAlbum_new RENAME TO RoomAlbum")

        db.execSQL("CREATE INDEX index_RoomAlbum_artistId ON RoomAlbum(artistId)")

        db.execSQL("DROP TABLE RoomAlbumArtist")
    }

    private fun mxnTableMigration(
        tableName: String,
        mColumnName: String,
        mTableName: String,
        nColumnName: String,
        nTableName: String,
        db: SupportSQLiteDatabase,
    ) {
        db.execSQL("ALTER TABLE $tableName ADD COLUMN dataMode TEXT NOT NULL DEFAULT '${DataMode.Local.value}'")
        db.execSQL(
            """
            CREATE TABLE ${tableName}_new (
                id TEXT PRIMARY KEY NOT NULL,
                $mColumnName BLOB NOT NULL,
                $nColumnName BLOB NOT NULL,
                dataMode TEXT NOT NULL,
                FOREIGN KEY (${mColumnName}) REFERENCES ${mTableName}(${mColumnName}) ON DELETE CASCADE,
                FOREIGN KEY (${nColumnName}) REFERENCES ${nTableName}(${nColumnName}) ON DELETE CASCADE
            )
        """
        )

        db.execSQL(
            """
            INSERT INTO ${tableName}_new (id, ${mColumnName}, ${nColumnName}, dataMode)
            SELECT hex(${mColumnName}) || hex(${nColumnName}), ${mColumnName}, $nColumnName, dataMode FROM $tableName
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

    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            println("DATABASE -- Start migrating from 18 to 19")
            musicMigration(db = db)
            artistMigration(db = db)
            playlistMigration(db = db)
            albumMigration(db = db)
            musicArtistMigration(db = db)
            musicPlaylistMigration(db = db)
        } catch (e: Exception) {
            println("DATABASE -- Error while migrating fro 18 to 19: ${e.message}")
        }
    }
}