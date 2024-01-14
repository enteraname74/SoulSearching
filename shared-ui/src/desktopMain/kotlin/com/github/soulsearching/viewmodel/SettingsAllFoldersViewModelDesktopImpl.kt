package com.github.soulsearching.viewmodel

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
class SettingsAllFoldersViewModelDesktopImpl(
    folderRepository: FolderRepository,
    musicRepository: MusicRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
) : SettingsAllFoldersViewModel {
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