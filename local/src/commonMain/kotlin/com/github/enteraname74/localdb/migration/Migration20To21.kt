package com.github.enteraname74.localdb.migration

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.async.executeSQL

object Migration20To21 : Migration(20, 21) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.backupExistingTables()
        connection.dropViews()
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
            "RoomPlayerMusicUser",
            "RoomSharedPlayedListUser",
            "RoomPlayerMusicProgress",
            "RoomPlayerMusic",
            "RoomMusicPlaylist",
            "RoomMusicArtist",
            "RoomSharedPlayedListPreview",
            "RoomUserInscriptionCode",
            "RoomSimpleUser",
            "RoomMusic",
            "RoomAlbum",
            "RoomArtist",
            "RoomPlaylist",
            "RoomPlayerPlayedList",
            "RoomFolder",
            "RoomUser",
            "RoomCloudPreferences",
            "RoomDeviceId",
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
        executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomUserStorage (
                id TEXT NOT NULL,
                max TEXT NOT NULL,
                current REAL NOT NULL,
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
            "CREATE VIEW `CurrentPlayerMusicsView` AS WITH currentPlayedList AS (\n" +
                "        SELECT * FROM RoomPlayerPlayedList  \n" +
                "        WHERE state != 'Cached'\n" +
                "        LIMIT 1\n" +
                "    )\n" +
                "    SELECT \n" +
                "        m.*,\n" +
                "        CASE\n" +
                "            WHEN c.mode = 'Shuffle'\n" +
                "            THEN m.shuffledOrder\n" +
                "            ELSE m.`order`\n" +
                "        END AS currentOrder,\n" +
                "        c.mode \n" +
                "    FROM RoomPlayerMusic m\n" +
                "    INNER JOIN currentPlayedList c ON m.playedListId = c.id\n" +
                "    ORDER BY currentOrder"
        )
        executeSQL(
            "CREATE VIEW `RoomMusicFolderPreview` AS SELECT \n" +
                "                folderMusic.folder,\n" +
                "                COUNT(*) AS totalMusics, \n" +
                "                (\n" +
                "                    SELECT music.coverId FROM RoomMusic AS music \n" +
                "                    WHERE music.isHidden = 0 \n" +
                "                    AND scope != 'SharedPlayedList' \n" +
                "                    AND music.coverId IS NOT NULL \n" +
                "                    AND music.folder = folderMusic.folder \n" +
                "                    ORDER BY\n" +
                "                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, \n" +
                "                    name \n" +
                "                    LIMIT 1\n" +
                "                ) AS coverId,\n" +
                "                (\n" +
                "                    SELECT music.localPath FROM RoomMusic AS music \n" +
                "                    WHERE music.isHidden = 0 \n" +
                "                    AND scope != 'SharedPlayedList' \n" +
                "                    AND music.folder = folderMusic.folder \n" +
                "                    ORDER BY name \n" +
                "                    LIMIT 1 \n" +
                "                ) AS musicCoverPath, \n" +
                "                (\n" +
                "                    SELECT music.coverUrl FROM RoomMusic AS music \n" +
                "                    WHERE music.isHidden = 0 \n" +
                "                    AND scope != 'SharedPlayedList' \n" +
                "                    AND music.folder = folderMusic.folder \n" +
                "                    ORDER BY name \n" +
                "                    LIMIT 1 \n" +
                "                ) AS musicCoverUrl \n" +
                "            FROM RoomMusic As folderMusic\n" +
                "            WHERE isHidden = 0 \n" +
                "            AND scope != 'SharedPlayedList' \n" +
                "            GROUP BY folderMusic.folder"
        )
        executeSQL(
            "CREATE VIEW `RoomMonthMusicPreview` AS SELECT \n" +
                "                strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch') AS month,\n" +
                "                COUNT(*) AS totalMusics, \n" +
                "                (\n" +
                "                    SELECT music.coverId FROM RoomMusic AS music \n" +
                "                    WHERE music.isHidden = 0 \n" +
                "                    AND scope != 'SharedPlayedList' \n" +
                "                    AND music.coverId IS NOT NULL \n" +
                "                    AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') = strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch')\n" +
                "                    ORDER BY\n" +
                "                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, \n" +
                "                    name \n" +
                "                    LIMIT 1\n" +
                "                ) AS coverId,\n" +
                "                (\n" +
                "                    SELECT music.localPath FROM RoomMusic AS music \n" +
                "                    WHERE music.isHidden = 0 \n" +
                "                    AND scope != 'SharedPlayedList' \n" +
                "                    AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') = strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch') \n" +
                "                    ORDER BY name \n" +
                "                    LIMIT 1 \n" +
                "                ) AS musicCoverPath, \n" +
                "                (\n" +
                "                    SELECT music.coverUrl FROM RoomMusic AS music \n" +
                "                    WHERE music.isHidden = 0 \n" +
                "                    AND scope != 'SharedPlayedList' \n" +
                "                    AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') = strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch') \n" +
                "                    ORDER BY name \n" +
                "                    LIMIT 1 \n" +
                "                ) AS musicCoverUrl \n" +
                "            FROM RoomMusic AS monthMusic\n" +
                "            WHERE isHidden = 0 \n" +
                "            AND scope != 'SharedPlayedList' \n" +
                "            GROUP BY strftime('%Y-%m', addedDate / 1000, 'unixepoch') \n" +
                "            ORDER BY strftime('%Y-%m', addedDate / 1000, 'unixepoch') DESC"
        )
        executeSQL(
            "CREATE VIEW `RoomAlbumPreview` AS SELECT \n" +
                "        album.albumId AS id, \n" +
                "        album.albumName AS name, \n" +
                "        album.nbPlayed, \n" +
                "        album.addedDate, \n" +
                "        album.artistId,\n" +
                "        album.coverUrl, \n" +
                "        (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist, \n" +
                "        (\n" +
                "            CASE WHEN album.coverId IS NULL THEN \n" +
                "                (\n" +
                "                    SELECT music.coverId FROM RoomMusic AS music \n" +
                "                    WHERE music.albumId = album.albumId AND music.isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY \n" +
                "                    CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, \n" +
                "                    music.albumPosition, \n" +
                "                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, \n" +
                "                    music.name \n" +
                "                )\n" +
                "            ELSE album.coverId END\n" +
                "        ) AS coverId,\n" +
                "        (\n" +
                "            SELECT music.localPath FROM RoomMusic AS music \n" +
                "            WHERE music.albumId = album.albumId AND music.isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY \n" +
                "            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, \n" +
                "            music.albumPosition, \n" +
                "            music.name \n" +
                "            LIMIT 1 \n" +
                "        ) AS musicCoverPath,\n" +
                "        (\n" +
                "            SELECT music.coverUrl FROM RoomMusic AS music \n" +
                "            WHERE music.albumId = album.albumId AND music.isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY \n" +
                "            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, \n" +
                "            music.albumPosition, \n" +
                "            music.name \n" +
                "            LIMIT 1 \n" +
                "        ) AS musicCoverUrl,\n" +
                "        album.isInQuickAccess \n" +
                "        FROM RoomAlbum AS album \n" +
                "        WHERE album.scope != 'SharedPlayedList'"
        )
        executeSQL(
            "CREATE VIEW `RoomArtistPreview` AS SELECT \n" +
                "        artist.artistId AS id, \n" +
                "        artist.artistName AS name, \n" +
                "        artist.coverFolderKey,\n" +
                "        artist.addedDate, \n" +
                "        artist.nbPlayed, \n" +
                "        artist.coverUrl, \n" +
                "        (SELECT COUNT(*) FROM RoomMusicArtist AS musicArtist WHERE musicArtist.artistId = artist.artistId) AS totalMusics, \n" +
                "        (\n" +
                "            CASE WHEN artist.coverId IS NULL THEN \n" +
                "                (\n" +
                "                    SELECT music.coverId FROM RoomMusic AS music \n" +
                "                    INNER JOIN RoomMusicArtist AS musicArtist \n" +
                "                    ON music.musicId = musicArtist.musicId \n" +
                "                    AND artist.artistId = musicArtist.artistId \n" +
                "                    AND music.isHidden = 0 \n" +
                "                    AND scope != 'SharedPlayedList' \n" +
                "                    AND music.coverId IS NOT NULL \n" +
                "                    ORDER BY name ASC \n" +
                "                    LIMIT 1\n" +
                "                )\n" +
                "            ELSE artist.coverId END\n" +
                "        ) AS coverId,\n" +
                "        (\n" +
                "            SELECT music.localPath FROM RoomMusic AS music \n" +
                "            INNER JOIN RoomMusicArtist AS musicArtist \n" +
                "            ON music.musicId = musicArtist.musicId \n" +
                "            AND artist.artistId = musicArtist.artistId \n" +
                "            AND music.isHidden = 0 \n" +
                "            AND scope != 'SharedPlayedList' \n" +
                "            ORDER BY name ASC \n" +
                "            LIMIT 1\n" +
                "        ) AS musicCoverPath,\n" +
                "        (\n" +
                "            SELECT music.coverUrl FROM RoomMusic AS music \n" +
                "            INNER JOIN RoomMusicArtist AS musicArtist \n" +
                "            ON music.musicId = musicArtist.musicId \n" +
                "            AND artist.artistId = musicArtist.artistId \n" +
                "            AND music.isHidden = 0 \n" +
                "            AND scope != 'SharedPlayedList' \n" +
                "            ORDER BY name ASC \n" +
                "            LIMIT 1\n" +
                "        ) AS musicCoverUrl,\n" +
                "        artist.isInQuickAccess \n" +
                "        FROM RoomArtist AS artist \n" +
                "        WHERE artist.scope != 'SharedPlayedList'"
        )
        executeSQL(
            "CREATE VIEW `RoomPlaylistPreview` AS SELECT playlist.playlistId AS id, \n" +
                "        playlist.name, \n" +
                "        playlist.isFavorite, \n" +
                "        playlist.addedDate, \n" +
                "        playlist.coverUrl, \n" +
                "        (\n" +
                "            SELECT COUNT(*) \n" +
                "            FROM RoomMusicPlaylist AS musicPlaylist \n" +
                "            WHERE musicPlaylist.playlistId = playlist.playlistId\n" +
                "        ) AS totalMusics, \n" +
                "        (\n" +
                "            CASE WHEN playlist.coverId IS NULL THEN \n" +
                "                (\n" +
                "                    SELECT music.coverId FROM RoomMusic AS music \n" +
                "                    INNER JOIN RoomMusicPlaylist AS musicPlaylist \n" +
                "                    ON music.musicId = musicPlaylist.musicId \n" +
                "                    AND playlist.playlistId = musicPlaylist.playlistId \n" +
                "                    AND music.isHidden = 0 \n" +
                "                    AND scope != 'SharedPlayedList' \n" +
                "                    AND music.coverId IS NOT NULL \n" +
                "                    ORDER BY name ASC \n" +
                "                    LIMIT 1\n" +
                "                )\n" +
                "            ELSE playlist.coverId END\n" +
                "        ) AS coverId,\n" +
                "        (\n" +
                "            SELECT music.localPath FROM RoomMusic AS music \n" +
                "            INNER JOIN RoomMusicPlaylist AS musicPlaylist \n" +
                "            ON music.musicId = musicPlaylist.musicId \n" +
                "            AND playlist.playlistId = musicPlaylist.playlistId \n" +
                "            AND music.isHidden = 0 \n" +
                "            AND scope != 'SharedPlayedList' \n" +
                "            ORDER BY name ASC \n" +
                "            LIMIT 1\n" +
                "        ) AS musicCoverPath,\n" +
                "        (\n" +
                "            SELECT music.coverUrl FROM RoomMusic AS music \n" +
                "            INNER JOIN RoomMusicPlaylist AS musicPlaylist \n" +
                "            ON music.musicId = musicPlaylist.musicId \n" +
                "            AND playlist.playlistId = musicPlaylist.playlistId \n" +
                "            AND music.isHidden = 0 \n" +
                "            AND scope != 'SharedPlayedList' \n" +
                "            ORDER BY name ASC \n" +
                "            LIMIT 1\n" +
                "        ) AS musicCoverUrl,\n" +
                "        playlist.isInQuickAccess, \n" +
                "        playlist.nbPlayed \n" +
                "        FROM RoomPlaylist AS playlist"
        )
    }

    private fun uuid(column: String): String =
        """
        CASE
            WHEN $column IS NULL THEN NULL
            WHEN typeof($column) = 'text' THEN lower($column)
            ELSE lower(
                substr(hex($column), 1, 8) || '-' ||
                substr(hex($column), 9, 4) || '-' ||
                substr(hex($column), 13, 4) || '-' ||
                substr(hex($column), 17, 4) || '-' ||
                substr(hex($column), 21, 12)
            )
        END
        """.trimIndent()

    /*
     * Version 20 stored java.time.LocalDateTime.toString(), created from LocalDateTime.now().
     * Those values represent the device's local wall-clock time and contain no UTC offset.
     * The `utc` modifier converts that local time to UTC before producing the epoch value.
     * `%f` preserves the millisecond part that would otherwise be lost by `%s`.
     */
    private fun localDateTimeToMillis(column: String): String =
        "CAST(strftime('%s', $column, 'utc') AS INTEGER) * 1000 + " +
            "CAST(substr(strftime('%f', $column, 'utc'), 4, 3) AS INTEGER)"
}
