package com.github.enteraname74.soulsearching.localdesktop.migration.impl

import com.github.enteraname74.soulsearching.localdesktop.migration.ExposedMigration
import org.jetbrains.exposed.sql.Transaction

object Migration18To19 : ExposedMigration(
    forVersion = 18,
    toVersion = 19
) {
    private fun Transaction.musicAlbumMigration() {
        exec("ALTER TABLE MusicAlbum ADD COLUMN stringId VARCHAR(256) NOT NULL DEFAULT ''")
        exec(
            """
                    UPDATE MusicAlbum
                    SET stringId = musicId || albumId
                """
        )
        exec(
            """
                    CREATE TABLE MusicAlbum_new (
                        id VARCHAR(256) PRIMARY KEY NOT NULL,
                        musicId UUID NOT NULL,
                        albumId UUID NOT NULL
                    )
                """
        )
        exec(
            """
                    INSERT INTO MusicAlbum_new (id, musicId, albumId)
                    SELECT stringId, musicId, albumId FROM MusicAlbum
                """
        )
        exec("DROP TABLE MusicAlbum")
        exec("ALTER TABLE MusicAlbum_new RENAME TO MusicAlbum")
    }

    private fun Transaction.musicArtistMigration() {
        exec("ALTER TABLE MusicArtist ADD COLUMN stringId VARCHAR(256) NOT NULL DEFAULT ''")
        exec(
            """
                    UPDATE MusicArtist
                    SET stringId = musicId || artistId
                """
        )
        exec(
            """
                    CREATE TABLE MusicArtist_new (
                        id VARCHAR(256) PRIMARY KEY NOT NULL,
                        musicId UUID NOT NULL,
                        artistId UUID NOT NULL
                    )
                """
        )
        exec(
            """
                    INSERT INTO MusicArtist_new (id, musicId, artistId)
                    SELECT stringId, musicId, artistId FROM MusicArtist
                """
        )
        exec("DROP TABLE MusicArtist")
        exec("ALTER TABLE MusicArtist_new RENAME TO MusicArtist")
    }

    private fun Transaction.musicPlaylistMigration() {
        exec("ALTER TABLE MusicPlaylist ADD COLUMN stringId VARCHAR(256) NOT NULL DEFAULT ''")
        exec(
            """
                    UPDATE MusicPlaylist
                    SET stringId = musicId || playlistId
                """
        )
        exec(
            """
                    CREATE TABLE MusicPlaylist_new (
                        id VARCHAR(256) PRIMARY KEY NOT NULL,
                        musicId UUID NOT NULL,
                        playlistId UUID NOT NULL
                    )
                """
        )
        exec(
            """
                    INSERT INTO MusicPlaylist_new (id, musicId, playlistId)
                    SELECT stringId, musicId, playlistId FROM MusicPlaylist
                """
        )
        exec("DROP TABLE MusicPlaylist")
        exec("ALTER TABLE MusicPlaylist_new RENAME TO MusicPlaylist")
    }

    override fun Transaction.migrate() {
        musicArtistMigration()
        musicAlbumMigration()
        musicPlaylistMigration()
    }
}