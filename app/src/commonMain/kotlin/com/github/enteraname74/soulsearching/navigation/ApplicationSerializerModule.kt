package com.github.enteraname74.soulsearching.navigation

import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.AppInitSongFetchingDestination
import com.github.enteraname74.soulsearching.feature.application.MainAppDestination
import com.github.enteraname74.soulsearching.feature.migration.MigrationDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceDestination
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

internal val ApplicationSerializerModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(MigrationDestination::class, MigrationDestination.serializer())
        subclass(MainAppDestination::class, MainAppDestination.serializer())
        subclass(AppInitSongFetchingDestination::class, AppInitSongFetchingDestination.serializer())
        subclass(MultipleArtistsChoiceDestination::class, MultipleArtistsChoiceDestination.serializer())
    }
}