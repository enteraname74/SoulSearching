package com.github.enteraname74.localdb

import com.github.enteraname74.localdb.datasourceimpl.RoomAlbumDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomCloudPreferencesDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomCoverLocalDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomDeviceLocalDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomFolderDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicLocalDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomMusicPlaylistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomPlayerLocalDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomPlaylistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomUserInscriptionCodeLocalDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.RoomUserLocalDataSourceImpl
import com.github.enteraname74.localdb.migration.EndMigrationCallback
import com.github.enteraname74.localdb.migration.Migration16To17
import com.github.enteraname74.localdb.migration.Migration17To18
import com.github.enteraname74.localdb.migration.Migration18To19
import com.github.enteraname74.localdb.migration.Migration19To20
import com.github.enteraname74.localdb.migration.Migration20To21
import com.github.enteraname74.soulsearching.repository.datasource.AlbumDataSource
import com.github.enteraname74.soulsearching.repository.datasource.ArtistDataSource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.DeviceLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.cover.CoverLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.FolderDataSource
import com.github.enteraname74.soulsearching.repository.datasource.MusicArtistDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.MusicPlaylistDataSource
import com.github.enteraname74.soulsearching.repository.datasource.player.PlayerLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.PlaylistDataSource
import com.github.enteraname74.soulsearching.repository.datasource.code.UserInscriptionCodeLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
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
        .setDriver(localDatabaseDriver())
        .setQueryCoroutineContext(dispatcher)
        .addMigrations(
            Migration16To17(
                coverFileManager = get(),
            ),
            Migration17To18,
            Migration18To19(
                musicMetadataHelper = get(),
            ),
            Migration19To20,
            Migration20To21,
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
            dispatcher = localDatabaseDispatcher,
        )
    }

    singleOf(::RoomAlbumDataSourceImpl) bind AlbumDataSource::class
    singleOf(::RoomArtistDataSourceImpl) bind ArtistDataSource::class
    singleOf(::RoomFolderDataSourceImpl) bind FolderDataSource::class
    singleOf(::RoomMusicArtistDataSourceImpl) bind MusicArtistDataSource::class
    singleOf(::RoomMusicLocalDataSourceImpl) bind MusicLocalDataSource::class
    singleOf(::RoomMusicPlaylistDataSourceImpl) bind MusicPlaylistDataSource::class
    singleOf(::RoomPlayerLocalDataSourceImpl) bind PlayerLocalDataSource::class
    singleOf(::RoomPlaylistDataSourceImpl) bind PlaylistDataSource::class
    singleOf(::RoomCoverLocalDataSourceImpl) bind CoverLocalDataSource::class
    singleOf(::RoomUserLocalDataSourceImpl) bind UserLocalDataSource::class
    singleOf(::RoomCloudPreferencesDataSourceImpl) bind CloudPreferencesDataSource::class
    singleOf(::RoomDeviceLocalDataSourceImpl) bind DeviceLocalDataSource::class
    singleOf(::RoomUserInscriptionCodeLocalDataSourceImpl) bind UserInscriptionCodeLocalDataSource::class
}
