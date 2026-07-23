package com.github.enteraname74.soulsearching.composables.bottomsheets

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.composables.bottomsheets.album.AlbumBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.artist.ArtistBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.folder.FolderBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.main.MusicBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.month.MonthBottomSheetDestination
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

        PlaylistBottomSheetDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        ArtistBottomSheetDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        AlbumBottomSheetDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        FolderBottomSheetDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        MonthBottomSheetDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )
    }

    fun serializerModule(
        polymorphicModuleBuilder: PolymorphicModuleBuilder<NavKey>
    ) {
        with(polymorphicModuleBuilder) {
            subclass(MusicBottomSheetDestination::class, MusicBottomSheetDestination.serializer())
            subclass(PlaylistBottomSheetDestination::class, PlaylistBottomSheetDestination.serializer())
            subclass(ArtistBottomSheetDestination::class, ArtistBottomSheetDestination.serializer())
            subclass(AlbumBottomSheetDestination::class, AlbumBottomSheetDestination.serializer())
            subclass(FolderBottomSheetDestination::class, FolderBottomSheetDestination.serializer())
            subclass(MonthBottomSheetDestination::class, MonthBottomSheetDestination.serializer())
        }
    }
}
