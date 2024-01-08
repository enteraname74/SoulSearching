package com.github.enteraname74.localandroid

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.enteraname74.localandroid.converters.Converters
import com.github.enteraname74.localandroid.dao.*
import com.github.enteraname74.localandroid.model.*


@Database(
    entities = [
        RoomMusic::class,
        RoomAlbum::class,
        RoomArtist::class,
        RoomPlaylist::class,
        RoomMusicPlaylist::class,
        RoomMusicAlbum::class,
        RoomMusicArtist::class,
        RoomAlbumArtist::class,
        RoomImageCover::class,
        RoomPlayerMusic::class,
        RoomFolder::class
    ],
    version = 16
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
    abstract val imageCoverDao: ImageCoverDao
    abstract val playerMusicDao: PlayerMusicDao
    abstract val folderDao: FolderDao
}