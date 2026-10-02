package com.github.enteraname74.localdb.migration

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.async.executeSQL

object Migration21To22 : Migration(21, 22) {
    override suspend fun migrate(connection: SQLiteConnection) {
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
        connection.executeSQL(
            """
            CREATE TABLE IF NOT EXISTS RoomCollectionPlaylist (
                collectionId TEXT NOT NULL,
                playlistId TEXT NOT NULL,
                PRIMARY KEY(collectionId, playlistId),
                FOREIGN KEY(collectionId) REFERENCES RoomCollection(collectionId) ON DELETE CASCADE,
                FOREIGN KEY(playlistId) REFERENCES RoomPlaylist(playlistId) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        connection.executeSQL("CREATE INDEX IF NOT EXISTS index_RoomCollectionAlbum_albumId ON RoomCollectionAlbum(albumId)")
        connection.executeSQL("CREATE INDEX IF NOT EXISTS index_RoomCollectionArtist_artistId ON RoomCollectionArtist(artistId)")
        connection.executeSQL("CREATE INDEX IF NOT EXISTS index_RoomCollectionPlaylist_playlistId ON RoomCollectionPlaylist(playlistId)")

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

        connection.executeSQL(
            """
            CREATE VIEW RoomCollectionPreview AS
            SELECT
                collection.collectionId AS id,
                collection.remoteId,
                collection.name,
                collection.addedDate,
                collection.nbPlayed,
                collection.isInQuickAccess,
                (
                    SELECT COUNT(*)
                    FROM RoomMusic AS music
                    WHERE music.isHidden = 0
                      AND music.scope != 'SharedPlayedList'
                      AND (
                          EXISTS (
                              SELECT 1
                              FROM RoomCollectionAlbum AS collectionAlbum
                              WHERE collectionAlbum.collectionId = collection.collectionId
                                AND collectionAlbum.albumId = music.albumId
                          )
                          OR EXISTS (
                              SELECT 1
                              FROM RoomCollectionArtist AS collectionArtist
                              INNER JOIN RoomMusicArtist AS musicArtist
                                  ON musicArtist.artistId = collectionArtist.artistId
                              WHERE collectionArtist.collectionId = collection.collectionId
                                AND musicArtist.musicId = music.musicId
                          )
                          OR EXISTS (
                              SELECT 1
                              FROM RoomCollectionPlaylist AS collectionPlaylist
                              INNER JOIN RoomMusicPlaylist AS musicPlaylist
                                  ON musicPlaylist.playlistId = collectionPlaylist.playlistId
                              WHERE collectionPlaylist.collectionId = collection.collectionId
                                AND musicPlaylist.musicId = music.musicId
                          )
                      )
                ) AS totalMusics
            FROM RoomCollection AS collection
            """.trimIndent()
        )
    }
}
