package com.github.enteraname74.localdb

import androidx.room.Room
import com.github.enteraname74.domain.datasource.AlbumArtistDataSource
import com.github.enteraname74.domain.datasource.AlbumDataSource
import com.github.enteraname74.domain.datasource.ArtistDataSource
import com.github.enteraname74.domain.datasource.FolderDataSource
import com.github.enteraname74.domain.datasource.ImageCoverDataSource
import com.github.enteraname74.domain.datasource.MusicAlbumDataSource
import com.github.enteraname74.domain.datasource.MusicArtistDataSource
import com.github.enteraname74.domain.datasource.MusicDataSource
import com.github.enteraname74.domain.datasource.MusicPlaylistDataSource
import com.github.enteraname74.domain.datasource.PlayerMusicDataSource
import com.github.enteraname74.domain.datasource.PlaylistDataSource
import com.github.enteraname74.localdb.datasourceimpl.RoomAlbumArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomAlbumDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomFolderDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomImageCoverDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicAlbumDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicPlaylistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomPlayerMusicDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomPlaylistDataSourceImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

val localAndroidModule: Module = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "SoulSearching.db"
        ).fallbackToDestructiveMigration().build()
    }

    single<AlbumArtistDataSource> { RoomAlbumArtistDataSourceImpl(get()) }
    single<AlbumDataSource> { RoomAlbumDataSourceImpl(get()) }
    single<ArtistDataSource> { RoomArtistDataSourceImpl(get()) }
    single<FolderDataSource> { RoomFolderDataSourceImpl(get()) }
    single<ImageCoverDataSource> { RoomImageCoverDataSourceImpl(get()) }
    single<MusicAlbumDataSource> { RoomMusicAlbumDataSourceImpl(get()) }
    single<MusicArtistDataSource> { RoomMusicArtistDataSourceImpl(get()) }
    single<MusicDataSource> { RoomMusicDataSourceImpl(get()) }
    single<MusicPlaylistDataSource> { RoomMusicPlaylistDataSourceImpl(get()) }
    single<PlayerMusicDataSource> { RoomPlayerMusicDataSourceImpl(get()) }
    single<PlaylistDataSource> { RoomPlaylistDataSourceImpl(get()) }
}