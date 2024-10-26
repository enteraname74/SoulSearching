package com.github.enteraname74.localdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
        RoomMusicAlbum::class,
        RoomMusicArtist::class,
        RoomAlbumArtist::class,
        RoomPlayerMusic::class,
        RoomFolder::class
    ]
)
@TypeConverters(Converters::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract val musicDao: MusicDao
    abstract val playlistDao: PlaylistDao
    abstract val albumDao: AlbumDao
    abstract val artistDao: ArtistDao
    abstract val musicPlaylistDao: MusicPlaylistDao
    abstract val musicAlbumDao: MusicAlbumDao
    abstract val musicArtistDao: MusicArtistDao
    abstract val albumArtistDao: AlbumArtistDao
    abstract val playerMusicDao: PlayerMusicDao
    abstract val folderDao: FolderDao
}