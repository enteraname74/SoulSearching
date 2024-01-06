package com.github.enteraname74.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.enteraname74.data.converters.Converters
import com.github.enteraname74.data.dao.*
import com.github.enteraname74.data.model.*


@Database(
    entities = [
        Music::class,
        Album::class,
        Artist::class,
        Playlist::class,
        MusicPlaylist::class,
        MusicAlbum::class,
        MusicArtist::class,
        AlbumArtist::class,
        ImageCover::class,
        PlayerMusic::class,
        Folder::class
    ],
    version = 14
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