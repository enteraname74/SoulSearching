package com.github.soulsearching.screens.settings

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.soulsearching.R
import com.github.soulsearching.classes.enumsAndTypes.AddMusicsStateType
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.MusicSelectableComposable
import com.github.soulsearching.composables.settings.FetchingNewMusicsComposable
import com.github.soulsearching.composables.settings.SavingNewMusicsComposable
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.AddMusicsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction2

@Composable
fun SettingsAddMusicsScreen(
    addMusicsViewModel: AddMusicsViewModel,
    finishAction: () -> Unit,
    saveMusicFunction: KSuspendFunction2<Music, Bitmap?, Unit>
) {
    val addMusicsState by addMusicsViewModel.state.collectAsState()
    val context = LocalContext.current

    var isFetchingMusics by rememberSaveable {
        mutableStateOf(false)
    }

    var isSavingMusics by rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler {
        finishAction()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DynamicColor.primary)
    ) {
        AppHeaderBar(
            title = stringResource(id = R.string.add_musics_title),
            leftAction = finishAction,
            rightIcon = Icons.Rounded.Check,
            rightAction = {
                addMusicsViewModel.onAddMusicEvent(
                    AddMusicsEvent.SetState(
                        newState = AddMusicsStateType.SAVING_MUSICS
                    )
                )
            }
        )
        when(addMusicsState.state) {
            AddMusicsStateType.FETCHING_MUSICS -> {
                var progress by rememberSaveable {
                    mutableStateOf(0F)
                }
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
                )
                FetchingNewMusicsComposable(progressIndicator = animatedProgress)
                if (!isFetchingMusics) {
                    LaunchedEffect(key1 = "FetchingMusics") {
                        isFetchingMusics = true
                        addMusicsViewModel.fetchNewMusics(context) { progress = it }
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
                    SavingNewMusicsComposable()
                    if (!isSavingMusics) {
                        LaunchedEffect(key1 = "SavingMusics") {
                            CoroutineScope(Dispatchers.IO).launch {
                                isSavingMusics = true
                                addMusicsState.fetchedMusics.filter { it.isSelected }.forEach{
                                    saveMusicFunction(it.music, it.cover)
                                }
                                isSavingMusics = false
                                addMusicsViewModel.onAddMusicEvent(AddMusicsEvent.SetState(
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
                            text = stringResource(id = R.string.no_new_musics),
                            color = DynamicColor.onPrimary
                        )
                    }
                } else {
                    LazyColumn {
                        items(addMusicsState.fetchedMusics) {
                            MusicSelectableComposable(
                                music = it.music,
                                onClick = {
                                    addMusicsViewModel.onAddMusicEvent(
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
                    }
                }
            }
        }
    }
}