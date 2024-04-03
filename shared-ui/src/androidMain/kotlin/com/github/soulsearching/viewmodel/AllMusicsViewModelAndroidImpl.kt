package com.github.soulsearching.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.classes.utils.MusicFetcherAndroidImpl
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.playback.PlaybackManagerAndroidImpl
import com.github.soulsearching.viewmodel.handler.AllMusicsViewModelAndroidHandler
import com.github.soulsearching.viewmodel.handler.AllMusicsViewModelHandler

/**
 * Implementation of the AllMusicsViewModel.
 */
class AllMusicsViewModelAndroidImpl(
    context: Context,
    musicRepository: MusicRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    folderRepository: FolderRepository,
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManagerAndroidImpl,
    musicFetcher: MusicFetcherAndroidImpl
) : AllMusicsViewModel {
    override val handler: AllMusicsViewModelHandler = AllMusicsViewModelAndroidHandler(
        context = context,
        coroutineScope = screenModelScope,
        musicRepository = musicRepository,
        playlistRepository = playlistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository,
        musicAlbumRepository = musicAlbumRepository,
        musicArtistRepository = musicArtistRepository,
        albumArtistRepository = albumArtistRepository,
        imageCoverRepository = imageCoverRepository,
        folderRepository = folderRepository,
        settings = settings,
        playbackManager = playbackManager,
        musicFetcher = musicFetcher
    )
}