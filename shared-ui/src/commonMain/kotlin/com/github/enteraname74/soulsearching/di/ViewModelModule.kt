package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain.SelectedAlbumViewModel
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain.SelectedArtistViewModel
import com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain.SelectedFolderViewModel
import com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain.SelectedMonthViewModel
import com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain.SelectedPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.*
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.ModifyAlbumViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.ModifyArtistViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.ModifyMusicViewModel
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.ModifyPlaylistViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.SettingsAddMusicsViewModel
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.domain.SettingsAllFoldersViewModel
import com.github.enteraname74.soulsearching.feature.settings.colortheme.SettingsColorThemeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val viewModelModule: Module = module {
    // Settings
    factoryOf(::SettingsAddMusicsViewModel)
    factoryOf(::SettingsAllFoldersViewModel)
    factoryOf(::SettingsColorThemeViewModel)

    // Main page
    singleOf(::AllMusicsViewModel)
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