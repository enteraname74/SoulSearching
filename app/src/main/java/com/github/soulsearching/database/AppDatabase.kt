package com.github.soulsearching.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.soulsearching.database.converters.Converters
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.*


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
        CurrentPlaylistItem::class
    ],
    version = 12
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val musicDao: MusicDao
    abstract val playlistDao: PlaylistDao
    abstract val albumDao: AlbumDao
    abstract val artistDao: ArtistDao
    abstract val musicPlaylistDao: MusicPlaylistDao
    abstract val musicAlbumDao: MusicAlbumDao
    abstract val musicArtistDao: MusicArtistDao
    abstract val albumArtistDao: AlbumArtistDao
    abstract val imageCoverDao: ImageCoverDao
    abstract val currentPlaylistItemDao: CurrentPlaylistItemDao
}