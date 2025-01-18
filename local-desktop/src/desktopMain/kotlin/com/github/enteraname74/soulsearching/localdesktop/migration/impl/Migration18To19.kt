package com.github.enteraname74.soulsearching.localdesktop.migration.impl

import com.github.enteraname74.domain.model.DataMode
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
        mTableName: String,
        nTableName: String,
    ) {
        exec(
            """
            CREATE TABLE ${tableName}_new (
                id VARCHAR(256) PRIMARY KEY NOT NULL,
                $mColumnName TEXT NOT NULL,
                $nColumnName TEXT NOT NULL,
                FOREIGN KEY (${mColumnName}) REFERENCES ${mTableName}(${mColumnName}) ON DELETE CASCADE,
                FOREIGN KEY (${nColumnName}) REFERENCES ${nTableName}(${nColumnName}) ON DELETE CASCADE
            )
        """
        )

        exec(
            """
            INSERT INTO ${tableName}_new (id, ${mColumnName}, ${nColumnName})
            SELECT $mColumnName || $nColumnName, ${mColumnName}, $nColumnName FROM $tableName
        """
        )

        exec("DROP TABLE $tableName")
        exec("ALTER TABLE ${tableName}_new RENAME TO $tableName")

        exec("CREATE INDEX index_${tableName}_${mColumnName} ON ${tableName}(${mColumnName})")
        exec("CREATE INDEX index_${tableName}_${nColumnName} ON ${tableName}(${nColumnName})")
    }

    private fun Transaction.musicMigration() {
        exec("ALTER TABLE Music ADD COLUMN coverUrl TEXT")
        exec("ALTER TABLE Music ADD COLUMN dataMode TEXT NOT NULL DEFAULT '${DataMode.Local.value}'")

        exec("""
            CREATE TABLE Music_new (
                id TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                album TEXT NOT NULL,
                artist TEXT NOT NULL,
                coverId TEXT,
                coverUrl TEXT,
                duration INTEGER NOT NULL,
                path TEXT NOT NULL,
                folder TEXT NOT NULL,
                addedDate TEXT NOT NULL,
                nbPlayed INTEGER NOT NULL,
                isInQuickAccess INTEGER NOT NULL,
                isHidden INTEGER NOT NULL,
                dataMode TEXT NOT NULL,
                albumId TEXT,
                FOREIGN KEY (id) REFERENCES Album(id) ON DELETE CASCADE
            )
        """)

        exec("""
            INSERT INTO Music_new (
                id, name, album, artist, coverId, coverUrl, duration, path, folder, addedDate, nbPlayed, isInQuickAccess, isHidden, dataMode, id
            )
            SELECT 
                Music.id, Music.name, Music.album, Music.artist, 
                Music.coverId, Music.coverUrl, Music.duration, Music.path, Music.folder, 
                Music.addedDate, Music.nbPlayed, Music.isInQuickAccess, Music.isHidden, Music.dataMode,
                (SELECT id FROM MusicAlbum WHERE Music.id = MusicAlbum.musicId)
            FROM Music
        """)

        exec("DROP TABLE Music")
        exec("ALTER TABLE Music_new RENAME TO Music")

        exec("CREATE INDEX index_Music_id ON Music(id)")

        exec("DROP TABLE MusicAlbum")
    }
    
    private fun Transaction.albumMigration() {
        exec("""
            CREATE TABLE Album_new (
                id TEXT PRIMARY KEY NOT NULL,
                albumName TEXT NOT NULL,
                coverId TEXT,
                addedDate TEXT NOT NULL,
                nbPlayed INTEGER NOT NULL,
                isInQuickAccess INTEGER NOT NULL,
                artistId TEXT,
                FOREIGN KEY (artistId) REFERENCES Artist(id) ON DELETE CASCADE
            )
        """)

        exec("""
            INSERT INTO Album_new (
                id, albumName, coverId, addedDate, nbPlayed, isInQuickAccess, artistId
            )
            SELECT 
                Album.id, Album.albumName, Album.coverId, 
                Album.addedDate, Album.nbPlayed, Album.isInQuickAccess,
                (SELECT artistId FROM AlbumArtist WHERE Album.id = AlbumArtist.albumId)
            FROM Album
        """)

        exec("DROP TABLE Album")
        exec("ALTER TABLE Album_new RENAME TO Album")

        exec("CREATE INDEX index_Album_artistId ON Album(artistId)")

        exec("DROP TABLE AlbumArtist")
    }

    private fun Transaction.musicArtistMigration() {
        mxnTableMigration(
            tableName = "MusicArtist",
            mColumnName = "musicId",
            nColumnName = "artistId",
            mTableName = "Music",
            nTableName = "Artist",
        )
    }

    private fun Transaction.musicPlaylistMigration() {
        mxnTableMigration(
            tableName = "MusicPlaylist",
            mColumnName = "musicId",
            nColumnName = "playlistId",
            mTableName = "Music",
            nTableName = "Playlist"
        )
    }

    override fun Transaction.migrate() {
        musicMigration()
        albumMigration()
        musicArtistMigration()
        musicPlaylistMigration()
    }
}