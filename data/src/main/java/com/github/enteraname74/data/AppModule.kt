package com.github.enteraname74.data

import android.content.Context
import androidx.room.Room
import com.github.enteraname74.data.dao.*
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

    @Provides
    fun provideMusicDao(appDatabase: AppDatabase): MusicDao {
        return appDatabase.musicDao
    }

    @Provides
    fun providePlaylistDao(appDatabase: AppDatabase): PlaylistDao {
        return appDatabase.playlistDao
    }

    @Provides
    fun provideAlbumDao(appDatabase: AppDatabase): AlbumDao {
        return appDatabase.albumDao
    }

    @Provides
    fun provideArtistDao(appDatabase: AppDatabase): ArtistDao {
        return appDatabase.artistDao
    }

    @Provides
    fun provideMusicPlaylistDao(appDatabase: AppDatabase): MusicPlaylistDao {
        return appDatabase.musicPlaylistDao
    }

    @Provides
    fun provideMusicAlbumDao(appDatabase: AppDatabase): MusicAlbumDao {
        return appDatabase.musicAlbumDao
    }

    @Provides
    fun provideMusicArtistDao(appDatabase: AppDatabase): MusicArtistDao {
        return appDatabase.musicArtistDao
    }

    @Provides
    fun provideAlbumArtistDao(appDatabase: AppDatabase): AlbumArtistDao {
        return appDatabase.albumArtistDao
    }

    @Provides
    fun provideImageCoverDao(appDatabase: AppDatabase): ImageCoverDao {
        return appDatabase.imageCoverDao
    }

    @Provides
    fun provideCurrentPlaylistItemDao(appDatabase: AppDatabase): PlayerMusicDao {
        return appDatabase.playerMusicDao
    }

    @Provides
    fun provideFolderDao(appDatabase: AppDatabase): FolderDao {
        return appDatabase.folderDao
    }
}