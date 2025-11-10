package com.github.enteraname74.localdb

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.enteraname74.localdb.datasourceimpl.*
import com.github.enteraname74.localdb.migration.EndMigrationCallback
import com.github.enteraname74.localdb.migration.Migration16To17
import com.github.enteraname74.localdb.migration.Migration17To18
import com.github.enteraname74.localdb.migration.Migration18To19
import com.github.enteraname74.soulsearching.repository.datasource.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.module

private fun Scope.getAppDatabase(builder: RoomPlatformBuilder, dispatcher: CoroutineDispatcher): AppDatabase {
    return builder
        .builder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(dispatcher)
        .addMigrations(
            Migration16To17(
                coverFileManager = get(),
            ),
            Migration17To18,
            Migration18To19(
                musicMetadataHelper = get(),
            )
        )
        .addCallback(
            EndMigrationCallback(
                settings = get(),
            )
        )
        .build()
}

internal expect val platformModule: Module

val localModule: Module = module {
    includes(platformModule)
    single {
        getAppDatabase(
            builder = get(),
            dispatcher = Dispatchers.IO,
        )
    }

    single<AlbumDataSource> { RoomAlbumDataSourceImpl(get()) }
    single<ArtistDataSource> { RoomArtistDataSourceImpl(get()) }
    single<FolderDataSource> { RoomFolderDataSourceImpl(get()) }
    single<MusicArtistDataSource> { RoomMusicArtistDataSourceImpl(get()) }
    single<MusicDataSource> { RoomMusicDataSourceImpl(get()) }
    single<MusicPlaylistDataSource> { RoomMusicPlaylistDataSourceImpl(get()) }
    single<PlayerMusicDataSource> { RoomPlayerMusicDataSourceImpl(get()) }
    single<PlaylistDataSource> { RoomPlaylistDataSourceImpl(get()) }
}