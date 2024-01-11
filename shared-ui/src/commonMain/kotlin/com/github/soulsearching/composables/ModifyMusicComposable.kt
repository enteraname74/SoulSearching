package com.github.soulsearching.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.SoulSearchingContext
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.ScreenOrientation
import com.github.soulsearching.viewmodel.ModifyMusicViewModel
import java.util.UUID

@Composable
fun ModifyMusicComposable(
    modifyMusicViewModel: ModifyMusicViewModel,
    selectedMusicId: String,
    finishAction: () -> Unit,
    selectImage: () -> Unit
) {
    var isMusicFetched by rememberSaveable {
        mutableStateOf(false)
    }

    if (!isMusicFetched) {
        modifyMusicViewModel.handler.getMusicFromId(UUID.fromString(selectedMusicId))
        isMusicFetched = true
    }

    val focusManager = LocalFocusManager.current
    val musicState by modifyMusicViewModel.handler.state.collectAsState()

    Scaffold(
        topBar = {
            AppHeaderBar(
                title = strings.musicInformation,
                leftAction = finishAction,
                rightIcon = Icons.Rounded.Done,
                rightAction = {
                    modifyMusicViewModel.handler.onMusicEvent(MusicEvent.UpdateMusic)
                    finishAction()
                }
            )
        },
        content = { padding ->
            when (SoulSearchingContext.orientation) {
                ScreenOrientation.HORIZONTAL -> {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SoulSearchingColorTheme.colorScheme.secondary)
                            .padding(padding)
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    focusManager.clearFocus()
                                })
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(Constants.Spacing.medium)
                                .weight(1F),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(bottom = Constants.Spacing.medium),
                                text = strings.albumCover,
                                color = SoulSearchingColorTheme.colorScheme.onSecondary
                            )
                            AppImage(
                                bitmap = musicState.cover,
                                size = 200.dp,
                                modifier = Modifier.clickable { selectImage() }
                            )
                        }
                        ModifyMusicTextFields(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(2F)
                                .background(color = SoulSearchingColorTheme.colorScheme.primary)
                                .padding(Constants.Spacing.medium),
                            state = musicState,
                            focusManager = focusManager,
                            setName = { modifyMusicViewModel.handler.onMusicEvent(MusicEvent.SetName(it)) },
                            setAlbum = {
                                modifyMusicViewModel.handler.onMusicEvent(
                                    MusicEvent.SetAlbum(
                                        it
                                    )
                                )
                            },
                            setArtist = {
                                modifyMusicViewModel.handler.onMusicEvent(
                                    MusicEvent.SetArtist(
                                        it
                                    )
                                )
                            }
                        )
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SoulSearchingColorTheme.colorScheme.secondary)
                            .padding(padding)
                            .padding(top = Constants.Spacing.medium)
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    focusManager.clearFocus()
                                })
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(Constants.Spacing.medium),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(bottom = Constants.Spacing.medium),
                                text = strings.albumCover,
                                color = SoulSearchingColorTheme.colorScheme.onSecondary
                            )
                            AppImage(
                                bitmap = musicState.cover,
                                size = 200.dp,
                                modifier = Modifier.clickable { selectImage() }
                            )
                        }
                        ModifyMusicTextFields(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1F)
                                .clip(RoundedCornerShape(topStart = 50f, topEnd = 50f))
                                .background(color = SoulSearchingColorTheme.colorScheme.primary)
                                .padding(Constants.Spacing.medium),
                            state = musicState,
                            focusManager = focusManager,
                            setName = { modifyMusicViewModel.handler.onMusicEvent(MusicEvent.SetName(it)) },
                            setAlbum = {
                                modifyMusicViewModel.handler.onMusicEvent(
                                    MusicEvent.SetAlbum(
                                        it
                                    )
                                )
                            },
                            setArtist = {
                                modifyMusicViewModel.handler.onMusicEvent(
                                    MusicEvent.SetArtist(
                                        it
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ModifyMusicTextFields(
    modifier: Modifier,
    state: MusicState,
    focusManager: FocusManager,
    setName: (String) -> Unit,
    setAlbum: (String) -> Unit,
    setArtist: (String) -> Unit
) {
    Row(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(4F),
            verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
        ) {
            AppTextField(
                value = state.name,
                onValueChange = { setName(it) },
                labelName = strings.musicName,
                focusManager = focusManager
            )
            AppTextField(
                value = state.album,
                onValueChange = { setAlbum(it) },
                labelName = strings.albumName,
                focusManager = focusManager
            )
            AppTextField(
                value = state.artist,
                onValueChange = { setArtist(it) },
                labelName = strings.artistName,
                focusManager = focusManager
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        )
    }
}