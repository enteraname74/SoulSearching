package com.github.soulsearching.screens.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.MusicSelectableComposable
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.composables.SoulSearchingBackHandler
import com.github.soulsearching.composables.settings.LoadingComposable
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.strings.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.AddMusicsStateType
import com.github.soulsearching.viewmodel.SettingsAddMusicsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction2

/**
 * Represent the view of the add musics screen in the settings.
 */
class SettingsAddMusicsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SettingsAddMusicsViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        SettingsAddMusicsScreenView(
            addMusicsViewModel = screenModel,
            finishAction = {
                navigator.pop()
            },
            saveMusicFunction = screenModel.handler::addMusic
        )
    }
}

@Composable
fun SettingsAddMusicsScreenView(
    addMusicsViewModel: SettingsAddMusicsViewModel,
    finishAction: () -> Unit,
    saveMusicFunction: KSuspendFunction2<Music, ImageBitmap?, Unit>
) {
    val addMusicsState by addMusicsViewModel.handler.state.collectAsState()

    var isFetchingMusics by rememberSaveable {
        mutableStateOf(false)
    }

    var isSavingMusics by rememberSaveable {
        mutableStateOf(false)
    }

    SoulSearchingBackHandler {
        finishAction()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.primary)
    ) {
        AppHeaderBar(
            title = strings.addMusicsTitle,
            leftAction = finishAction,
            rightIcon = if (addMusicsState.state == AddMusicsStateType.WAITING_FOR_USER_ACTION) Icons.Rounded.Check else null,
            rightAction = {
                if (addMusicsState.state != AddMusicsStateType.WAITING_FOR_USER_ACTION) {
                    return@AppHeaderBar
                }
                addMusicsViewModel.handler.onAddMusicEvent(
                    AddMusicsEvent.SetState(
                        newState = AddMusicsStateType.SAVING_MUSICS
                    )
                )
            }
        )
        when(addMusicsState.state) {
            AddMusicsStateType.FETCHING_MUSICS -> {
                var progress by rememberSaveable {
                    mutableFloatStateOf(0F)
                }
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                    label = "ANIMATED_PROGRESS_FETCHING_MUSICS_SETTINGS_ADD_MUSICS_VIEW"
                )
                LoadingComposable(
                    progressIndicator = animatedProgress,
                    progressMessage = strings.searchingSongsFromYourDevice
                )
                if (!isFetchingMusics) {
                    LaunchedEffect(key1 = "FetchingMusics") {
                        isFetchingMusics = true
                        addMusicsViewModel.handler.fetchAndAddNewMusics { progress = it }
                        isFetchingMusics = false
                    }
                }
            }
            AddMusicsStateType.SAVING_MUSICS -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    var progress by rememberSaveable {
                        mutableFloatStateOf(0F)
                    }
                    val animatedProgress by animateFloatAsState(
                        targetValue = progress,
                        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                        label = "ANIMATED_PROGRESS_SAVING_MUSICS_SETTINGS_ADD_MUSICS_VIEW"
                    )
                    LoadingComposable(
                        progressIndicator = animatedProgress,
                        progressMessage = strings.savingNewMusics
                    )
                    if (!isSavingMusics) {
                        LaunchedEffect(key1 = "SavingMusics") {
                            CoroutineScope(Dispatchers.IO).launch {
                                isSavingMusics = true
                                var count = 0
                                addMusicsState.fetchedMusics.filter { it.isSelected }.forEach{
                                    saveMusicFunction(it.music, it.cover)
                                    count ++
                                    progress = (count * 1F) / addMusicsState.fetchedMusics.size
                                }
                                isSavingMusics = false
                                addMusicsViewModel.handler.onAddMusicEvent(
                                    AddMusicsEvent.SetState(
                                        newState = AddMusicsStateType.FETCHING_MUSICS
                                    ))
                            }
                        }
                    }
                }
            }
            AddMusicsStateType.WAITING_FOR_USER_ACTION -> {
                if (addMusicsState.fetchedMusics.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = strings.noNewMusics,
                            color = SoulSearchingColorTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    LazyColumn {
                        items(addMusicsState.fetchedMusics) {
                            MusicSelectableComposable(
                                music = it.music,
                                onClick = {
                                    addMusicsViewModel.handler.onAddMusicEvent(
                                        AddMusicsEvent.SetSelectedMusic(
                                            music = it.music,
                                            isSelected = !it.isSelected
                                        )
                                    )
                                },
                                isSelected = it.isSelected,
                                musicCover = it.cover
                            )
                        }
                        item {
                            PlayerSpacer()
                        }
                    }
                }
            }
        }
    }
}