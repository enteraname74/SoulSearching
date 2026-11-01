package com.github.enteraname74.soulsearching.feature.search.all

import androidx.compose.ui.focus.FocusRequester
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.di.DiConstants
import com.github.enteraname74.soulsearching.feature.search.composable.SearchAdaptiveView
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.navigation.OverlaySceneStrategy
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named

@Serializable
data object SearchAllDestination : NavKey {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<SearchAllDestination>(
            metadata = OverlaySceneStrategy.overlay(),
        ) {
            val holder: SearchAllViewHolder = koinViewModel()
            val focusRequester: FocusRequester = koinInject(named(DiConstants.SearchAllFocusRequester))
            val swipeableViewManager: SearchAllViewManager = koinInject()

            SearchAdaptiveView(
                swipeableViewManager = swipeableViewManager,
                focusRequester = focusRequester,
                dismiss = navigator::pop,
            ) { onBack ->
                holder.Screen(
                    navigation = object : SearchAllNavScope {
                        override fun navigateBack() {
                            onBack()
                        }

                        override fun toDestination(destination: NavKey) {
                            navigator.push(destination)
                        }
                    }
                )
            }
        }
    }
}