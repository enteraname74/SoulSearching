package com.github.enteraname74.soulsearching.feature.search.all

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.di.DiConstants
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.ext.swipeableView
import com.github.enteraname74.soulsearching.feature.search.SearchAllViewManager
import com.github.enteraname74.soulsearching.feature.swipeableview.SwipeableViewManager
import com.github.enteraname74.soulsearching.feature.swipeableview.SwipeableViewManagerHandler
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.navigation.OverlaySceneStrategy
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.qualifier.named
import kotlin.math.roundToInt

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


            if (rememberWindowSize() == WindowSize.Large) {
                LaunchInit { focusRequester.requestFocus() }
                Dialog(
                    onDismissRequest = navigator::pop,
                    properties = DialogProperties(
                        usePlatformDefaultWidth = false,
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(.6f)
                            .fillMaxHeight(.6f)
                            .clip(RoundedCornerShape(UiConstants.Spacing.large))
                            .background(SoulSearchingColorTheme.colorScheme.surface)
                    ) {
                        holder.Screen(
                            navigation = object : SearchAllNavScope {
                                override fun navigateBack() {
                                    navigator.pop()
                                }

                                override fun toDestination(destination: NavKey) {
                                    navigator.push(destination)
                                }
                            }
                        )
                    }
                }
            } else {
                val swipeableViewManager: SearchAllViewManager = koinInject()
                SwipeableViewManagerHandler(swipeableViewManager = swipeableViewManager)

                val previousState by swipeableViewManager.previousState.collectAsState()

                LaunchedEffect(swipeableViewManager.currentValue, previousState) {
                    if (
                        swipeableViewManager.currentValue == BottomSheetStates.COLLAPSED
                        && (previousState != BottomSheetStates.COLLAPSED && previousState != null)) {
                        navigator.pop()
                    }
                }

                LaunchInit {
                    swipeableViewManager.animateTo(
                        newState = BottomSheetStates.EXPANDED,
                        onExpanded = {
                            focusRequester.requestFocus()
                        }
                    )
                }

                SwipeableHolder(
                    swipeableViewManager = swipeableViewManager,
                ) {
                    holder.Screen(
                        navigation = object : SearchAllNavScope {
                            override fun navigateBack() {
                                swipeableViewManager.closeIfPossible()
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
}

@Composable
private fun SwipeableHolder(
    swipeableViewManager: SwipeableViewManager,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val maxHeight = with(LocalDensity.current) {
            this@BoxWithConstraints.maxHeight.toPx() + getNavigationBarPadding()
        }
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = swipeableViewManager.offset.roundToInt()
                    )
                }
                .swipeableView(
                    swipeableViewManager = swipeableViewManager,
                    anchors = mapOf(
                        maxHeight to BottomSheetStates.COLLAPSED,
                        0f to BottomSheetStates.EXPANDED,
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoulSearchingColorTheme.colorScheme.background)
            ) {
                content()
            }
        }
    }
}