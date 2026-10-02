package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.addtoplaylist.AddToPlaylistBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.addtoplaylist.AddToPlaylistBottomSheetNavScope
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation.ModifyAlbumDestination
import com.github.enteraname74.soulsearching.navigation.BottomSheetSceneStrategy
import com.github.enteraname74.soulsearching.navigation.LocalBottomSheetCloseWithAnimAction
import com.github.enteraname74.soulsearching.navigation.NavigationAnimations
import com.github.enteraname74.soulsearching.navigation.Navigator
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Serializable
data class AlbumBottomSheetDestination(
    val albumIds: List<Uuid>,
) : BottomSheetDestination {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<AlbumBottomSheetDestination>(
                metadata = BottomSheetSceneStrategy.bottomSheet()
            ) { params ->
                NavHost(
                    initialRoute = InnerAlbumDestination(params.albumIds),
                    parentNavigator = navigator,
                )
            }
        }
    }
}

@Serializable
data class InnerAlbumDestination(
    val albumIds: List<Uuid>,
) : NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navScope: AlbumBottomSheetNavScope,
        ) {
            entryProviderScope.entry<InnerAlbumDestination> { params ->
                val viewModel: AlbumBottomSheetViewModel = koinViewModel {
                    parametersOf(navScope, AlbumBottomSheetDestination(params.albumIds))
                }
                AlbumBottomSheetScreen(
                    viewModel = viewModel,
                )
            }
        }
    }
}

@Composable
private fun NavHost(
    initialRoute: NavKey,
    parentNavigator: Navigator,
    loadingManager: LoadingManager = injectElement(),
) {
    val isLoading: Boolean by loadingManager.state.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration { serializersModule = SerializerModule },
        initialRoute,
    )
    val navigator = remember { Navigator(backStack) }

    val closeWithAnim = LocalBottomSheetCloseWithAnimAction.current

    NavDisplay(
        backStack = backStack,
        sizeTransform = SizeTransform(
            clip = false,
            sizeAnimationSpec = { _, _ ->
                tween(
                    durationMillis = UiConstants.AnimationDuration.medium,
                    delayMillis = UiConstants.AnimationDuration.short,
                )
            },
        ),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = {
            if (!isLoading) {
                navigator.pop()
            }
        },
        transitionSpec = { NavigationAnimations.default },
        popTransitionSpec = { NavigationAnimations.default },
        entryProvider = entryProvider {
            InnerAlbumDestination.register(
                entryProviderScope = this,
                navScope = object : AlbumBottomSheetNavScope {
                    override val navigateBack: () -> Unit = {
                        closeWithAnim { }
                    }
                    override val toModifyAlbum: (albumId: Uuid) -> Unit = { albumId ->
                        closeWithAnim {
                            parentNavigator.push(ModifyAlbumDestination(albumId))
                        }
                    }
                    override val toAddToPlaylists: (musicIds: List<Uuid>) -> Unit = {
                        navigator.push(
                            AddToPlaylistBottomSheetDestination(
                                selectedMusicIds = it,
                            )
                        )
                    }
                }
            )
            AddToPlaylistBottomSheetDestination.register(
                entryProviderScope = this,
                navScope = object : AddToPlaylistBottomSheetNavScope {
                    override val onSave: () -> Unit = {
                        closeWithAnim { }
                    }
                    override val navigateBack: () -> Unit = {
                        navigator.pop()
                    }
                }
            )
        }
    )
}

private val SerializerModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(InnerAlbumDestination::class, InnerAlbumDestination.serializer())
        subclass(AddToPlaylistBottomSheetDestination::class, AddToPlaylistBottomSheetDestination.serializer())
    }
}
