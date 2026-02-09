package com.github.enteraname74.soulsearching.feature.editableelement

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation.ModifyAlbumDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.presentation.ModifyArtistDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation.ModifyPlaylistDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.modules.PolymorphicModuleBuilder

object ModifyElementNavigationHandler {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        ModifyAlbumDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )
        ModifyArtistDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )
        ModifyMusicDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )
        ModifyPlaylistDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )
    }

    fun serializerModule(
        polymorphicModuleBuilder: PolymorphicModuleBuilder<NavKey>
    ) {
        with(polymorphicModuleBuilder) {
            subclass(ModifyAlbumDestination::class, ModifyAlbumDestination.serializer())
            subclass(ModifyArtistDestination::class, ModifyArtistDestination.serializer())
            subclass(ModifyMusicDestination::class, ModifyMusicDestination.serializer())
            subclass(ModifyPlaylistDestination::class, ModifyPlaylistDestination.serializer())
        }
    }
}