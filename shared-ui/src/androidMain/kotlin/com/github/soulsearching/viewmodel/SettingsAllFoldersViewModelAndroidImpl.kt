package com.github.soulsearching.viewmodel

import androidx.lifecycle.ViewModel
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.viewmodel.handler.SettingsAllFolderViewModelHandler

/**
 * Implementation of the SettingsAllFoldersViewModel.
 */
class SettingsAllFoldersViewModelAndroidImpl(
    folderRepository: FolderRepository,
    musicRepository: MusicRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
) : SettingsAllFoldersViewModel, ViewModel() {
    override val handler: SettingsAllFolderViewModelHandler = SettingsAllFolderViewModelHandler(
        folderRepository = folderRepository,
        musicRepository = musicRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository,
        albumArtistRepository = albumArtistRepository,
        musicAlbumRepository = musicAlbumRepository,
        musicArtistRepository = musicArtistRepository
    )
}