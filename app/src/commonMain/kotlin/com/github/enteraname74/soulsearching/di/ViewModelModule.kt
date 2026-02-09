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
import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedViewModel
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.domain.SettingsAboutViewModel
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceViewModel
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.AppInitSongFetchingViewModel
import com.github.enteraname74.soulsearching.feature.migration.MigrationViewModel
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist.SettingsArtistCoverMethodViewModel
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsScreenViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val viewModelModule: Module = module {
    // Settings
    viewModelOf(::SettingsScreenViewModel)
    viewModelOf(::SettingsAddMusicsViewModel)
    viewModelOf(::SettingsAllFoldersViewModel)
    viewModelOf(::SettingsColorThemeViewModel)
    viewModelOf(::SettingsMainPagePersonalisationViewModel)
    viewModelOf(::SettingsPlayerPersonalisationViewModel)
    viewModelOf(::SettingsStatisticsViewModel)
    viewModelOf(::SettingsThemeSelectionViewModel)
    viewModelOf(::SettingsAdvancedViewModel)
    viewModelOf(::SettingsArtistCoverMethodViewModel)
    viewModelOf(::SettingsAboutViewModel)

    // Main page
    viewModelOf(::MainPageViewModel)
    viewModelOf(::ApplicationViewModel)

    // Song fetching and management
    viewModelOf(::AppInitSongFetchingViewModel)
    viewModelOf(::MultipleArtistsChoiceViewModel)

    // Modify elements
    viewModelOf(::ModifyAlbumViewModel)
    viewModelOf(::ModifyMusicViewModel)
    viewModelOf(::ModifyArtistViewModel)
    viewModelOf(::ModifyPlaylistViewModel)

    // Selected elements
    viewModelOf(::SelectedAlbumViewModel)
    viewModelOf(::SelectedArtistViewModel)
    viewModelOf(::SelectedPlaylistViewModel)
    viewModelOf(::SelectedFolderViewModel)
    viewModelOf(::SelectedMonthViewModel)

    // Player
    viewModelOf(::PlayerViewModel)

    // Other
    singleOf(::ColorThemeManager)
    singleOf(::ViewSettingsManager)

    viewModelOf(::MigrationViewModel)
}