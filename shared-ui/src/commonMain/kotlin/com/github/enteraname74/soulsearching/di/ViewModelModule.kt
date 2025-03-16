package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.application.ApplicationViewModel
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
import com.github.enteraname74.soulsearching.feature.settings.colortheme.themeselection.domain.SettingsThemeSelectionViewModel
import com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.domain.SettingsMainPagePersonalisationViewModel
import com.github.enteraname74.soulsearching.feature.settings.personalisation.player.domain.SettingsPlayerPersonalisationViewModel
import com.github.enteraname74.soulsearching.feature.settings.statistics.domain.SettingsStatisticsViewModel
import com.github.enteraname74.soulsearching.feature.settings.cloud.SettingsCloudViewModel
import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedViewModel
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.domain.SettingsAboutViewModel
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceViewModel
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.AppInitSongFetchingViewModel
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsScreenViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val viewModelModule: Module = module {
    // Settings
    factoryOf(::SettingsScreenViewModel)
    factoryOf(::SettingsAddMusicsViewModel)
    factoryOf(::SettingsAllFoldersViewModel)
    factoryOf(::SettingsColorThemeViewModel)
    factoryOf(::SettingsMainPagePersonalisationViewModel)
    factoryOf(::SettingsPlayerPersonalisationViewModel)
    factoryOf(::SettingsStatisticsViewModel)
    factoryOf(::SettingsThemeSelectionViewModel)
    factoryOf(::SettingsAdvancedViewModel)
    factoryOf(::SettingsAboutViewModel)
    factoryOf(::SettingsCloudViewModel)

    // Main page
    singleOf(::MainPageViewModel)
    singleOf(::ApplicationViewModel)

    // Song fetching and management
    factoryOf(::AppInitSongFetchingViewModel)
    factoryOf(::MultipleArtistsChoiceViewModel)

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