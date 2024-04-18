package com.github.enteraname74.localdesktop

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
import com.github.enteraname74.localdesktop.dao.AlbumArtistDao
import com.github.enteraname74.localdesktop.dao.AlbumDao
import com.github.enteraname74.localdesktop.dao.ArtistDao
import com.github.enteraname74.localdesktop.dao.FolderDao
import com.github.enteraname74.localdesktop.dao.ImageCoverDao
import com.github.enteraname74.localdesktop.dao.MusicAlbumDao
import com.github.enteraname74.localdesktop.dao.MusicArtistDao
import com.github.enteraname74.localdesktop.dao.MusicDao
import com.github.enteraname74.localdesktop.dao.MusicPlaylistDao
import com.github.enteraname74.localdesktop.dao.PlayerMusicDao
import com.github.enteraname74.localdesktop.dao.PlaylistDao
import com.github.enteraname74.localdesktop.daoimpl.ExposedAlbumArtistDaoImpl
import com.github.enteraname74.localdesktop.daoimpl.ExposedAlbumDaoImpl
import com.github.enteraname74.localdesktop.daoimpl.ExposedArtistDaoImpl
import com.github.enteraname74.localdesktop.daoimpl.ExposedFolderDaoImpl
import com.github.enteraname74.localdesktop.daoimpl.ExposedImageCoverDaoImpl
import com.github.enteraname74.localdesktop.daoimpl.ExposedMusicAlbumDaoImpl
import com.github.enteraname74.localdesktop.daoimpl.ExposedMusicArtistDaoImpl
import com.github.enteraname74.localdesktop.daoimpl.ExposedMusicDaoImpl
import com.github.enteraname74.localdesktop.daoimpl.ExposedMusicPlaylistDaoImpl
import com.github.enteraname74.localdesktop.daoimpl.ExposedPlayerMusicDaoImpl
import com.github.enteraname74.localdesktop.daoimpl.ExposedPlaylistDaoImpl
import com.github.enteraname74.localdesktop.datasourceimpl.ExposedAlbumArtistDataSourceImpl
import com.github.enteraname74.localdesktop.datasourceimpl.ExposedAlbumDataSourceImpl
import com.github.enteraname74.localdesktop.datasourceimpl.ExposedArtistDataSourceImpl
import com.github.enteraname74.localdesktop.datasourceimpl.ExposedFolderDataSourceImpl
import com.github.enteraname74.localdesktop.datasourceimpl.ExposedImageCoverDataSourceImpl
import com.github.enteraname74.localdesktop.datasourceimpl.ExposedMusicAlbumDataSourceImpl
import com.github.enteraname74.localdesktop.datasourceimpl.ExposedMusicArtistDataSourceImpl
import com.github.enteraname74.localdesktop.datasourceimpl.ExposedMusicDataSourceImpl
import com.github.enteraname74.localdesktop.datasourceimpl.ExposedMusicPlaylistDataSourceImpl
import com.github.enteraname74.localdesktop.datasourceimpl.ExposedPlayerMusicDataSourceImpl
import com.github.enteraname74.localdesktop.datasourceimpl.ExposedPlaylistDataSourceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val localDesktopModule: Module = module {
    single<AlbumArtistDataSource> { ExposedAlbumArtistDataSourceImpl(get()) }
    single<AlbumDataSource> { ExposedAlbumDataSourceImpl(get()) }
    single<ArtistDataSource> { ExposedArtistDataSourceImpl(get()) }
    single<FolderDataSource> { ExposedFolderDataSourceImpl(get()) }
    single<ImageCoverDataSource> { ExposedImageCoverDataSourceImpl(get()) }
    single<MusicAlbumDataSource> { ExposedMusicAlbumDataSourceImpl(get()) }
    single<MusicArtistDataSource> { ExposedMusicArtistDataSourceImpl(get()) }
    single<MusicDataSource> { ExposedMusicDataSourceImpl(get()) }
    single<MusicPlaylistDataSource> { ExposedMusicPlaylistDataSourceImpl(get()) }
    single<PlayerMusicDataSource> { ExposedPlayerMusicDataSourceImpl(get()) }
    single<PlaylistDataSource> { ExposedPlaylistDataSourceImpl(get()) }

    single<AlbumArtistDao> { ExposedAlbumArtistDaoImpl() }
    single<AlbumDao> { ExposedAlbumDaoImpl() }
    single<ArtistDao> { ExposedArtistDaoImpl() }
    single<FolderDao> { ExposedFolderDaoImpl() }
    single<ImageCoverDao> { ExposedImageCoverDaoImpl() }
    single<MusicAlbumDao> { ExposedMusicAlbumDaoImpl() }
    single<MusicArtistDao> { ExposedMusicArtistDaoImpl() }
    single<MusicDao> { ExposedMusicDaoImpl() }
    single<MusicPlaylistDao> { ExposedMusicPlaylistDaoImpl() }
    single<PlayerMusicDao> { ExposedPlayerMusicDaoImpl() }
    single<PlaylistDao> { ExposedPlaylistDaoImpl() }
}