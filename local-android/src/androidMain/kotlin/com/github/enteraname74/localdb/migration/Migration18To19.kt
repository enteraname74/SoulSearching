package com.github.enteraname74.localdb.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.enteraname74.domain.model.DataMode

object Migration18To19: Migration(18, 19) {
    private fun musicMigration(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN coverUrl TEXT")
        db.execSQL("ALTER TABLE RoomMusic ADD COLUMN dataMode TEXT NOT NULL DEFAULT '${DataMode.Local.value}'")
    }

    private fun musicAlbumMigration(db: SupportSQLiteDatabase) {
        // Step 1: Create the new table with the correct schema
        db.execSQL("""
            CREATE TABLE RoomMusicAlbum_new (
                id TEXT PRIMARY KEY NOT NULL,
                musicId BLOB NOT NULL,
                albumId BLOB NOT NULL,
                FOREIGN KEY (musicId) REFERENCES RoomMusic(musicId) ON DELETE CASCADE ON UPDATE NO ACTION,
                FOREIGN KEY (albumId) REFERENCES RoomAlbum(albumId) ON DELETE CASCADE ON UPDATE NO ACTION
            )
        """)

        // Step 2: Populate the new table with the concatenated id
        db.execSQL("""
            INSERT INTO RoomMusicAlbum_new (id, musicId, albumId)
            SELECT hex(musicId) || hex(albumId), musicId, albumId FROM RoomMusicAlbum
        """)

        // Step 3: Drop the old table
        db.execSQL("DROP TABLE RoomMusicAlbum")

        // Step 4: Rename the new table to the original name
        db.execSQL("ALTER TABLE RoomMusicAlbum_new RENAME TO RoomMusicAlbum")

        // Step 5: Recreate indices for the new table
        db.execSQL("CREATE INDEX index_RoomMusicAlbum_musicId ON RoomMusicAlbum(musicId)")
        db.execSQL("CREATE INDEX index_RoomMusicAlbum_albumId ON RoomMusicAlbum(albumId)")
    }

    private fun musicArtistMigration(db: SupportSQLiteDatabase) {
        // Step 1: Create the new table with the correct schema
        db.execSQL("""
            CREATE TABLE RoomMusicArtist_new (
                id TEXT PRIMARY KEY NOT NULL,
                musicId BLOB NOT NULL,
                artistId BLOB NOT NULL,
                FOREIGN KEY (musicId) REFERENCES RoomMusic(musicId) ON DELETE CASCADE ON UPDATE NO ACTION,
                FOREIGN KEY (artistId) REFERENCES RoomArtist(artistId) ON DELETE CASCADE ON UPDATE NO ACTION
            )
        """)

        // Step 2: Populate the new table with the concatenated id
        db.execSQL("""
            INSERT INTO RoomMusicArtist_new (id, musicId, artistId)
            SELECT musicId || artistId, musicId, artistId FROM RoomMusicArtist
        """)

        // Step 3: Drop the old table
        db.execSQL("DROP TABLE RoomMusicArtist")

        // Step 4: Rename the new table to the original name
        db.execSQL("ALTER TABLE RoomMusicArtist_new RENAME TO RoomMusicArtist")

        // Step 5: Recreate indices for the new table
        db.execSQL("CREATE INDEX index_RoomMusicArtist_musicId ON RoomMusicArtist(musicId)")
        db.execSQL("CREATE INDEX index_RoomMusicArtist_artistId ON RoomMusicArtist(artistId)")
    }

    private fun musicPlaylistMigration(db: SupportSQLiteDatabase) {
        // Step 1: Create the new table with the correct schema
        db.execSQL("""
            CREATE TABLE RoomMusicPlaylist_new (
                id TEXT PRIMARY KEY NOT NULL,
                musicId BLOB NOT NULL,
                playlistId BLOB NOT NULL,
                FOREIGN KEY (musicId) REFERENCES RoomMusic(musicId) ON DELETE CASCADE ON UPDATE NO ACTION,
                FOREIGN KEY (playlistId) REFERENCES RoomPlaylist(playlistId) ON DELETE CASCADE ON UPDATE NO ACTION
            )
        """)

        // Step 2: Populate the new table with the concatenated id
        db.execSQL("""
            INSERT INTO RoomMusicPlaylist_new (id, musicId, playlistId)
            SELECT musicId || playlistId, musicId, playlistId FROM RoomMusicPlaylist
        """)

        // Step 3: Drop the old table
        db.execSQL("DROP TABLE RoomMusicPlaylist")

        // Step 4: Rename the new table to the original name
        db.execSQL("ALTER TABLE RoomMusicPlaylist_new RENAME TO RoomMusicPlaylist")

        // Step 5: Recreate indices for the new table
        db.execSQL("CREATE INDEX index_RoomMusicPlaylist_musicId ON RoomMusicPlaylist(musicId)")
        db.execSQL("CREATE INDEX index_RoomMusicPlaylist_playlistId ON RoomMusicPlaylist(playlistId)")
    }

    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            println("DATABASE -- Start migrating from 18 to 19")
            musicMigration(db = db)
            musicAlbumMigration(db = db)
            musicArtistMigration(db = db)
            musicPlaylistMigration(db = db)
        } catch (e: Exception) {
            println("DATABASE -- Error while migrating fro 18 to 19: ${e.message}")
        }
    }
}