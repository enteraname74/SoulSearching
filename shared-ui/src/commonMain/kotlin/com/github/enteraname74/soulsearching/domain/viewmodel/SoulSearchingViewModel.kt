package com.github.soulsearching.domain.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.soulsearching.settings.managemusics.addmusics.domain.SettingsAddMusicsViewModelHandler
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllAlbumsViewModeHandler
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllArtistsViewModelHandler
import com.github.soulsearching.settings.managemusics.managefolders.domain.SettingsAllFolderViewModelHandler
import com.github.soulsearching.coversprovider.domain.AllImageCoversViewModelHandler
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllMusicsViewModelHandler
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllPlaylistsViewModelHandler
import com.github.soulsearching.mainpage.domain.viewmodelhandler.AllQuickAccessViewModelHandler
import com.github.soulsearching.mainpage.domain.viewmodelhandler.MainActivityViewModelHandler
import com.github.soulsearching.modifyelement.modifyalbum.domain.ModifyAlbumViewModelHandler
import com.github.soulsearching.modifyelement.modifyartist.domain.ModifyArtistViewModelHandler
import com.github.soulsearching.modifyelement.modifymusic.domain.ModifyMusicViewModelHandler
import com.github.soulsearching.modifyelement.modifyplaylist.domain.ModifyPlaylistViewModelHandler
import com.github.soulsearching.domain.viewmodel.handler.NavigationViewModelHandler
import com.github.soulsearching.player.domain.PlayerViewModelHandler
import com.github.soulsearching.elementpage.albumpage.domain.SelectedAlbumViewModelHandler
import com.github.soulsearching.elementpage.artistpage.domain.SelectedArtistViewModelHandler
import com.github.soulsearching.elementpage.folderpage.domain.SelectedFolderViewModelHandler
import com.github.soulsearching.elementpage.monthpage.domain.SelectedMonthViewModelHandler
import com.github.soulsearching.elementpage.playlistpage.domain.SelectedPlaylistViewModelHandler

///**
// * A ViewModel is represented by an Interface.
// * The benefits of it is that it can be used anywhere (Android, desktop) instead of
// * an abstract class (on android, we need to inherit from the ViewModel class, making it
// * inherit also from an abstract class).
// *
// * A ViewModel possess only an handler that is used for handling the logic of the ViewModel.
// * This handler can be an abstract class that implementations will need to specialize.
// * If the class is not abstract, it will make the ViewModel usable on any platform without specific implementation.
// */
//interface SoulSearchingViewModel<Handler: ViewModelHandler> : ScreenModel {
//    val handler: ViewModelHandler
//}

/**
 * ViewModel for adding new musics from the settings screen.
 */
interface SettingsAddMusicsViewModel : ScreenModel {
    val handler: SettingsAddMusicsViewModelHandler
}

/**
 * ViewModel for managing all albums.
 */
interface AllAlbumsViewModel : ScreenModel {
    val handler: AllAlbumsViewModeHandler
}

/**
 * ViewModel for managing all artists.
 */
interface AllArtistsViewModel : ScreenModel {
    val handler: AllArtistsViewModelHandler
}

/**
 * ViewModel for managing all folders.
 */
interface SettingsAllFoldersViewModel : ScreenModel {
    val handler: SettingsAllFolderViewModelHandler
}

/**
 * ViewModel for managing all image covers.
 */
interface AllImageCoversViewModel : ScreenModel {
    val handler: AllImageCoversViewModelHandler
}

/**
 * ViewModel for the player.
 */
interface PlayerViewModel : ScreenModel {
    val handler: PlayerViewModelHandler
}

/**
 * ViewModel for managing all musics.
 */
interface AllMusicsViewModel : ScreenModel {
    val handler: AllMusicsViewModelHandler
}

/**
 * ViewModel for managing all playlists.
 */
interface AllPlaylistsViewModel : ScreenModel {
    val handler: AllPlaylistsViewModelHandler
}

/**
 * ViewModel for managing all quick access.
 */
interface AllQuickAccessViewModel : ScreenModel {
    val handler: AllQuickAccessViewModelHandler
}

/**
 * ViewModel for managing the main activity.
 */
interface MainActivityViewModel : ScreenModel {
    val handler: MainActivityViewModelHandler
}

/**
 * ViewModel for the modify album screen.
 */
interface ModifyAlbumViewModel : ScreenModel {
    val handler: ModifyAlbumViewModelHandler
}

/**
 * ViewModel for the modify artist screen.
 */
interface ModifyArtistViewModel : ScreenModel {
    val handler: ModifyArtistViewModelHandler
}

/**
 * ViewModel for the modify music screen.
 */
interface ModifyMusicViewModel : ScreenModel {
    val handler: ModifyMusicViewModelHandler
}

/**
 * ViewModel for the modify playlist screen.
 */
interface ModifyPlaylistViewModel : ScreenModel {
    val handler: ModifyPlaylistViewModelHandler
}

/**
 * ViewModel for the selected album screen.
 */
interface SelectedAlbumViewModel : ScreenModel {
    val handler: SelectedAlbumViewModelHandler
}

/**
 * ViewModel for the selected artist screen.
 */
interface SelectedArtistViewModel : ScreenModel {
    val handler: SelectedArtistViewModelHandler
}

/**
 * ViewModel for the selected playlist screen.
 */
interface SelectedPlaylistViewModel : ScreenModel {
    val handler: SelectedPlaylistViewModelHandler
}

/**
 * ViewModel for the selected folder screen.
 */
interface SelectedFolderViewModel: ScreenModel {
    val handler: SelectedFolderViewModelHandler
}

/**
 * ViewModel for the selected month screen.
 */
interface SelectedMonthViewModel: ScreenModel {
    val handler: SelectedMonthViewModelHandler
}

/**
 * ViewModel for the navigation.
 */
interface NavigationViewModel : ScreenModel {
    val handler: NavigationViewModelHandler
}
