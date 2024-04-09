package com.github.soulsearching.modifyelement.modifyalbum.presentation.composable

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
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.composables.AppTextField
import com.github.soulsearching.domain.model.types.ScreenOrientation
import com.github.soulsearching.domain.viewmodel.ModifyAlbumViewModel
import com.github.soulsearching.modifyelement.modifyalbum.domain.ModifyAlbumEvent
import com.github.soulsearching.strings.strings
import java.util.UUID

@Composable
fun ModifyAlbumComposable(
    modifyAlbumViewModel: ModifyAlbumViewModel,
    selectedAlbumId: String,
    finishAction: () -> Unit,
    selectImage: () -> Unit
) {

    val state by modifyAlbumViewModel.handler.state.collectAsState()

    var isSelectedAlbumFetched by rememberSaveable {
        mutableStateOf(false)
    }

    if (!isSelectedAlbumFetched) {
        modifyAlbumViewModel.handler.onEvent(
            ModifyAlbumEvent.AlbumFromID(
                albumId = UUID.fromString(selectedAlbumId)
            )
        )
        isSelectedAlbumFetched = true
    }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            AppHeaderBar(
                title = strings.albumInformation,
                leftAction = finishAction,
                rightIcon = Icons.Rounded.Done,
                rightAction = {
                    modifyAlbumViewModel.handler.onEvent(ModifyAlbumEvent.UpdateAlbum)
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
                                bitmap = state.albumCover,
                                size = 200.dp,
                                modifier = Modifier.clickable { selectImage() }
                            )
                        }
                        ModifyAlbumTextFields(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(2F)
                                .background(color = SoulSearchingColorTheme.colorScheme.primary)
                                .padding(Constants.Spacing.medium),
                            albumName = state.albumWithMusics.album.albumName,
                            artistName = state.albumWithMusics.artist!!.artistName,
                            focusManager = focusManager,
                            setName = {
                                modifyAlbumViewModel.handler.onEvent(
                                    ModifyAlbumEvent.SetName(
                                        it
                                    )
                                )
                            },
                            setArtist = {
                                modifyAlbumViewModel.handler.onEvent(
                                    ModifyAlbumEvent.SetArtist(
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
                                bitmap = state.albumCover,
                                size = 200.dp,
                                modifier = Modifier.clickable { selectImage() }
                            )
                        }
                        ModifyAlbumTextFields(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1F)
                                .clip(RoundedCornerShape(topStart = 50f, topEnd = 50f))
                                .background(color = SoulSearchingColorTheme.colorScheme.primary)
                                .padding(Constants.Spacing.medium),
                            albumName = state.albumWithMusics.album.albumName,
                            artistName = state.albumWithMusics.artist!!.artistName,
                            focusManager = focusManager,
                            setName = {
                                modifyAlbumViewModel.handler.onEvent(
                                    ModifyAlbumEvent.SetName(
                                        it
                                    )
                                )
                            },
                            setArtist = {
                                modifyAlbumViewModel.handler.onEvent(
                                    ModifyAlbumEvent.SetArtist(
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
fun ModifyAlbumTextFields(
    modifier: Modifier,
    albumName: String,
    artistName: String,
    focusManager: FocusManager,
    setName: (String) -> Unit,
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
                value = albumName,
                onValueChange = { setName(it) },
                labelName = strings.albumName,
                focusManager = focusManager
            )
            AppTextField(
                value = artistName,
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