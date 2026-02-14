package com.github.enteraname74.localdb

import androidx.room.*
import com.github.enteraname74.domain.util.LocalDatabaseVersion
import com.github.enteraname74.localdb.converters.Converters
import com.github.enteraname74.localdb.dao.*
import com.github.enteraname74.localdb.model.*


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
    abstract val coverDao: CoverDao
}

// The Room compiler generates the `actual` implementations.
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect class RoomPlatformBuilder {
    fun builder(): RoomDatabase.Builder<AppDatabase>
}