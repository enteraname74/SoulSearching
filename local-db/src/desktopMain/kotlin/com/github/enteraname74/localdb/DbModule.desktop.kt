package com.github.enteraname74.localdb

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
import com.github.enteraname74.localdb.datasourceimpl.ExposedAlbumArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.ExposedAlbumDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.ExposedArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.ExposedFolderDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.ExposedImageCoverDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.ExposedMusicAlbumDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.ExposedMusicArtistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.ExposedMusicDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.ExposedMusicPlaylistDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.ExposedPlayerMusicDataSourceImpl
import com.github.enteraname74.localdb.datasourceimpl.ExposedPlaylistDataSourceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual val localDbModule: Module = module {
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