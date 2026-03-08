package com.github.enteraname74.localdb.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

object Migration19To20 : Migration(19, 20) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomPlayerPlayedList (
                id BLOB NOT NULL PRIMARY KEY,
                playlistId TEXT,
                isMainPlaylist INTEGER NOT NULL,
                mode TEXT NOT NULL,
                state TEXT NOT NULL
            )
            """.trimIndent()
        )

        connection.execSQL("DROP TABLE IF EXISTS RoomPlayerMusic")

        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomPlayerMusic (
                id TEXT NOT NULL PRIMARY KEY,
                musicId BLOB NOT NULL,
                playedListId BLOB NOT NULL,
                `order` REAL NOT NULL,
                shuffledOrder REAL NOT NULL,
                lastPlayedMillis INTEGER,
                FOREIGN KEY(musicId) REFERENCES RoomMusic(musicId) ON DELETE CASCADE,
                FOREIGN KEY(playedListId) REFERENCES RoomPlayerPlayedList(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS index_RoomPlayerMusic_musicId ON RoomPlayerMusic(musicId)"
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS index_RoomPlayerMusic_playedListId ON RoomPlayerMusic(playedListId)"
        )

        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomPlayerMusicProgress (
                id TEXT NOT NULL PRIMARY KEY,
                playedListId BLOB NOT NULL,
                playerMusicId TEXT NOT NULL,
                progress INTEGER NOT NULL
            )
            """.trimIndent()
        )

        connection.execSQL(
            "CREATE VIEW `CurrentPlayerMusicsView` AS WITH currentPlayedList AS (\n" +
                    "        SELECT * FROM RoomPlayerPlayedList  \n" +
                    "        WHERE state != \"Cached\"\n" +
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

        connection.execSQL(
            "CREATE VIEW `RoomMusicFolderPreview` AS SELECT \n" +
                    "                folderMusic.folder,\n" +
                    "                COUNT(*) AS totalMusics, \n" +
                    "                (\n" +
                    "                    SELECT music.coverId FROM RoomMusic AS music \n" +
                    "                    WHERE music.isHidden = 0 \n" +
                    "                    AND music.coverId IS NOT NULL \n" +
                    "                    AND music.folder = folderMusic.folder \n" +
                    "                    ORDER BY\n" +
                    "                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, \n" +
                    "                    name \n" +
                    "                    LIMIT 1\n" +
                    "                ) AS coverId,\n" +
                    "                (\n" +
                    "                    SELECT music.path FROM RoomMusic AS music \n" +
                    "                    WHERE music.isHidden = 0 \n" +
                    "                    AND music.folder = folderMusic.folder \n" +
                    "                    ORDER BY name \n" +
                    "                    LIMIT 1 \n" +
                    "                ) AS musicCoverPath \n" +
                    "            FROM RoomMusic As folderMusic\n" +
                    "            WHERE isHidden = 0 \n" +
                    "            GROUP BY folderMusic.folder"
        )

        connection.execSQL(
            "CREATE VIEW `RoomMonthMusicPreview` AS SELECT \n" +
                    "                strftime('%m/%Y', monthMusic.addedDate) AS month,\n" +
                    "                COUNT(*) AS totalMusics, \n" +
                    "                (\n" +
                    "                    SELECT music.coverId FROM RoomMusic AS music \n" +
                    "                    WHERE music.isHidden = 0 \n" +
                    "                    AND music.coverId IS NOT NULL \n" +
                    "                    AND strftime('%m/%Y', music.addedDate) = strftime('%m/%Y', monthMusic.addedDate)\n" +
                    "                    ORDER BY\n" +
                    "                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, \n" +
                    "                    name \n" +
                    "                    LIMIT 1\n" +
                    "                ) AS coverId,\n" +
                    "                (\n" +
                    "                    SELECT music.path FROM RoomMusic AS music \n" +
                    "                    WHERE music.isHidden = 0 \n" +
                    "                    AND strftime('%m/%Y', music.addedDate) = strftime('%m/%Y', monthMusic.addedDate) \n" +
                    "                    ORDER BY name \n" +
                    "                    LIMIT 1 \n" +
                    "                ) AS musicCoverPath \n" +
                    "            FROM RoomMusic AS monthMusic\n" +
                    "            WHERE isHidden = 0 \n" +
                    "            GROUP BY strftime('%Y-%m', addedDate) \n" +
                    "            ORDER BY strftime('%Y-%m', addedDate) DESC"
        )

        connection.execSQL(
            "CREATE VIEW `RoomAlbumPreview` AS SELECT \n" +
                    "        album.albumId AS id, \n" +
                    "        album.albumName AS name, \n" +
                    "        album.nbPlayed, \n" +
                    "        album.addedDate, \n" +
                    "        album.artistId, \n" +
                    "        (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist, \n" +
                    "        (\n" +
                    "            CASE WHEN album.coverId IS NULL THEN \n" +
                    "                (\n" +
                    "                    SELECT music.coverId FROM RoomMusic AS music \n" +
                    "                    WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY \n" +
                    "                    CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, \n" +
                    "                    music.albumPosition, \n" +
                    "                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, \n" +
                    "                    music.name \n" +
                    "                )\n" +
                    "            ELSE album.coverId END\n" +
                    "        ) AS coverId,\n" +
                    "        (\n" +
                    "            SELECT music.path FROM RoomMusic AS music \n" +
                    "            WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY \n" +
                    "            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, \n" +
                    "            music.albumPosition, \n" +
                    "            music.name \n" +
                    "            LIMIT 1 \n" +
                    "        ) AS musicCoverPath,\n" +
                    "        album.isInQuickAccess \n" +
                    "        FROM RoomAlbum AS album"
        )

        connection.execSQL(
            "CREATE VIEW `RoomArtistPreview` AS SELECT \n" +
                    "        artist.artistId AS id, \n" +
                    "        artist.artistName AS name, \n" +
                    "        artist.coverFolderKey,\n" +
                    "        artist.addedDate, \n" +
                    "        artist.nbPlayed,\n" +
                    "        (SELECT COUNT(*) FROM RoomMusicArtist AS musicArtist WHERE musicArtist.artistId = artist.artistId) AS totalMusics, \n" +
                    "        (\n" +
                    "            CASE WHEN artist.coverId IS NULL THEN \n" +
                    "                (\n" +
                    "                    SELECT music.coverId FROM RoomMusic AS music \n" +
                    "                    INNER JOIN RoomMusicArtist AS musicArtist \n" +
                    "                    ON music.musicId = musicArtist.musicId \n" +
                    "                    AND artist.artistId = musicArtist.artistId \n" +
                    "                    AND music.isHidden = 0 \n" +
                    "                    AND music.coverId IS NOT NULL \n" +
                    "                    ORDER BY name ASC \n" +
                    "                    LIMIT 1\n" +
                    "                )\n" +
                    "            ELSE artist.coverId END\n" +
                    "        ) AS coverId,\n" +
                    "        (\n" +
                    "            SELECT music.path FROM RoomMusic AS music \n" +
                    "            INNER JOIN RoomMusicArtist AS musicArtist \n" +
                    "            ON music.musicId = musicArtist.musicId \n" +
                    "            AND artist.artistId = musicArtist.artistId \n" +
                    "            AND music.isHidden = 0 \n" +
                    "            ORDER BY name ASC \n" +
                    "            LIMIT 1\n" +
                    "        ) AS musicCoverPath,\n" +
                    "        artist.isInQuickAccess \n" +
                    "        FROM RoomArtist AS artist"
        )

        connection.execSQL(
            "CREATE VIEW `RoomPlaylistPreview` AS SELECT playlist.playlistId AS id, \n" +
                    "        playlist.name, \n" +
                    "        playlist.isFavorite, \n" +
                    "        playlist.addedDate, \n" +
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
                    "                    AND music.coverId IS NOT NULL \n" +
                    "                    LIMIT 1\n" +
                    "                )\n" +
                    "            ELSE playlist.coverId END\n" +
                    "        ) AS coverId,\n" +
                    "        (\n" +
                    "            SELECT music.path FROM RoomMusic AS music \n" +
                    "            INNER JOIN RoomMusicPlaylist AS musicPlaylist \n" +
                    "            ON music.musicId = musicPlaylist.musicId \n" +
                    "            AND playlist.playlistId = musicPlaylist.playlistId \n" +
                    "            AND music.isHidden = 0 \n" +
                    "            LIMIT 1\n" +
                    "        ) AS musicCoverPath,\n" +
                    "        playlist.isInQuickAccess, \n" +
                    "        playlist.nbPlayed \n" +
                    "        FROM RoomPlaylist AS playlist"
        )
    }
}