package com.github.enteraname74.soulsearching.navigation

import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.AppInitSongFetchingDestination
import com.github.enteraname74.soulsearching.feature.editableelement.ModifyElementNavigationHandler
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageDestination
import com.github.enteraname74.soulsearching.feature.migration.MigrationDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceMode
import com.github.enteraname74.soulsearching.feature.playlistdetail.PlaylistDetailNavigationHandler
import com.github.enteraname74.soulsearching.feature.settings.SettingsNavigationHandler
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

internal val MainAppSerializerModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(MainPageDestination::class, MainPageDestination.serializer())
        subclass(MultipleArtistsChoiceDestination::class, MultipleArtistsChoiceDestination.serializer())

        PlaylistDetailNavigationHandler.serializerModule(
            polymorphicModuleBuilder = this,
        )
        ModifyElementNavigationHandler.serializerModule(
            polymorphicModuleBuilder = this,
        )
        SettingsNavigationHandler.serializerModule(
            polymorphicModuleBuilder = this,
        )
    }

    MultipleArtistsChoiceMode.serializerModule(
        serializerModuleBuilder = this,
    )
}