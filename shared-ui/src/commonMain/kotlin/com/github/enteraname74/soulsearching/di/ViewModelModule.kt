package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain.SelectedAlbumViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain.SelectedArtistViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain.SelectedFolderViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain.SelectedMonthViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain.SelectedPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.*
import com.github.enteraname74.soulsearching.feature.coversprovider.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.domain.ModifyAlbumViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.domain.ModifyArtistViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.domain.ModifyMusicViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.domain.ModifyPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.SettingsAddMusicsViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.SettingsAllFoldersViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val viewModelModule: Module = module {
    // Settings
    singleOf(::SettingsAddMusicsViewModel)
    singleOf(::SettingsAllFoldersViewModel)

    // Main page
    singleOf(::AllMusicsViewModel)
    singleOf(::AllAlbumsViewModel)
    singleOf(::AllArtistsViewModel)
    singleOf(::AllPlaylistsViewModel)
    singleOf(::AllQuickAccessViewModel)
    singleOf(::AllImageCoversViewModel)
    singleOf(::MainActivityViewModel)

    // Modify elements
    singleOf(::ModifyAlbumViewModel)
    singleOf(::ModifyMusicViewModel)
    singleOf(::ModifyArtistViewModel)
    singleOf(::ModifyPlaylistViewModel)

    // Selected elements
    singleOf(::SelectedAlbumViewModel)
    singleOf(::SelectedArtistViewModel)
    singleOf(::SelectedPlaylistViewModel)
    singleOf(::SelectedFolderViewModel)
    singleOf(::SelectedMonthViewModel)

    // Player
    singleOf(::PlayerViewModel)

    // Other
    singleOf(::ColorThemeManager)
    singleOf(::ViewSettingsManager)

}