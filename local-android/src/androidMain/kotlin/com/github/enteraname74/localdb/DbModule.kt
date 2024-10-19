package com.github.enteraname74.localdb

import androidx.room.Room
import com.github.enteraname74.localdb.datasourceimpl.RoomAlbumArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomAlbumDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomFolderDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicAlbumDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicPlaylistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomPlayerMusicDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomPlaylistDataSourceImpl
import com.github.enteraname74.localdb.migration.Migration16To17
import com.github.enteraname74.soulsearching.repository.datasource.*
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

val localAndroidModule: Module = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "SoulSearching.db"
        )
            .addMigrations(
                Migration16To17(
                    context = androidApplication(),
                    coverFileManager = get(),
                )
            )
            .build()
    }

    single<AlbumArtistDataSource> { RoomAlbumArtistDataSourceImpl(get()) }
    single<AlbumDataSource> { RoomAlbumDataSourceImpl(get()) }
    single<ArtistDataSource> { RoomArtistDataSourceImpl(get()) }
    single<FolderDataSource> { RoomFolderDataSourceImpl(get()) }
    single<MusicAlbumDataSource> { RoomMusicAlbumDataSourceImpl(get()) }
    single<MusicArtistDataSource> { RoomMusicArtistDataSourceImpl(get()) }
    single<MusicDataSource> { RoomMusicDataSourceImpl(get()) }
    single<MusicPlaylistDataSource> { RoomMusicPlaylistDataSourceImpl(get()) }
    single<PlayerMusicDataSource> { RoomPlayerMusicDataSourceImpl(get()) }
    single<PlaylistDataSource> { RoomPlaylistDataSourceImpl(get()) }
}