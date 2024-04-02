package com.github.soulsearching.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.soulsearching.viewmodel.handler.SettingsAddMusicsViewModelHandler
import com.github.soulsearching.viewmodel.handler.AllAlbumsViewModeHandler
import com.github.soulsearching.viewmodel.handler.AllArtistsViewModelHandler
import com.github.soulsearching.viewmodel.handler.SettingsAllFolderViewModelHandler
import com.github.soulsearching.viewmodel.handler.AllImageCoversViewModelHandler
import com.github.soulsearching.viewmodel.handler.AllMusicsViewModelHandler
import com.github.soulsearching.viewmodel.handler.AllPlaylistsViewModelHandler
import com.github.soulsearching.viewmodel.handler.AllQuickAccessViewModelHandler
import com.github.soulsearching.viewmodel.handler.MainActivityViewModelHandler
import com.github.soulsearching.viewmodel.handler.ModifyAlbumViewModelHandler
import com.github.soulsearching.viewmodel.handler.ModifyArtistViewModelHandler
import com.github.soulsearching.viewmodel.handler.ModifyMusicViewModelHandler
import com.github.soulsearching.viewmodel.handler.ModifyPlaylistViewModelHandler
import com.github.soulsearching.viewmodel.handler.NavigationViewModelHandler
import com.github.soulsearching.viewmodel.handler.PlayerMusicListViewModelHandler
import com.github.soulsearching.viewmodel.handler.PlayerViewModelHandler
import com.github.soulsearching.viewmodel.handler.SelectedAlbumViewModelHandler
import com.github.soulsearching.viewmodel.handler.SelectedArtistViewModelHandler
import com.github.soulsearching.viewmodel.handler.SelectedPlaylistViewModelHandler
import com.github.soulsearching.viewmodel.handler.ViewModelHandler

/**
 * A ViewModel is represented by an Interface.
 * The benefits of it is that it can be used anywhere (Android, desktop) instead of
 * an abstract class (on android, we need to inherit from the ViewModel class, making it
 * inherit also from an abstract class).
 *
 * A ViewModel possess only an handler that is used for handling the logic of the ViewModel.
 * This handler can be an abstract class that implementations will need to specialize.
 * If the class is not abstract, it will make the ViewModel usable on any platform without specific implementation.
 */
interface SoulSearchingViewModel<Handler: ViewModelHandler> : ScreenModel {
    val handler: Handler
}

/**
 * ViewModel for adding new musics from the settings screen.
 */
typealias SettingsAddMusicsViewModel = SoulSearchingViewModel<SettingsAddMusicsViewModelHandler>

/**
 * ViewModel for managing all albums.
 */
typealias AllAlbumsViewModel = SoulSearchingViewModel<AllAlbumsViewModeHandler>

/**
 * ViewModel for managing all artists.
 */
typealias AllArtistsViewModel = SoulSearchingViewModel<AllArtistsViewModelHandler>

/**
 * ViewModel for managing all folders.
 */
typealias SettingsAllFoldersViewModel = SoulSearchingViewModel<SettingsAllFolderViewModelHandler>

/**
 * ViewModel for managing all image covers.
 */
typealias AllImageCoversViewModel = SoulSearchingViewModel<AllImageCoversViewModelHandler>

/**
 * ViewModel for the player.
 */
typealias PlayerViewModel = SoulSearchingViewModel<PlayerViewModelHandler>

/**
 * ViewModel for managing all musics.
 */
typealias AllMusicsViewModel = SoulSearchingViewModel<AllMusicsViewModelHandler>

/**
 * ViewModel for managing all playlists.
 */
typealias AllPlaylistsViewModel = SoulSearchingViewModel<AllPlaylistsViewModelHandler>

/**
 * ViewModel for managing all quick access.
 */
typealias AllQuickAccessViewModel = SoulSearchingViewModel<AllQuickAccessViewModelHandler>

/**
 * ViewModel for managing the main activity.
 */
typealias MainActivityViewModel = SoulSearchingViewModel<MainActivityViewModelHandler>

/**
 * ViewModel for the modify album screen.
 */
typealias ModifyAlbumViewModel = SoulSearchingViewModel<ModifyAlbumViewModelHandler>

/**
 * ViewModel for the modify artist screen.
 */
typealias ModifyArtistViewModel = SoulSearchingViewModel<ModifyArtistViewModelHandler>

/**
 * ViewModel for the modify music screen.
 */
typealias ModifyMusicViewModel = SoulSearchingViewModel<ModifyMusicViewModelHandler>

/**
 * ViewModel for the modify playlist screen.
 */
typealias ModifyPlaylistViewModel = SoulSearchingViewModel<ModifyPlaylistViewModelHandler>

/**
 * ViewModel for the player music list view.
 */
typealias PlayerMusicListViewModel = SoulSearchingViewModel<PlayerMusicListViewModelHandler>

/**
 * ViewModel for the selected album screen.
 */
typealias SelectedAlbumViewModel = SoulSearchingViewModel<SelectedAlbumViewModelHandler>

/**
 * ViewModel for the selected artist screen.
 */
typealias SelectedArtistViewModel = SoulSearchingViewModel<SelectedArtistViewModelHandler>

/**
 * ViewModel for the selected playlist screen.
 */
typealias SelectedPlaylistViewModel = SoulSearchingViewModel<SelectedPlaylistViewModelHandler>

/**
 * ViewModel for the navigation.
 */
typealias NavigationViewModel = SoulSearchingViewModel<NavigationViewModelHandler>
