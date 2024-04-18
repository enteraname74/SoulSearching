package com.github.enteraname74.localdesktop

import com.github.enteraname74.localdesktop.tables.AlbumArtistTable
import com.github.enteraname74.localdesktop.tables.AlbumTable
import com.github.enteraname74.localdesktop.tables.ArtistTable
import com.github.enteraname74.localdesktop.tables.FolderTable
import com.github.enteraname74.localdesktop.tables.ImageCoverTable
import com.github.enteraname74.localdesktop.tables.MusicAlbumTable
import com.github.enteraname74.localdesktop.tables.MusicArtistTable
import com.github.enteraname74.localdesktop.tables.MusicPlaylistTable
import com.github.enteraname74.localdesktop.tables.MusicTable
import com.github.enteraname74.localdesktop.tables.PlayerMusicTable
import com.github.enteraname74.localdesktop.tables.PlaylistTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Database of the application.
 */
object AppDatabase {

    /**
     * Establish a connection with the database.
     */
    fun connectToDatabase() {
        Database.connect("jdbc:sqlite:database.db", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(
                AlbumArtistTable,
                AlbumTable,
                ArtistTable,
                FolderTable,
                ImageCoverTable,
                MusicAlbumTable,
                MusicArtistTable,
                MusicPlaylistTable,
                MusicTable,
                PlayerMusicTable,
                PlaylistTable
            )
        }
    }
}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }