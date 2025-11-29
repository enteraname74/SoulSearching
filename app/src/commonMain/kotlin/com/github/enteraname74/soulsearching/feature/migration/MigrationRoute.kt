package com.github.enteraname74.soulsearching.feature.migration

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.application.MainAppDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MigrationRoute(
    viewModel: MigrationViewModel,
) {
    SoulLoadingScreen(
        text = strings.appMigration,
    )
}

@Serializable
data object MigrationDestination : NavKey {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<MigrationDestination> {
            MigrationRoute(
                viewModel = koinViewModel {
                    parametersOf(
                        object : MigrationNavScope {
                            override val toMainApp: () -> Unit = {
                                navigator.navigate(MainAppDestination) {
                                    clearBackStack = true
                                }
                            }
                        }
                    )
                }
            )
        }
    }
}