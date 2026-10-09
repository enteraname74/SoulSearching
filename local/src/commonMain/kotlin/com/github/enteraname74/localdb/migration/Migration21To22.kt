package com.github.enteraname74.localdb.migration

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.async.executeSQL
import com.github.enteraname74.localdb.view.ROOM_COLLECTION_PREVIEW_QUERY_V22
import com.github.enteraname74.localdb.view.ROOM_PLAYLIST_PREVIEW_QUERY_V22

object Migration21To22 : Migration(21, 22) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.dropCoverViews()
        connection.migrateCoverColumns()

        connection.executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomCollection (
                collectionId TEXT NOT NULL,
                remoteId TEXT,
                name TEXT NOT NULL,
                addedDate INTEGER NOT NULL,
                nbPlayed INTEGER NOT NULL,
                isInQuickAccess INTEGER NOT NULL,
                lastUpdatedMillis INTEGER,
                cover_simple_initialCoverPath TEXT,
                cover_simple_fileCoverId TEXT,
                cover_simple_url TEXT,
                cover_simple_devicePathSpecKey TEXT,
                cover_grid_topStart_initialCoverPath TEXT,
                cover_grid_topStart_fileCoverId TEXT,
                cover_grid_topStart_url TEXT,
                cover_grid_topStart_devicePathSpecKey TEXT,
                cover_grid_topEnd_initialCoverPath TEXT,
                cover_grid_topEnd_fileCoverId TEXT,
                cover_grid_topEnd_url TEXT,
                cover_grid_topEnd_devicePathSpecKey TEXT,
                cover_grid_bottomStart_initialCoverPath TEXT,
                cover_grid_bottomStart_fileCoverId TEXT,
                cover_grid_bottomStart_url TEXT,
                cover_grid_bottomStart_devicePathSpecKey TEXT,
                cover_grid_bottomEnd_initialCoverPath TEXT,
                cover_grid_bottomEnd_fileCoverId TEXT,
                cover_grid_bottomEnd_url TEXT,
                cover_grid_bottomEnd_devicePathSpecKey TEXT,
                PRIMARY KEY(collectionId)
            )
            """.trimIndent()
        )
        connection.executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomCollectionAlbum (
                collectionId TEXT NOT NULL,
                albumId TEXT NOT NULL,
                PRIMARY KEY(collectionId, albumId),
                FOREIGN KEY(collectionId) REFERENCES RoomCollection(collectionId) ON DELETE CASCADE,
                FOREIGN KEY(albumId) REFERENCES RoomAlbum(albumId) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        connection.executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomCollectionArtist (
                collectionId TEXT NOT NULL,
                artistId TEXT NOT NULL,
                PRIMARY KEY(collectionId, artistId),
                FOREIGN KEY(collectionId) REFERENCES RoomCollection(collectionId) ON DELETE CASCADE,
                FOREIGN KEY(artistId) REFERENCES RoomArtist(artistId) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        connection.executeSQL("CREATE INDEX IF NOT EXISTS index_RoomCollectionAlbum_albumId ON RoomCollectionAlbum(albumId)")
        connection.executeSQL("CREATE INDEX IF NOT EXISTS index_RoomCollectionArtist_artistId ON RoomCollectionArtist(artistId)")

        connection.executeSQL(
            "ALTER TABLE RoomListeningStatistics ADD COLUMN collectionId TEXT REFERENCES RoomCollection(collectionId) ON DELETE CASCADE"
        )
        connection.executeSQL(
            "CREATE INDEX IF NOT EXISTS index_RoomListeningStatistics_collectionId ON RoomListeningStatistics(collectionId)"
        )

        connection.executeSQL(
            """
            DELETE FROM RoomMusicArtist
            WHERE rowid NOT IN (
                SELECT MIN(rowid)
                FROM RoomMusicArtist
                GROUP BY artistId, musicId
            )
            """.trimIndent()
        )
        connection.executeSQL("DROP INDEX IF EXISTS index_RoomMusicArtist_artistId")
        connection.executeSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_RoomMusicArtist_artistId_musicId ON RoomMusicArtist(artistId, musicId)"
        )

        connection.executeSQL(
            """
            DELETE FROM RoomMusicPlaylist
            WHERE rowid NOT IN (
                SELECT MIN(rowid)
                FROM RoomMusicPlaylist
                GROUP BY playlistId, musicId
            )
            """.trimIndent()
        )
        connection.executeSQL("DROP INDEX IF EXISTS index_RoomMusicPlaylist_playlistId")
        connection.executeSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_RoomMusicPlaylist_playlistId_musicId ON RoomMusicPlaylist(playlistId, musicId)"
        )

        connection.createCoverViews()
    }

    private suspend fun SQLiteConnection.dropCoverViews() {
        listOf(
            "RoomMusicFolderPreview",
            "RoomMonthMusicPreview",
            "RoomAlbumPreview",
            "RoomArtistPreview",
            "RoomPlaylistPreview",
            "RoomCollectionPreview",
        ).forEach { view -> executeSQL("DROP VIEW IF EXISTS $view") }
    }

    private suspend fun SQLiteConnection.migrateCoverColumns() {
        executeSQL("ALTER TABLE RoomMusic RENAME COLUMN coverId TO cover_fileCoverId")
        executeSQL("ALTER TABLE RoomMusic RENAME COLUMN coverUrl TO cover_url")
        executeSQL("ALTER TABLE RoomMusic ADD COLUMN cover_initialCoverPath TEXT")
        executeSQL("ALTER TABLE RoomMusic ADD COLUMN cover_devicePathSpecKey TEXT")
        executeSQL("UPDATE RoomMusic SET cover_initialCoverPath = localPath")

        executeSQL("ALTER TABLE RoomAlbum RENAME COLUMN coverId TO cover_fileCoverId")
        executeSQL("ALTER TABLE RoomAlbum RENAME COLUMN coverUrl TO cover_url")
        executeSQL("ALTER TABLE RoomAlbum ADD COLUMN cover_initialCoverPath TEXT")
        executeSQL("ALTER TABLE RoomAlbum ADD COLUMN cover_devicePathSpecKey TEXT")

        executeSQL("ALTER TABLE RoomArtist RENAME COLUMN coverId TO cover_fileCoverId")
        executeSQL("ALTER TABLE RoomArtist RENAME COLUMN coverUrl TO cover_url")
        executeSQL("ALTER TABLE RoomArtist RENAME COLUMN coverFolderKey TO cover_devicePathSpecKey")
        executeSQL("ALTER TABLE RoomArtist ADD COLUMN cover_initialCoverPath TEXT")

        executeSQL("ALTER TABLE RoomPlaylist RENAME COLUMN coverId TO cover_simple_fileCoverId")
        executeSQL("ALTER TABLE RoomPlaylist RENAME COLUMN coverUrl TO cover_simple_url")
        executeSQL("ALTER TABLE RoomPlaylist ADD COLUMN cover_simple_initialCoverPath TEXT")
        executeSQL("ALTER TABLE RoomPlaylist ADD COLUMN cover_simple_devicePathSpecKey TEXT")

        listOf("topStart", "topEnd", "bottomStart", "bottomEnd").forEach { position ->
            executeSQL("ALTER TABLE RoomPlaylist ADD COLUMN cover_grid_${position}_initialCoverPath TEXT")
            executeSQL("ALTER TABLE RoomPlaylist ADD COLUMN cover_grid_${position}_fileCoverId TEXT")
            executeSQL("ALTER TABLE RoomPlaylist ADD COLUMN cover_grid_${position}_url TEXT")
            executeSQL("ALTER TABLE RoomPlaylist ADD COLUMN cover_grid_${position}_devicePathSpecKey TEXT")
        }
    }

    private suspend fun SQLiteConnection.createCoverViews() {
        executeSQL(
            """
            CREATE VIEW `RoomMusicFolderPreview` AS SELECT 
                            folderMusic.folder,
                            COUNT(*) AS totalMusics, 
                            (
                                SELECT music.cover_fileCoverId FROM RoomMusic AS music 
                                WHERE music.isHidden = 0 
                                AND scope != 'SharedPlayedList' 
                                AND music.cover_fileCoverId IS NOT NULL 
                                AND music.folder = folderMusic.folder 
                                ORDER BY
                                CASE WHEN music.cover_fileCoverId IS NULL THEN 1 ELSE 0 END, 
                                name 
                                LIMIT 1
                            ) AS coverId,
                            (
                                SELECT music.localPath FROM RoomMusic AS music 
                                WHERE music.isHidden = 0 
                                AND scope != 'SharedPlayedList' 
                                AND music.folder = folderMusic.folder 
                                ORDER BY name 
                                LIMIT 1 
                            ) AS musicCoverPath, 
                            (
                                SELECT music.cover_url FROM RoomMusic AS music 
                                WHERE music.isHidden = 0 
                                AND scope != 'SharedPlayedList' 
                                AND music.folder = folderMusic.folder 
                                ORDER BY name 
                                LIMIT 1 
                            ) AS musicCoverUrl 
                        FROM RoomMusic As folderMusic
                        WHERE isHidden = 0 
                        AND scope != 'SharedPlayedList' 
                        GROUP BY folderMusic.folder
            """.trimIndent()
        )

        executeSQL(
            """
            CREATE VIEW `RoomMonthMusicPreview` AS SELECT 
                            strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch') AS month,
                            COUNT(*) AS totalMusics, 
                            (
                                SELECT music.cover_fileCoverId FROM RoomMusic AS music 
                                WHERE music.isHidden = 0 
                                AND scope != 'SharedPlayedList' 
                                AND music.cover_fileCoverId IS NOT NULL 
                                AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') = strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch')
                                ORDER BY
                                CASE WHEN music.cover_fileCoverId IS NULL THEN 1 ELSE 0 END, 
                                name 
                                LIMIT 1
                            ) AS coverId,
                            (
                                SELECT music.localPath FROM RoomMusic AS music 
                                WHERE music.isHidden = 0 
                                AND scope != 'SharedPlayedList' 
                                AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') = strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch') 
                                ORDER BY name 
                                LIMIT 1 
                            ) AS musicCoverPath, 
                            (
                                SELECT music.cover_url FROM RoomMusic AS music 
                                WHERE music.isHidden = 0 
                                AND scope != 'SharedPlayedList' 
                                AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') = strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch') 
                                ORDER BY name 
                                LIMIT 1 
                            ) AS musicCoverUrl 
                        FROM RoomMusic AS monthMusic
                        WHERE isHidden = 0 
                        AND scope != 'SharedPlayedList' 
                        GROUP BY strftime('%Y-%m', addedDate / 1000, 'unixepoch') 
                        ORDER BY strftime('%Y-%m', addedDate / 1000, 'unixepoch') DESC
            """.trimIndent()
        )

        executeSQL(
            """
            CREATE VIEW `RoomAlbumPreview` AS SELECT 
                    album.albumId AS id, 
                    album.remoteId, 
                    album.albumName AS name, 
                    album.nbPlayed, 
                    album.addedDate, 
                    album.artistId,
                    album.cover_url AS coverUrl, 
                    (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist, 
                    (
                        CASE WHEN album.cover_fileCoverId IS NULL THEN 
                            (
                                SELECT music.cover_fileCoverId FROM RoomMusic AS music 
                                WHERE music.albumId = album.albumId AND music.isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY 
                                CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                                music.albumPosition, 
                                CASE WHEN music.cover_fileCoverId IS NULL THEN 1 ELSE 0 END, 
                                music.name 
                            )
                        ELSE album.cover_fileCoverId END
                    ) AS coverId,
                    (
                        SELECT music.localPath FROM RoomMusic AS music 
                        WHERE music.albumId = album.albumId AND music.isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY 
                        CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                        music.albumPosition, 
                        music.name 
                        LIMIT 1 
                    ) AS musicCoverPath,
                    (
                        SELECT music.cover_url FROM RoomMusic AS music 
                        WHERE music.albumId = album.albumId AND music.isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY 
                        CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                        music.albumPosition, 
                        music.name 
                        LIMIT 1 
                    ) AS musicCoverUrl,
                    album.isInQuickAccess 
                    FROM RoomAlbum AS album 
                    WHERE album.scope != 'SharedPlayedList'
            """.trimIndent()
        )

        executeSQL(
            """
            CREATE VIEW `RoomArtistPreview` AS SELECT 
                    artist.artistId AS id, 
                    artist.remoteId,
                    artist.artistName AS name, 
                    artist.cover_devicePathSpecKey AS coverFolderKey,
                    artist.addedDate, 
                    artist.nbPlayed, 
                    artist.cover_url AS coverUrl, 
                    (SELECT COUNT(*) FROM RoomMusicArtist AS musicArtist WHERE musicArtist.artistId = artist.artistId) AS totalMusics, 
                    (
                        CASE WHEN artist.cover_fileCoverId IS NULL THEN 
                            (
                                SELECT music.cover_fileCoverId FROM RoomMusic AS music 
                                INNER JOIN RoomMusicArtist AS musicArtist 
                                ON music.musicId = musicArtist.musicId 
                                AND artist.artistId = musicArtist.artistId 
                                AND music.isHidden = 0 
                                AND scope != 'SharedPlayedList' 
                                AND music.cover_fileCoverId IS NOT NULL 
                                ORDER BY name ASC 
                                LIMIT 1
                            )
                        ELSE artist.cover_fileCoverId END
                    ) AS coverId,
                    (
                        SELECT music.localPath FROM RoomMusic AS music 
                        INNER JOIN RoomMusicArtist AS musicArtist 
                        ON music.musicId = musicArtist.musicId 
                        AND artist.artistId = musicArtist.artistId 
                        AND music.isHidden = 0 
                        AND scope != 'SharedPlayedList' 
                        ORDER BY name ASC 
                        LIMIT 1
                    ) AS musicCoverPath,
                    (
                        SELECT music.cover_url FROM RoomMusic AS music 
                        INNER JOIN RoomMusicArtist AS musicArtist 
                        ON music.musicId = musicArtist.musicId 
                        AND artist.artistId = musicArtist.artistId 
                        AND music.isHidden = 0 
                        AND scope != 'SharedPlayedList' 
                        ORDER BY name ASC 
                        LIMIT 1
                    ) AS musicCoverUrl,
                    artist.isInQuickAccess 
                    FROM RoomArtist AS artist 
                    WHERE artist.scope != 'SharedPlayedList'
            """.trimIndent()
        )

        executeSQL("CREATE VIEW `RoomPlaylistPreview` AS $ROOM_PLAYLIST_PREVIEW_QUERY_V22")
        executeSQL("CREATE VIEW `RoomCollectionPreview` AS $ROOM_COLLECTION_PREVIEW_QUERY_V22")
    }
}




