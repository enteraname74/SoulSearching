package com.github.enteraname74.localdb

import androidx.room.*
import com.github.enteraname74.domain.util.LocalDatabaseVersion
import com.github.enteraname74.localdb.converters.LocalDateTimeConverters
import com.github.enteraname74.localdb.dao.*
import com.github.enteraname74.localdb.model.*
import com.github.enteraname74.localdb.model.player.RoomPlayerMusic
import com.github.enteraname74.localdb.model.player.RoomPlayerMusicProgress
import com.github.enteraname74.localdb.model.player.RoomPlayerPlayedList
import com.github.enteraname74.localdb.view.CurrentPlayerMusicsView


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
        RoomPlayerMusicProgress::class,
        RoomPlayerPlayedList::class,
        RoomFolder::class
    ],
    views = [
        CurrentPlayerMusicsView::class
    ]
)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(LocalDateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val musicDao: MusicDao
    abstract val playlistDao: PlaylistDao
    abstract val albumDao: AlbumDao
    abstract val artistDao: ArtistDao
    abstract val musicPlaylistDao: MusicPlaylistDao
    abstract val musicArtistDao: MusicArtistDao
    abstract val playerMusicDao: PlayerMusicDao
    abstract val playerPlayedListDao: PlayerPlayedListDao
    abstract val folderDao: FolderDao
    abstract val coverDao: CoverDao
    abstract val playerMusicProgressDao: PlayerMusicProgressDao
}

// The Room compiler generates the `actual` implementations.
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect class RoomPlatformBuilder {
    fun builder(): RoomDatabase.Builder<AppDatabase>
}