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
    single<AlbumArtistDataSource> { ExposedAlbumArtistDataSourceImpl() }
    single<AlbumDataSource> { ExposedAlbumDataSourceImpl() }
    single<ArtistDataSource> { ExposedArtistDataSourceImpl() }
    single<FolderDataSource> { ExposedFolderDataSourceImpl() }
    single<ImageCoverDataSource> { ExposedImageCoverDataSourceImpl() }
    single<MusicAlbumDataSource> { ExposedMusicAlbumDataSourceImpl() }
    single<MusicArtistDataSource> { ExposedMusicArtistDataSourceImpl() }
    single<MusicDataSource> { ExposedMusicDataSourceImpl() }
    single<MusicPlaylistDataSource> { ExposedMusicPlaylistDataSourceImpl() }
    single<PlayerMusicDataSource> { ExposedPlayerMusicDataSourceImpl() }
    single<PlaylistDataSource> { ExposedPlaylistDataSourceImpl() }
}