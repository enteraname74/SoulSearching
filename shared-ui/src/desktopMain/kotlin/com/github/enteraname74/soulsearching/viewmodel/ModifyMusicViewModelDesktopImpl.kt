package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.model.PlaybackManagerDesktopImpl
import com.github.soulsearching.modifyelement.modifymusic.domain.ModifyMusicViewModelHandler

/**
 * Implementation of the ModifyMusicViewModel.
 */
class ModifyMusicViewModelDesktopImpl(
    musicRepository: MusicRepository,
    artistRepository: ArtistRepository,
    albumRepository: AlbumRepository,
    imageCoverRepository: ImageCoverRepository,
    playbackManager: PlaybackManagerDesktopImpl
) : ModifyMusicViewModel {
    override val handler: ModifyMusicViewModelHandler = ModifyMusicViewModelHandler(
        coroutineScope = screenModelScope,
        musicRepository = musicRepository,
        imageCoverRepository = imageCoverRepository,
        playbackManager = playbackManager,
        albumRepository = albumRepository,
        artistRepository = artistRepository
    )
}