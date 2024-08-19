package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.coreui.theme.color.ColorThemeManager
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain.SelectedAlbumViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain.SelectedArtistViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.folderpage.domain.SelectedFolderViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.monthpage.domain.SelectedMonthViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.playlistpage.domain.SelectedPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.*
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.domain.ModifyAlbumViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.domain.ModifyArtistViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.domain.ModifyMusicViewModel
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.domain.ModifyPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.SettingsAddMusicsViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.SettingsAllFoldersViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val viewModelModule: Module = module {
    // Settings
    factoryOf(::SettingsAddMusicsViewModel)
    factoryOf(::SettingsAllFoldersViewModel)

    // Main page
    factoryOf(::AllMusicsViewModel)
    factoryOf(::AllAlbumsViewModel)
    factoryOf(::AllArtistsViewModel)
    factoryOf(::AllPlaylistsViewModel)
    factoryOf(::AllQuickAccessViewModel)
    factoryOf(::MainActivityViewModel)

    // Modify elements
    factoryOf(::ModifyAlbumViewModel)
    factoryOf(::ModifyMusicViewModel)
    factoryOf(::ModifyArtistViewModel)
    factoryOf(::ModifyPlaylistViewModel)

    // Selected elements
    factoryOf(::SelectedAlbumViewModel)
    factoryOf(::SelectedArtistViewModel)
    factoryOf(::SelectedPlaylistViewModel)
    factoryOf(::SelectedFolderViewModel)
    factoryOf(::SelectedMonthViewModel)

    // Player
    singleOf(::PlayerViewModel)

    // Other
    singleOf(::ColorThemeManager)
    singleOf(::ViewSettingsManager)

}