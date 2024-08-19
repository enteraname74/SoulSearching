package com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import com.github.enteraname74.soulsearching.coreui.ScreenOrientation
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulBitmapImage
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextField
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.domain.ModifyPlaylistEvent
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyplaylist.domain.ModifyPlaylistViewModel
import java.util.*

@Composable
fun ModifyPlaylistComposable(
    modifyPlaylistViewModel: ModifyPlaylistViewModel,
    selectedPlaylistId: String,
    finishAction: () -> Unit,
    selectImage: () -> Unit
) {
    var isPlaylistFetched by rememberSaveable {
        mutableStateOf(false)
    }

    if (!isPlaylistFetched) {
        modifyPlaylistViewModel.onEvent(
            ModifyPlaylistEvent.PlaylistFromId(
                playlistId = UUID.fromString(selectedPlaylistId)
            )
        )
        isPlaylistFetched = true
    }

    val focusManager = LocalFocusManager.current
    val state by modifyPlaylistViewModel.state.collectAsState()

    Scaffold(
        topBar = {
            SoulTopBar(
                title = strings.playlistInformation,
                leftAction = finishAction,
                rightIcon = Icons.Rounded.Done,
                rightAction = {
                    modifyPlaylistViewModel.onEvent(ModifyPlaylistEvent.UpdatePlaylist)
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
                                .padding(UiConstants.Spacing.medium)
                                .weight(1F),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(bottom = UiConstants.Spacing.medium),
                                text = strings.playlistCover,
                                color = SoulSearchingColorTheme.colorScheme.onSecondary
                            )
                            SoulBitmapImage(
                                bitmap = state.playlistCover,
                                size = 200.dp,
                                modifier = Modifier.clickable { selectImage() }
                            )
                        }
                        ModifyPlaylistTextFields(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(2F)
                                .background(color = SoulSearchingColorTheme.colorScheme.primary)
                                .padding(UiConstants.Spacing.medium),
                            playlistName = state.selectedPlaylist.name,
                            focusManager = focusManager,
                            setName = {
                                modifyPlaylistViewModel.onEvent(ModifyPlaylistEvent.SetName(it))
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
                            .padding(top = UiConstants.Spacing.medium)
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    focusManager.clearFocus()
                                })
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(UiConstants.Spacing.medium),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(bottom = UiConstants.Spacing.medium),
                                text = strings.playlistCover,
                                color = SoulSearchingColorTheme.colorScheme.onSecondary
                            )
                            SoulBitmapImage(
                                bitmap = state.playlistCover,
                                size = 200.dp,
                                modifier = Modifier.clickable { selectImage() }
                            )
                        }
                        ModifyPlaylistTextFields(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1F)
                                .clip(RoundedCornerShape(topStart = 50f, topEnd = 50f))
                                .background(color = SoulSearchingColorTheme.colorScheme.primary)
                                .padding(UiConstants.Spacing.medium),
                            playlistName = state.selectedPlaylist.name,
                            focusManager = focusManager,
                            setName = {
                                modifyPlaylistViewModel.onEvent(ModifyPlaylistEvent.SetName(it))
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ModifyPlaylistTextFields(
    modifier: Modifier,
    playlistName: String,
    focusManager: FocusManager,
    setName: (String) -> Unit
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
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
        ) {
            SoulTextField(
                value = playlistName,
                onValueChange = { setName(it) },
                labelName = strings.playlistName,
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