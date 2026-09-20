package com.github.enteraname74.soulsearching.navigation

import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.appinit.AppInitSongFetchingDestination
import com.github.enteraname74.soulsearching.feature.managefolders.ManageFoldersDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceDestination
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

internal val OnboardingSerializerModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(AppInitSongFetchingDestination::class, AppInitSongFetchingDestination.serializer())
        subclass(ManageFoldersDestination::class, ManageFoldersDestination.serializer())
        subclass(MultipleArtistsChoiceDestination::class, MultipleArtistsChoiceDestination.serializer())
    }
}