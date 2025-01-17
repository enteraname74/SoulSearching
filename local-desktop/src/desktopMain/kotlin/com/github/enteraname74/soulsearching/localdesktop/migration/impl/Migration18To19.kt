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
        nColumnName: String,
    ) {
        // Step 1: Create the new table with the correct schema
        exec(
            """
            CREATE TABLE ${tableName}_new (
                id VARCHAR(256) PRIMARY KEY NOT NULL,
                $mColumnName TEXT NOT NULL,
                $nColumnName TEXT NOT NULL
            )
        """
        )

        // Step 2: Populate the new table with the concatenated id
        exec(
            """
            INSERT INTO ${tableName}_new (id, ${mColumnName}, ${nColumnName})
            SELECT $mColumnName || $nColumnName, ${mColumnName}, $nColumnName FROM $tableName
        """
        )

        // Step 3: Drop the old table
        exec("DROP TABLE $tableName")

        // Step 4: Rename the new table to the original name
        exec("ALTER TABLE ${tableName}_new RENAME TO $tableName")
    }

    private fun Transaction.musicAlbumMigration() {
        mxnTableMigration(
            tableName = "MusicAlbum",
            mColumnName = "musicId",
            nColumnName = "albumId",
        )
    }

    private fun Transaction.musicArtistMigration() {
        mxnTableMigration(
            tableName = "MusicArtist",
            mColumnName = "musicId",
            nColumnName = "artistId",
        )
    }

    private fun Transaction.musicPlaylistMigration() {
        mxnTableMigration(
            tableName = "MusicPlaylist",
            mColumnName = "musicId",
            nColumnName = "playlistId",
        )
    }

    private fun Transaction.albumArtistMigration() {
        mxnTableMigration(
            tableName = "AlbumArtist",
            mColumnName = "albumId",
            nColumnName = "artistId",
        )
    }

    override fun Transaction.migrate() {
        musicArtistMigration()
        musicAlbumMigration()
        musicPlaylistMigration()
        albumArtistMigration()
    }
}