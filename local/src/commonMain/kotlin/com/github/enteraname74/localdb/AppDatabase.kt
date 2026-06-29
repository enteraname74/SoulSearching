package com.github.enteraname74.localdb

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.github.enteraname74.domain.util.LocalDatabaseVersion
import com.github.enteraname74.localdb.converters.LocalDateTimeConverters
import com.github.enteraname74.localdb.converters.UserTypeConverters
import com.github.enteraname74.localdb.converters.UuidTypeConverters
import com.github.enteraname74.localdb.dao.AlbumDao
import com.github.enteraname74.localdb.dao.ArtistDao
import com.github.enteraname74.localdb.dao.CloudPreferencesDao
import com.github.enteraname74.localdb.dao.CoverDao
import com.github.enteraname74.localdb.dao.FolderDao
import com.github.enteraname74.localdb.dao.MusicArtistDao
import com.github.enteraname74.localdb.dao.MusicDao
import com.github.enteraname74.localdb.dao.MusicPlaylistDao
import com.github.enteraname74.localdb.dao.PlayerMusicDao
import com.github.enteraname74.localdb.dao.PlayerMusicProgressDao
import com.github.enteraname74.localdb.dao.PlayerPlayedListDao
import com.github.enteraname74.localdb.dao.PlaylistDao
import com.github.enteraname74.localdb.dao.UserDao
import com.github.enteraname74.localdb.model.RoomAlbum
import com.github.enteraname74.localdb.model.RoomArtist
import com.github.enteraname74.localdb.model.RoomCloudPreferences
import com.github.enteraname74.localdb.model.RoomFolder
import com.github.enteraname74.localdb.model.RoomMusic
import com.github.enteraname74.localdb.model.RoomMusicArtist
import com.github.enteraname74.localdb.model.RoomMusicPlaylist
import com.github.enteraname74.localdb.model.RoomPlaylist
import com.github.enteraname74.localdb.model.RoomUser
import com.github.enteraname74.localdb.model.player.RoomPlayerMusic
import com.github.enteraname74.localdb.model.player.RoomPlayerMusicProgress
import com.github.enteraname74.localdb.model.player.RoomPlayerPlayedList
import com.github.enteraname74.localdb.view.CurrentPlayerMusicsView
import com.github.enteraname74.localdb.view.RoomAlbumPreview
import com.github.enteraname74.localdb.view.RoomArtistPreview
import com.github.enteraname74.localdb.view.RoomMonthMusicPreview
import com.github.enteraname74.localdb.view.RoomMusicFolderPreview
import com.github.enteraname74.localdb.view.RoomPlaylistPreview


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
        RoomFolder::class,
        RoomUser::class,
        RoomCloudPreferences::class,
    ],
    views = [
        CurrentPlayerMusicsView::class,
        RoomMusicFolderPreview::class,
        RoomMonthMusicPreview::class,
        RoomAlbumPreview::class,
        RoomArtistPreview::class,
        RoomPlaylistPreview::class,
    ]
)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(
    LocalDateTimeConverters::class,
    UserTypeConverters::class,
    UuidTypeConverters::class,
)
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
    abstract val userDao: UserDao
    abstract val cloudPreferencesDao: CloudPreferencesDao
}

// The Room compiler generates the `actual` implementations.
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect class RoomPlatformBuilder {
    fun builder(): RoomDatabase.Builder<AppDatabase>
}