package com.github.enteraname74.localandroid

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
import com.github.enteraname74.localandroid.datasourceimpl.AlbumArtistDataSourceImpl
import com.github.enteraname74.localandroid.datasourceimpl.AlbumDataSourceImpl
import com.github.enteraname74.localandroid.datasourceimpl.ArtistDataSourceImpl
import com.github.enteraname74.localandroid.datasourceimpl.FolderDataSourceImpl
import com.github.enteraname74.localandroid.datasourceimpl.ImageCoverDataSourceImpl
import com.github.enteraname74.localandroid.datasourceimpl.MusicAlbumDataSourceImpl
import com.github.enteraname74.localandroid.datasourceimpl.MusicArtistDataSourceImpl
import com.github.enteraname74.localandroid.datasourceimpl.MusicDataSourceImpl
import com.github.enteraname74.localandroid.datasourceimpl.MusicPlaylistDataSourceImpl
import com.github.enteraname74.localandroid.datasourceimpl.PlayerMusicDataSourceImpl
import com.github.enteraname74.localandroid.datasourceimpl.PlaylistDataSourceImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//internal class AppModule {
//    @Provides
//    @Singleton
//    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
//        return Room.databaseBuilder(
//            appContext,
//            AppDatabase::class.java,
//            "SoulSearching.db"
//        ).fallbackToDestructiveMigration().build()
//    }
//}

/**
 * Module for Room.
 */
val roomModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "SoulSearching.db"
        ).fallbackToDestructiveMigration().build()
    }

    single<AlbumArtistDataSource> { AlbumArtistDataSourceImpl(get()) }
    single<AlbumDataSource> { AlbumDataSourceImpl(get()) }
    single<ArtistDataSource> { ArtistDataSourceImpl(get()) }
    single<FolderDataSource> { FolderDataSourceImpl(get()) }
    single<ImageCoverDataSource> { ImageCoverDataSourceImpl(get()) }
    single<MusicAlbumDataSource> { MusicAlbumDataSourceImpl(get()) }
    single<MusicArtistDataSource> { MusicArtistDataSourceImpl(get()) }
    single<MusicDataSource> { MusicDataSourceImpl(get()) }
    single<MusicPlaylistDataSource> { MusicPlaylistDataSourceImpl(get()) }
    single<PlayerMusicDataSource> { PlayerMusicDataSourceImpl(get()) }
    single<PlaylistDataSource> { PlaylistDataSourceImpl(get()) }
}

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class DataSourceModule {
//    @Binds
//    internal abstract fun bindAlbumArtistDataSource(dataSourceImpl: AlbumArtistDataSourceImpl): AlbumArtistDataSource
//
//    @Binds
//    internal abstract fun bindAlbumDataSource(dataSourceImpl: AlbumDataSourceImpl): AlbumDataSource
//
//    @Binds
//    internal abstract fun bindArtistDataSource(dataSourceImpl: ArtistDataSourceImpl): ArtistDataSource
//
//    @Binds
//    internal abstract fun bindFolderDataSource(dataSourceImpl: FolderDataSourceImpl): FolderDataSource
//
//    @Binds
//    internal abstract fun bindImageCoverDataSource(dataSourceImpl: ImageCoverDataSourceImpl): ImageCoverDataSource
//
//    @Binds
//    internal abstract fun bindMusicAlbumDataSource(dataSourceImpl: MusicAlbumDataSourceImpl): MusicAlbumDataSource
//
//    @Binds
//    internal abstract fun bindMusicArtistDataSource(dataSourceImpl: MusicArtistDataSourceImpl): MusicArtistDataSource
//
//    @Binds
//    internal abstract fun bindMusicDataSource(dataSourceImpl: MusicDataSourceImpl): MusicDataSource
//
//    @Binds
//    internal abstract fun bindMusicPlaylistDataSource(dataSourceImpl: MusicPlaylistDataSourceImpl): MusicPlaylistDataSource
//
//    @Binds
//    internal abstract fun bindPlayerMusicDataSource(dataSourceImpl: PlayerMusicDataSourceImpl): PlayerMusicDataSource
//
//    @Binds
//    internal abstract fun bindPlaylistDataSourceImpl(dataSourceImpl: PlaylistDataSourceImpl): PlaylistDataSource
//}