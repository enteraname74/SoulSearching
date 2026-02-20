package com.github.enteraname74.localdb

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.enteraname74.localdb.datasourceimpl.RoomAlbumDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomCoverDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomFolderDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicPlaylistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomPlayerDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomPlaylistDataSourceImpl
import com.github.enteraname74.localdb.migration.EndMigrationCallback
import com.github.enteraname74.localdb.migration.Migration16To17
import com.github.enteraname74.localdb.migration.Migration17To18
import com.github.enteraname74.localdb.migration.Migration18To19
import com.github.enteraname74.soulsearching.repository.datasource.AlbumDataSource
import com.github.enteraname74.soulsearching.repository.datasource.ArtistDataSource
import com.github.enteraname74.soulsearching.repository.datasource.CoverDataSource
import com.github.enteraname74.soulsearching.repository.datasource.FolderDataSource
import com.github.enteraname74.soulsearching.repository.datasource.MusicArtistDataSource
import com.github.enteraname74.soulsearching.repository.datasource.MusicDataSource
import com.github.enteraname74.soulsearching.repository.datasource.MusicPlaylistDataSource
import com.github.enteraname74.soulsearching.repository.datasource.PlayerDataSource
import com.github.enteraname74.soulsearching.repository.datasource.PlaylistDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module

private fun Scope.getAppDatabase(
    builder: RoomPlatformBuilder,
    dispatcher: CoroutineDispatcher
): AppDatabase {
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
            ),
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

    singleOf(::RoomAlbumDataSourceImpl) bind AlbumDataSource::class
    singleOf(::RoomArtistDataSourceImpl) bind ArtistDataSource::class
    singleOf(::RoomFolderDataSourceImpl) bind FolderDataSource::class
    singleOf(::RoomMusicArtistDataSourceImpl) bind MusicArtistDataSource::class
    singleOf(::RoomMusicDataSourceImpl) bind MusicDataSource::class
    singleOf(::RoomMusicPlaylistDataSourceImpl) bind MusicPlaylistDataSource::class
    singleOf(::RoomPlayerDataSourceImpl) bind PlayerDataSource::class
    singleOf(::RoomPlaylistDataSourceImpl) bind PlaylistDataSource::class
    singleOf(::RoomCoverDataSourceImpl) bind CoverDataSource::class
}