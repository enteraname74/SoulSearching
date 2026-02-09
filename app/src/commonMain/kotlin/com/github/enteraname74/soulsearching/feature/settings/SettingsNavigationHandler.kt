package com.github.enteraname74.soulsearching.feature.settings

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.aboutpage.SettingsAboutDestination
import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedDestination
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist.SettingsArtistCoverMethodDestination
import com.github.enteraname74.soulsearching.feature.settings.colortheme.SettingsColorThemeDestination
import com.github.enteraname74.soulsearching.feature.settings.colortheme.colorseed.SettingsColorSeedDestination
import com.github.enteraname74.soulsearching.feature.settings.colortheme.themeselection.presentation.SettingsThemeSelectionDestination
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.SettingsAddMusicsDestination
import com.github.enteraname74.soulsearching.feature.settings.managemusics.managefolders.presentation.SettingsUsedFoldersDestination
import com.github.enteraname74.soulsearching.feature.settings.managemusics.presentation.SettingsManageMusicsDestination
import com.github.enteraname74.soulsearching.feature.settings.personalisation.SettingsPersonalisationDestination
import com.github.enteraname74.soulsearching.feature.settings.personalisation.album.SettingsAlbumViewPersonalisationDestination
import com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.presentation.SettingsMainPagePersonalisationDestination
import com.github.enteraname74.soulsearching.feature.settings.personalisation.music.SettingsMusicViewPersonalisationDestination
import com.github.enteraname74.soulsearching.feature.settings.personalisation.player.presentation.SettingsPlayerPersonalisationDestination
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsDestination
import com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.SettingsStatisticsDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.modules.PolymorphicModuleBuilder

object SettingsNavigationHandler {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        SettingsStatisticsDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsAboutDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsArtistCoverMethodDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsAdvancedDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsThemeSelectionDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsColorSeedDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsColorThemeDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsAddMusicsDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsUsedFoldersDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsManageMusicsDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsPersonalisationDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsMainPagePersonalisationDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsPlayerPersonalisationDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsMusicViewPersonalisationDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )

        SettingsAlbumViewPersonalisationDestination.register(
            entryProviderScope = entryProviderScope,
            navigator = navigator,
        )
    }

    fun serializerModule(
        polymorphicModuleBuilder: PolymorphicModuleBuilder<NavKey>
    ) {
        with(polymorphicModuleBuilder) {
            subclass(SettingsStatisticsDestination::class, SettingsStatisticsDestination.serializer())
            subclass(SettingsDestination::class, SettingsDestination.serializer())
            subclass(SettingsAboutDestination::class, SettingsAboutDestination.serializer())
            subclass(SettingsArtistCoverMethodDestination::class, SettingsArtistCoverMethodDestination.serializer())
            subclass(SettingsAdvancedDestination::class, SettingsAdvancedDestination.serializer())
            subclass(SettingsThemeSelectionDestination::class, SettingsThemeSelectionDestination.serializer())
            subclass(SettingsColorSeedDestination::class, SettingsColorSeedDestination.serializer())
            subclass(SettingsColorThemeDestination::class, SettingsColorThemeDestination.serializer())
            subclass(SettingsAddMusicsDestination::class, SettingsAddMusicsDestination.serializer())
            subclass(SettingsUsedFoldersDestination::class, SettingsUsedFoldersDestination.serializer())
            subclass(SettingsManageMusicsDestination::class, SettingsManageMusicsDestination.serializer())
            subclass(SettingsPersonalisationDestination::class, SettingsPersonalisationDestination.serializer())
            subclass(SettingsMainPagePersonalisationDestination::class, SettingsMainPagePersonalisationDestination.serializer())
            subclass(SettingsPlayerPersonalisationDestination::class, SettingsPlayerPersonalisationDestination.serializer())
            subclass(SettingsMusicViewPersonalisationDestination::class, SettingsMusicViewPersonalisationDestination.serializer())
            subclass(SettingsAlbumViewPersonalisationDestination::class, SettingsAlbumViewPersonalisationDestination.serializer())
        }
    }
}