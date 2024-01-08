package com.github.enteraname74.localandroid

import android.content.Context
import androidx.room.Room
import com.github.enteraname74.localandroid.dao.*
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
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "SoulSearching.db"
        ).fallbackToDestructiveMigration().build()
    }

//    @Provides
//    fun provideMusicDao(appDatabase: AppDatabase): MusicDao {
//        return appDatabase.musicDao
//    }
//
//    @Provides
//    fun providePlaylistDao(appDatabase: AppDatabase): PlaylistDao {
//        return appDatabase.playlistDao
//    }
//
//    @Provides
//    fun provideAlbumDao(appDatabase: AppDatabase): AlbumDao {
//        return appDatabase.albumDao
//    }
//
//    @Provides
//    fun provideArtistDao(appDatabase: AppDatabase): ArtistDao {
//        return appDatabase.artistDao
//    }
//
//    @Provides
//    fun provideMusicPlaylistDao(appDatabase: AppDatabase): MusicPlaylistDao {
//        return appDatabase.musicPlaylistDao
//    }
//
//    @Provides
//    fun provideMusicAlbumDao(appDatabase: AppDatabase): MusicAlbumDao {
//        return appDatabase.musicAlbumDao
//    }
//
//    @Provides
//    fun provideMusicArtistDao(appDatabase: AppDatabase): MusicArtistDao {
//        return appDatabase.musicArtistDao
//    }
//
//    @Provides
//    fun provideAlbumArtistDao(appDatabase: AppDatabase): AlbumArtistDao {
//        return appDatabase.albumArtistDao
//    }
//
//    @Provides
//    fun provideImageCoverDao(appDatabase: AppDatabase): ImageCoverDao {
//        return appDatabase.imageCoverDao
//    }
//
//    @Provides
//    fun provideCurrentPlaylistItemDao(appDatabase: AppDatabase): PlayerMusicDao {
//        return appDatabase.playerMusicDao
//    }
//
//    @Provides
//    fun provideFolderDao(appDatabase: AppDatabase): FolderDao {
//        return appDatabase.folderDao
//    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    internal abstract fun bindAlbumArtistDataSource(dataSourceImpl: AlbumArtistDataSourceImpl): AlbumArtistDataSource

    @Binds
    internal abstract fun bindAlbumDataSource(dataSourceImpl: AlbumDataSourceImpl): AlbumDataSource

    @Binds
    internal abstract fun bindArtistDataSource(dataSourceImpl: ArtistDataSourceImpl): ArtistDataSource

    @Binds
    internal abstract fun bindFolderDataSource(dataSourceImpl: FolderDataSourceImpl): FolderDataSource

    @Binds
    internal abstract fun bindImageCoverDataSource(dataSourceImpl: ImageCoverDataSourceImpl): ImageCoverDataSource

    @Binds
    internal abstract fun bindMusicAlbumDataSource(dataSourceImpl: MusicAlbumDataSourceImpl): MusicAlbumDataSource

    @Binds
    internal abstract fun bindMusicArtistDataSource(dataSourceImpl: MusicArtistDataSourceImpl): MusicArtistDataSource

    @Binds
    internal abstract fun bindMusicDataSource(dataSourceImpl: MusicDataSourceImpl): MusicDataSource

    @Binds
    internal abstract fun bindMusicPlaylistDataSource(dataSourceImpl: MusicPlaylistDataSourceImpl): MusicPlaylistDataSource

    @Binds
    internal abstract fun bindPlayerMusicDataSource(dataSourceImpl: PlayerMusicDataSourceImpl): PlayerMusicDataSource

    @Binds
    internal abstract fun bindPlaylistDataSourceImpl(dataSourceImpl: PlaylistDataSourceImpl): PlaylistDataSource
}