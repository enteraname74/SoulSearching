package com.github.enteraname74.soulsearching.localdesktop

import com.github.enteraname74.soulsearching.localdesktop.tables.AlbumArtistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.AlbumTable
import com.github.enteraname74.soulsearching.localdesktop.tables.ArtistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.FolderTable
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicAlbumTable
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicArtistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicPlaylistTable
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable
import com.github.enteraname74.soulsearching.localdesktop.tables.PlayerMusicTable
import com.github.enteraname74.soulsearching.localdesktop.tables.PlaylistTable
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
        Database.connect("jdbc:sqlite:soulSearchingDatabase.db?foreign_keys=on", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                AlbumArtistTable,
                AlbumTable,
                ArtistTable,
                FolderTable,
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