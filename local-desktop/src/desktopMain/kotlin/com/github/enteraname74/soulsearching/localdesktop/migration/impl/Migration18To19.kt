package com.github.enteraname74.soulsearching.localdesktop.migration.impl

import com.github.enteraname74.soulsearching.localdesktop.migration.ExposedMigration
import org.jetbrains.exposed.sql.Transaction

object Migration18To19 : ExposedMigration(
    forVersion = 18,
    toVersion = 19
) {
    private fun Transaction.mxnTableMigration(
        tableName: String,
        mColumnName: String,
        mTableName: String,
        nColumnName: String,
        nTableName: String,
    ) {
        // Step 1: Create the new table with the correct schema
        exec("""
            CREATE TABLE ${tableName}_new (
                id VARCHAR(256) PRIMARY KEY NOT NULL,
                $mColumnName TEXT NOT NULL,
                $nColumnName TEXT NOT NULL,
                FOREIGN KEY (${mColumnName}) REFERENCES ${mTableName}(${mColumnName}) ON DELETE CASCADE ON UPDATE NO ACTION,
                FOREIGN KEY (${nColumnName}) REFERENCES ${nTableName}(${nColumnName}) ON DELETE CASCADE ON UPDATE NO ACTION
            )
        """)

        // Step 2: Populate the new table with the concatenated id
        exec("""
            INSERT INTO ${tableName}_new (id, ${mColumnName}, albumId)
            SELECT $mColumnName || $nColumnName, ${mColumnName}, $nColumnName FROM $tableName
        """)

        // Step 3: Drop the old table
        exec("DROP TABLE $tableName")

        // Step 4: Rename the new table to the original name
        exec("ALTER TABLE ${tableName}_new RENAME TO $tableName")

        // Step 5: Recreate indices for the new table
        exec("CREATE INDEX index_${tableName}_${mColumnName} ON ${tableName}(${nColumnName})")
        exec("CREATE INDEX index_${tableName}${nColumnName} ON ${tableName}(${nColumnName})")
    }
    
    private fun Transaction.musicAlbumMigration() {
        mxnTableMigration(
            tableName = "MusicAlbum",
            mColumnName = "musicId",
            mTableName = "Music",
            nColumnName = "albumId",
            nTableName = "Album",
        )
//        exec("ALTER TABLE MusicAlbum ADD COLUMN stringId VARCHAR(256) NOT NULL DEFAULT ''")
//        exec(
//            """
//                    UPDATE MusicAlbum
//                    SET stringId = musicId || albumId
//                """
//        )
//        exec(
//            """
//                    CREATE TABLE MusicAlbum_new (
//                        id VARCHAR(256) PRIMARY KEY NOT NULL,
//                        musicId TEXT NOT NULL,
//                        albumId TEXT NOT NULL
//                    )
//                """
//        )
//        exec(
//            """
//                    INSERT INTO MusicAlbum_new (id, musicId, albumId)
//                    SELECT stringId, musicId, albumId FROM MusicAlbum
//                """
//        )
//        exec("DROP TABLE MusicAlbum")
//        exec("ALTER TABLE MusicAlbum_new RENAME TO MusicAlbum")
    }

    private fun Transaction.musicArtistMigration() {
        mxnTableMigration(
            tableName = "MusicArtist",
            mColumnName = "musicId",
            mTableName = "Music",
            nColumnName = "artistId",
            nTableName = "Artist",
        )
    }

    private fun Transaction.musicPlaylistMigration() {
        mxnTableMigration(
            tableName = "MusicPlaylist",
            mColumnName = "musicId",
            mTableName = "Music",
            nColumnName = "playlistId",
            nTableName = "Playlist",
        )
    }

    private fun Transaction.albumArtistMigration() {
        mxnTableMigration(
            tableName = "AlbumArtist",
            mColumnName = "albumId",
            mTableName = "Album",
            nColumnName = "artistId",
            nTableName = "Artist",
        )
    }

    override fun Transaction.migrate() {
        musicArtistMigration()
        musicAlbumMigration()
        musicPlaylistMigration()
        albumArtistMigration()
    }
}