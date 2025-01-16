package com.github.enteraname74.soulsearching.localdesktop

import com.github.enteraname74.domain.util.AppEnvironment
import com.github.enteraname74.soulsearching.localdesktop.migration.ExposedMigrationHandler
import com.github.enteraname74.soulsearching.localdesktop.migration.impl.Migration16To17
import com.github.enteraname74.soulsearching.localdesktop.migration.impl.Migration18To19
import com.github.enteraname74.soulsearching.localdesktop.tables.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Database of the application.
 */
object AppDatabase {

    private val migrationHandler = ExposedMigrationHandler(
        Migration16To17(),
        Migration18To19,
    )

    /**
     * Establish a connection with the database.
     */
    fun connectToDatabase() {
        val databaseName = if (AppEnvironment.IS_IN_DEVELOPMENT) {
            "soulSearchingDevDatabase.db"
        } else {
            "soulSearchingDatabase.db"
        }
        Database.connect("jdbc:sqlite:$databaseName?foreign_keys=on", "org.sqlite.JDBC")
        migrationHandler.doMigrations()
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

suspend fun <T> dbQuery(block: suspend Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }