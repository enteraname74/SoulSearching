package com.github.enteraname74.localdb.migration

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.async.executeSQL

object Migration20To21 : Migration(20, 21) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.dropViews()
        connection.backupExistingTables()
        connection.dropExistingTables()
        connection.createTables()
        connection.restoreExistingData()
        connection.createIndices()
        connection.dropBackupTables()
        connection.createViews()
    }

    private suspend fun SQLiteConnection.backupExistingTables() {
        listOf(
            "RoomMusic",
            "RoomAlbum",
            "RoomArtist",
            "RoomPlaylist",
            "RoomMusicPlaylist",
            "RoomMusicArtist",
            "RoomPlayerMusic",
            "RoomPlayerMusicProgress",
            "RoomPlayerPlayedList",
            "RoomFolder",
        ).forEach { table ->
            executeSQL("DROP TABLE IF EXISTS migration_backup_$table")
            executeSQL("CREATE TABLE migration_backup_$table AS SELECT * FROM $table")
        }
    }

    private suspend fun SQLiteConnection.dropExistingTables() {
        listOf(
            "RoomPlayerMusicProgress",
            "RoomPlayerMusic",
            "RoomMusicPlaylist",
            "RoomMusicArtist",
            "RoomMusic",
            "RoomAlbum",
            "RoomArtist",
            "RoomPlaylist",
            "RoomPlayerPlayedList",
            "RoomFolder",
        ).forEach { table ->
            executeSQL("DROP TABLE IF EXISTS $table")
        }
    }

    private suspend fun SQLiteConnection.createTables() {
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomMusic (
                musicId TEXT NOT NULL,
                remoteId TEXT,
                lastUpdateMillis INTEGER,
                name TEXT NOT NULL,
                coverId TEXT,
                coverUrl TEXT,
                duration INTEGER NOT NULL,
                path TEXT,
                localPath TEXT,
                remotePath TEXT,
                folder TEXT NOT NULL,
                addedDate INTEGER NOT NULL,
                nbPlayed INTEGER NOT NULL,
                isInQuickAccess INTEGER NOT NULL,
                isHidden INTEGER NOT NULL,
                albumPosition INTEGER,
                albumId TEXT NOT NULL,
                scope TEXT NOT NULL,
                PRIMARY KEY(musicId),
                FOREIGN KEY(albumId) REFERENCES RoomAlbum(albumId) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomAlbum (
                albumId TEXT NOT NULL,
                remoteId TEXT,
                albumName TEXT NOT NULL,
                coverId TEXT,
                coverUrl TEXT,
                addedDate INTEGER NOT NULL,
                nbPlayed INTEGER NOT NULL,
                isInQuickAccess INTEGER NOT NULL,
                artistId TEXT NOT NULL,
                lastUpdatedMillis INTEGER,
                scope TEXT NOT NULL,
                PRIMARY KEY(albumId),
                FOREIGN KEY(artistId) REFERENCES RoomArtist(artistId) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomArtist (
                artistId TEXT NOT NULL,
                remoteId TEXT,
                artistName TEXT NOT NULL,
                coverId TEXT,
                coverFolderKey TEXT,
                coverUrl TEXT,
                addedDate INTEGER NOT NULL,
                nbPlayed INTEGER NOT NULL,
                isInQuickAccess INTEGER NOT NULL,
                lastUpdatedMillis INTEGER,
                scope TEXT NOT NULL,
                PRIMARY KEY(artistId)
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomPlaylist (
                playlistId TEXT NOT NULL,
                remoteId TEXT,
                name TEXT NOT NULL,
                coverId TEXT,
                coverUrl TEXT,
                isFavorite INTEGER NOT NULL,
                addedDate INTEGER NOT NULL,
                nbPlayed INTEGER NOT NULL,
                isInQuickAccess INTEGER NOT NULL,
                lastUpdatedMillis INTEGER,
                PRIMARY KEY(playlistId)
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomMusicPlaylist (
                id TEXT NOT NULL,
                musicId TEXT NOT NULL,
                playlistId TEXT NOT NULL,
                PRIMARY KEY(id),
                FOREIGN KEY(musicId) REFERENCES RoomMusic(musicId) ON DELETE CASCADE,
                FOREIGN KEY(playlistId) REFERENCES RoomPlaylist(playlistId) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomMusicArtist (
                id TEXT NOT NULL,
                musicId TEXT NOT NULL,
                artistId TEXT NOT NULL,
                PRIMARY KEY(id),
                FOREIGN KEY(musicId) REFERENCES RoomMusic(musicId) ON DELETE CASCADE,
                FOREIGN KEY(artistId) REFERENCES RoomArtist(artistId) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomPlayerMusic (
                id TEXT NOT NULL,
                musicId TEXT NOT NULL,
                playedListId TEXT NOT NULL,
                `order` REAL NOT NULL,
                shuffledOrder REAL NOT NULL,
                lastPlayedMillis INTEGER,
                PRIMARY KEY(id),
                FOREIGN KEY(musicId) REFERENCES RoomMusic(musicId) ON DELETE CASCADE,
                FOREIGN KEY(playedListId) REFERENCES RoomPlayerPlayedList(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomPlayerMusicProgress (
                id TEXT NOT NULL,
                playedListId TEXT NOT NULL,
                playerMusicId TEXT NOT NULL,
                progress INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomPlayerPlayedList (
                id TEXT NOT NULL,
                playlistId TEXT,
                isMainPlaylist INTEGER NOT NULL,
                mode TEXT NOT NULL,
                state TEXT NOT NULL,
                invitationCode TEXT,
                scope TEXT NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomFolder (
                folderPath TEXT NOT NULL,
                isSelected INTEGER NOT NULL,
                PRIMARY KEY(folderPath)
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomUser (
                id TEXT NOT NULL,
                username TEXT NOT NULL,
                accessToken TEXT NOT NULL,
                refreshToken TEXT NOT NULL,
                type TEXT NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomCloudPreferences (
                id TEXT NOT NULL,
                url TEXT NOT NULL,
                lastSyncMillis INTEGER,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomDeviceId (
                id TEXT NOT NULL,
                deviceId TEXT NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomSharedPlayedListUser (
                id TEXT NOT NULL,
                playedListId TEXT NOT NULL,
                userId TEXT NOT NULL,
                deviceId TEXT NOT NULL,
                username TEXT NOT NULL,
                joinedAt INTEGER NOT NULL,
                isOwner INTEGER NOT NULL,
                status TEXT NOT NULL,
                PRIMARY KEY(id),
                FOREIGN KEY(playedListId) REFERENCES RoomPlayerPlayedList(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomUserInscriptionCode (
                code TEXT NOT NULL,
                PRIMARY KEY(code)
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomSimpleUser (
                id TEXT NOT NULL,
                username TEXT NOT NULL,
                type TEXT NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomPlayerMusicUser (
                playedListId TEXT NOT NULL,
                userId TEXT NOT NULL,
                musicId TEXT NOT NULL,
                PRIMARY KEY(playedListId, userId, musicId),
                FOREIGN KEY(playedListId) REFERENCES RoomPlayerPlayedList(id) ON DELETE CASCADE,
                FOREIGN KEY(musicId) REFERENCES RoomMusic(musicId) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomSharedPlayedListPreview (
                id TEXT NOT NULL,
                code TEXT NOT NULL,
                totalUsers INTEGER NOT NULL,
                connectedUsers INTEGER NOT NULL,
                createdAtMillis INTEGER NOT NULL,
                isOwner INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
    }

    private suspend fun SQLiteConnection.restoreExistingData() {
        executeSQL(
            """
            INSERT INTO RoomArtist (
                artistId, remoteId, artistName, coverId, coverFolderKey, coverUrl, addedDate,
                nbPlayed, isInQuickAccess, lastUpdatedMillis, scope
            )
            SELECT
                ${uuid("artistId")}, NULL, artistName, ${uuid("coverId")}, coverFolderKey, NULL,
                ${localDateTimeToMillis("addedDate")},
                nbPlayed, isInQuickAccess, NULL, 'User'
            FROM migration_backup_RoomArtist
            """.trimIndent()
        )
        executeSQL(
            """
            INSERT INTO RoomAlbum (
                albumId, remoteId, albumName, coverId, coverUrl, addedDate, nbPlayed,
                isInQuickAccess, artistId, lastUpdatedMillis, scope
            )
            SELECT
                ${uuid("albumId")}, NULL, albumName, ${uuid("coverId")}, NULL,
                ${localDateTimeToMillis("addedDate")},
                nbPlayed, isInQuickAccess, ${uuid("artistId")}, NULL, 'User'
            FROM migration_backup_RoomAlbum
            """.trimIndent()
        )
        executeSQL(
            """
            INSERT INTO RoomMusic (
                musicId, remoteId, lastUpdateMillis, name, coverId, coverUrl, duration,
                path, localPath, remotePath, folder, addedDate, nbPlayed, isInQuickAccess,
                isHidden, albumPosition, albumId, scope
            )
            SELECT
                ${uuid("musicId")}, NULL, NULL, name, ${uuid("coverId")}, NULL, duration,
                path, path, NULL, folder, ${localDateTimeToMillis("addedDate")},
                nbPlayed, isInQuickAccess, isHidden, albumPosition, ${uuid("albumId")}, 'User'
            FROM migration_backup_RoomMusic
            """.trimIndent()
        )
        executeSQL(
            """
            INSERT INTO RoomPlaylist (
                playlistId, remoteId, name, coverId, coverUrl, isFavorite, addedDate,
                nbPlayed, isInQuickAccess, lastUpdatedMillis
            )
            SELECT
                ${uuid("playlistId")}, NULL, name, ${uuid("coverId")}, NULL, isFavorite,
                ${localDateTimeToMillis("addedDate")},
                nbPlayed, isInQuickAccess, NULL
            FROM migration_backup_RoomPlaylist
            """.trimIndent()
        )
        executeSQL(
            """
            INSERT INTO RoomPlayerPlayedList (
                id, playlistId, isMainPlaylist, mode, state, invitationCode, scope
            )
            SELECT
                ${uuid("id")}, playlistId, isMainPlaylist, mode, state, NULL, 'LocalUser'
            FROM migration_backup_RoomPlayerPlayedList
            """.trimIndent()
        )
        executeSQL(
            """
            INSERT INTO RoomMusicPlaylist (id, musicId, playlistId)
            SELECT id, ${uuid("musicId")}, ${uuid("playlistId")}
            FROM migration_backup_RoomMusicPlaylist
            """.trimIndent()
        )
        executeSQL(
            """
            INSERT INTO RoomMusicArtist (id, musicId, artistId)
            SELECT id, ${uuid("musicId")}, ${uuid("artistId")}
            FROM migration_backup_RoomMusicArtist
            """.trimIndent()
        )
        executeSQL(
            """
            INSERT INTO RoomPlayerMusic (
                id, musicId, playedListId, `order`, shuffledOrder, lastPlayedMillis
            )
            SELECT
                id, ${uuid("musicId")}, ${uuid("playedListId")},
                `order`, shuffledOrder, lastPlayedMillis
            FROM migration_backup_RoomPlayerMusic
            """.trimIndent()
        )
        executeSQL(
            """
            INSERT INTO RoomPlayerMusicProgress (id, playedListId, playerMusicId, progress)
            SELECT id, ${uuid("playedListId")}, playerMusicId, progress
            FROM migration_backup_RoomPlayerMusicProgress
            """.trimIndent()
        )
        executeSQL(
            """
            INSERT INTO RoomFolder (folderPath, isSelected)
            SELECT folderPath, isSelected
            FROM migration_backup_RoomFolder
            """.trimIndent()
        )
    }

    private suspend fun SQLiteConnection.createIndices() {
        executeSQL("CREATE INDEX IF NOT EXISTS index_RoomMusic_albumId ON RoomMusic(albumId)")
        executeSQL("CREATE INDEX IF NOT EXISTS index_RoomAlbum_artistId ON RoomAlbum(artistId)")
        executeSQL("CREATE INDEX IF NOT EXISTS index_RoomMusicPlaylist_musicId ON RoomMusicPlaylist(musicId)")
        executeSQL("CREATE INDEX IF NOT EXISTS index_RoomMusicPlaylist_playlistId ON RoomMusicPlaylist(playlistId)")
        executeSQL("CREATE INDEX IF NOT EXISTS index_RoomMusicArtist_musicId ON RoomMusicArtist(musicId)")
        executeSQL("CREATE INDEX IF NOT EXISTS index_RoomMusicArtist_artistId ON RoomMusicArtist(artistId)")
        executeSQL("CREATE INDEX IF NOT EXISTS index_RoomPlayerMusic_playedListId ON RoomPlayerMusic(playedListId)")
        executeSQL("CREATE INDEX IF NOT EXISTS index_RoomPlayerMusic_musicId ON RoomPlayerMusic(musicId)")
        executeSQL("CREATE INDEX IF NOT EXISTS index_RoomSharedPlayedListUser_playedListId ON RoomSharedPlayedListUser(playedListId)")
    }

    private suspend fun SQLiteConnection.dropBackupTables() {
        listOf(
            "RoomMusic",
            "RoomAlbum",
            "RoomArtist",
            "RoomPlaylist",
            "RoomMusicPlaylist",
            "RoomMusicArtist",
            "RoomPlayerMusic",
            "RoomPlayerMusicProgress",
            "RoomPlayerPlayedList",
            "RoomFolder",
        ).forEach { table ->
            executeSQL("DROP TABLE migration_backup_$table")
        }
    }

    private suspend fun SQLiteConnection.dropViews() {
        executeSQL("DROP VIEW IF EXISTS CurrentPlayerMusicsView")
        executeSQL("DROP VIEW IF EXISTS RoomMusicFolderPreview")
        executeSQL("DROP VIEW IF EXISTS RoomMonthMusicPreview")
        executeSQL("DROP VIEW IF EXISTS RoomAlbumPreview")
        executeSQL("DROP VIEW IF EXISTS RoomArtistPreview")
        executeSQL("DROP VIEW IF EXISTS RoomPlaylistPreview")
    }

    private suspend fun SQLiteConnection.createViews() {
        executeSQL(
            """
            CREATE VIEW CurrentPlayerMusicsView AS WITH currentPlayedList AS (
                SELECT * FROM RoomPlayerPlayedList
                WHERE state != 'Cached'
                LIMIT 1
            )
            SELECT
                m.*,
                CASE
                    WHEN c.mode = 'Shuffle'
                    THEN m.shuffledOrder
                    ELSE m.`order`
                END AS currentOrder,
                c.mode
            FROM RoomPlayerMusic m
            INNER JOIN currentPlayedList c ON m.playedListId = c.id
            ORDER BY currentOrder
            """.trimIndent()
        )
        executeSQL(
            """
            CREATE VIEW RoomMusicFolderPreview AS SELECT
                folderMusic.folder,
                COUNT(*) AS totalMusics,
                (
                    SELECT music.coverId FROM RoomMusic AS music
                    WHERE music.isHidden = 0
                    AND scope != 'SharedPlayedList'
                    AND music.coverId IS NOT NULL
                    AND music.folder = folderMusic.folder
                    ORDER BY
                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END,
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
                    SELECT music.coverUrl FROM RoomMusic AS music
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
            CREATE VIEW RoomMonthMusicPreview AS SELECT
                strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch') AS month,
                COUNT(*) AS totalMusics,
                (
                    SELECT music.coverId FROM RoomMusic AS music
                    WHERE music.isHidden = 0
                    AND scope != 'SharedPlayedList'
                    AND music.coverId IS NOT NULL
                    AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') =
                        strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch')
                    ORDER BY
                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END,
                    name
                    LIMIT 1
                ) AS coverId,
                (
                    SELECT music.localPath FROM RoomMusic AS music
                    WHERE music.isHidden = 0
                    AND scope != 'SharedPlayedList'
                    AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') =
                        strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch')
                    ORDER BY name
                    LIMIT 1
                ) AS musicCoverPath,
                (
                    SELECT music.coverUrl FROM RoomMusic AS music
                    WHERE music.isHidden = 0
                    AND scope != 'SharedPlayedList'
                    AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') =
                        strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch')
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
            CREATE VIEW RoomAlbumPreview AS SELECT
                album.albumId AS id,
                album.albumName AS name,
                album.nbPlayed,
                album.addedDate,
                album.artistId,
                album.coverUrl,
                (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist,
                (
                    CASE WHEN album.coverId IS NULL THEN
                        (
                            SELECT music.coverId FROM RoomMusic AS music
                            WHERE music.albumId = album.albumId AND music.isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY
                            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END,
                            music.albumPosition,
                            CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END,
                            music.name
                        )
                    ELSE album.coverId END
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
                    SELECT music.coverUrl FROM RoomMusic AS music
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
            CREATE VIEW RoomArtistPreview AS SELECT
                artist.artistId AS id,
                artist.artistName AS name,
                artist.coverFolderKey,
                artist.addedDate,
                artist.nbPlayed,
                artist.coverUrl,
                (SELECT COUNT(*) FROM RoomMusicArtist AS musicArtist WHERE musicArtist.artistId = artist.artistId) AS totalMusics,
                (
                    CASE WHEN artist.coverId IS NULL THEN
                        (
                            SELECT music.coverId FROM RoomMusic AS music
                            INNER JOIN RoomMusicArtist AS musicArtist
                            ON music.musicId = musicArtist.musicId
                            AND artist.artistId = musicArtist.artistId
                            AND music.isHidden = 0
                            AND scope != 'SharedPlayedList'
                            AND music.coverId IS NOT NULL
                            ORDER BY name ASC
                            LIMIT 1
                        )
                    ELSE artist.coverId END
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
                    SELECT music.coverUrl FROM RoomMusic AS music
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
        executeSQL(
            """
            CREATE VIEW RoomPlaylistPreview AS SELECT playlist.playlistId AS id,
                playlist.name,
                playlist.isFavorite,
                playlist.addedDate,
                playlist.coverUrl,
                (
                    SELECT COUNT(*)
                    FROM RoomMusicPlaylist AS musicPlaylist
                    WHERE musicPlaylist.playlistId = playlist.playlistId
                ) AS totalMusics,
                (
                    CASE WHEN playlist.coverId IS NULL THEN
                        (
                            SELECT music.coverId FROM RoomMusic AS music
                            INNER JOIN RoomMusicPlaylist AS musicPlaylist
                            ON music.musicId = musicPlaylist.musicId
                            AND playlist.playlistId = musicPlaylist.playlistId
                            AND music.isHidden = 0
                            AND scope != 'SharedPlayedList'
                            AND music.coverId IS NOT NULL
                            LIMIT 1
                        )
                    ELSE playlist.coverId END
                ) AS coverId,
                (
                    SELECT music.localPath FROM RoomMusic AS music
                    INNER JOIN RoomMusicPlaylist AS musicPlaylist
                    ON music.musicId = musicPlaylist.musicId
                    AND playlist.playlistId = musicPlaylist.playlistId
                    AND music.isHidden = 0
                    AND scope != 'SharedPlayedList'
                    LIMIT 1
                ) AS musicCoverPath,
                (
                    SELECT music.coverUrl FROM RoomMusic AS music
                    INNER JOIN RoomMusicPlaylist AS musicPlaylist
                    ON music.musicId = musicPlaylist.musicId
                    AND playlist.playlistId = musicPlaylist.playlistId
                    AND music.isHidden = 0
                    AND scope != 'SharedPlayedList'
                    LIMIT 1
                ) AS musicCoverUrl,
                playlist.isInQuickAccess,
                playlist.nbPlayed
            FROM RoomPlaylist AS playlist
            """.trimIndent()
        )
    }

    private suspend fun uuid(column: String): String =
        """
        CASE WHEN $column IS NULL THEN NULL ELSE lower(
            substr(hex($column), 1, 8) || '-' ||
            substr(hex($column), 9, 4) || '-' ||
            substr(hex($column), 13, 4) || '-' ||
            substr(hex($column), 17, 4) || '-' ||
            substr(hex($column), 21, 12)
        ) END
        """.trimIndent()

    private suspend fun localDateTimeToMillis(column: String): String =
        "CAST(strftime('%s', $column) AS INTEGER) * 1000"
}
