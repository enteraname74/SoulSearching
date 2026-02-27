package com.github.enteraname74.soulsearching.composables.bottomsheets

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.addtoplaylist.AddToPlaylistBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.main.MusicBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.playlist.PlaylistBottomSheetDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.modules.PolymorphicModuleBuilder

object BottomSheetsNavigationHandler {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        MusicBottomSheetDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        AddToPlaylistBottomSheetDestination.register(
            entryProviderScope = entryProviderScope,
        )

        PlaylistBottomSheetDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )
    }

    fun serializerModule(
        polymorphicModuleBuilder: PolymorphicModuleBuilder<NavKey>
    ) {
        with(polymorphicModuleBuilder) {
            subclass(MusicBottomSheetDestination::class, MusicBottomSheetDestination.serializer())
            subclass(AddToPlaylistBottomSheetDestination::class, AddToPlaylistBottomSheetDestination.serializer())
            subclass(PlaylistBottomSheetDestination::class, PlaylistBottomSheetDestination.serializer())
        }
    }
}