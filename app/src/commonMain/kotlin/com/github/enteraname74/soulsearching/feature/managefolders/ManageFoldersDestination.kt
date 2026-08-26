package com.github.enteraname74.soulsearching.feature.managefolders

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class ManageFoldersDestination(
    val mode: Mode,
) : SettingPage {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navScope: ManageFoldersNavScope
        ) {
            entryProviderScope.entry<ManageFoldersDestination> { key ->
                val holder: ManageFoldersViewHolder = koinViewModel {
                    parametersOf(key.mode)
                }
                holder.Screen(navigation = navScope)
            }
        }
    }

    enum class Mode {
        InitialFetch,
        Settings;
    }
}
