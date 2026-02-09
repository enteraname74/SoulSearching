package com.github.enteraname74.soulsearching.feature.playlistdetail

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.presentation.SelectedAlbumDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation.SelectedArtistDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.presentation.SelectedFolderDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.presentation.SelectedMonthDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.presentation.SelectedPlaylistDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.modules.PolymorphicModuleBuilder

object PlaylistDetailNavigationHandler {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        SelectedAlbumDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )
        SelectedArtistDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )
        SelectedFolderDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )
        SelectedMonthDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )
        SelectedPlaylistDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )
    }

    fun serializerModule(
        polymorphicModuleBuilder: PolymorphicModuleBuilder<NavKey>
    ) {
        with(polymorphicModuleBuilder) {
            subclass(SelectedAlbumDestination::class, SelectedAlbumDestination.serializer())
            subclass(SelectedArtistDestination::class, SelectedArtistDestination.serializer())
            subclass(SelectedFolderDestination::class, SelectedFolderDestination.serializer())
            subclass(SelectedMonthDestination::class, SelectedMonthDestination.serializer())
            subclass(SelectedPlaylistDestination::class, SelectedPlaylistDestination.serializer())
        }
    }
}