package com.github.enteraname74.localdb

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.enteraname74.domain.util.LocalDatabaseVersion
import com.github.enteraname74.localdb.converters.Converters
import com.github.enteraname74.localdb.dao.*
import com.github.enteraname74.localdb.migration.EndMigrationCallback
import com.github.enteraname74.localdb.migration.Migration16To17
import com.github.enteraname74.localdb.migration.Migration17To18
import com.github.enteraname74.localdb.migration.Migration18To19
import com.github.enteraname74.localdb.model.*
import kotlinx.coroutines.CoroutineDispatcher


@Database(
    version = LocalDatabaseVersion.VERSION,
    entities = [
        RoomMusic::class,
        RoomAlbum::class,
        RoomArtist::class,
        RoomPlaylist::class,
        RoomMusicPlaylist::class,
        RoomMusicArtist::class,
        RoomPlayerMusic::class,
        RoomFolder::class
    ]
)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val musicDao: MusicDao
    abstract val playlistDao: PlaylistDao
    abstract val albumDao: AlbumDao
    abstract val artistDao: ArtistDao
    abstract val musicPlaylistDao: MusicPlaylistDao
    abstract val musicArtistDao: MusicArtistDao
    abstract val playerMusicDao: PlayerMusicDao
    abstract val folderDao: FolderDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect class RoomPlatformBuilder {
    fun builder(): RoomDatabase.Builder<AppDatabase>
}