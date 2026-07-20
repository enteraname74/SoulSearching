package com.github.enteraname74.soulsearching.feature.settings.cloud.explanations

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetDestination
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.navigation.BottomSheetSceneStrategy
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object SettingsCloudExplanationsDestination: SettingPage, BottomSheetDestination {

    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SettingsCloudExplanationsDestination>(
            metadata = BottomSheetSceneStrategy.bottomSheet()
        ) {
            val holder: SettingsCloudExplanationsViewHolder = koinViewModel()
            holder.Screen(
                navigation = object: SettingsCloudExplanationsNavScope {}
            )
        }
    }
}