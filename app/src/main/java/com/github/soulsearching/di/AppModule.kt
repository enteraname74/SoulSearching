package com.github.soulsearching.di

import android.content.Context
import androidx.room.Room
import com.github.soulsearching.database.AppDatabase
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.dao.MusicPlaylistDao
import com.github.soulsearching.database.dao.PlaylistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
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
    fun provideMusicDao(appDatabase: AppDatabase) : MusicDao {
        return appDatabase.musicDao
    }

    @Provides
    fun providePlaylistDao(appDatabase: AppDatabase) : PlaylistDao {
        return appDatabase.playlistDao
    }

    @Provides
    fun provideMusicPlaylistDao(appDatabase: AppDatabase) : MusicPlaylistDao {
        return appDatabase.musicPlaylistDao
    }
}