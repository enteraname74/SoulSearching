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
import java.io.File

/**
 * Database of the application.
 */
object AppDatabase {

    private val migrationHandler = ExposedMigrationHandler(
        Migration16To17(),
        Migration18To19,
    )

    private val SUFFIX = if (AppEnvironment.IS_IN_DEVELOPMENT) {
        "_dev"
    } else {
        ""
    }
    private val DATABASE_FOLDER: String = ".soul_searching$SUFFIX/"

    /**
     * Establish a connection with the database.
     */
    fun connectToDatabase() {
        val userHome = System.getProperty("user.home") ?: ""
        val userFolder = File(userHome)
        val appFolder = File(userFolder, DATABASE_FOLDER)
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }
        val appDbFile = File(appFolder, "app_db.db")

        Database.connect("jdbc:sqlite:${appDbFile.absolutePath}?foreign_keys=on", "org.sqlite.JDBC")
        migrationHandler.doMigrations()
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                AlbumTable,
                ArtistTable,
                FolderTable,
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