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
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.ScreenOrientation
import com.github.soulsearching.domain.viewmodel.ModifyAlbumViewModel
import com.github.soulsearching.modifyelement.composable.DropdownTextField
import com.github.soulsearching.modifyelement.modifyalbum.domain.ModifyAlbumEvent
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import java.util.UUID

@Composable
fun ModifyAlbumComposable(
    modifyAlbumViewModel: ModifyAlbumViewModel,
    selectedAlbumId: String,
    onModifyAlbum: () -> Unit,
    onCancel: () -> Unit,
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
            SoulTopBar(
                title = strings.albumInformation,
                leftAction = onCancel,
                rightIcon = Icons.Rounded.Done,
                rightAction = {
                    onModifyAlbum()
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
                                text = strings.albumCover,
                                color = SoulSearchingColorTheme.colorScheme.onSecondary
                            )
                            SoulImage(
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
                                .padding(UiConstants.Spacing.medium),
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
                            },
                            albumsNames = state.matchingAlbumsNames,
                            updateAlbumsNames = { albumSearch ->
                                modifyAlbumViewModel.handler.onEvent(
                                    ModifyAlbumEvent.SetMatchingAlbums(
                                        search = albumSearch
                                    )
                                )
                            },
                            artistsNames = state.matchingArtistsNames,
                            updateArtistsNames = { artistSearch ->
                                modifyAlbumViewModel.handler.onEvent(
                                    ModifyAlbumEvent.SetMatchingArtists(
                                        search = artistSearch
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
                                text = strings.albumCover,
                                color = SoulSearchingColorTheme.colorScheme.onSecondary
                            )
                            SoulImage(
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
                                .padding(UiConstants.Spacing.medium),
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
                            },
                            albumsNames = state.matchingAlbumsNames,
                            updateAlbumsNames = { albumSearch ->
                                modifyAlbumViewModel.handler.onEvent(
                                    ModifyAlbumEvent.SetMatchingAlbums(
                                        search = albumSearch
                                    )
                                )
                            },
                            artistsNames = state.matchingArtistsNames,
                            updateArtistsNames = { artistSearch ->
                                modifyAlbumViewModel.handler.onEvent(
                                    ModifyAlbumEvent.SetMatchingArtists(
                                        search = artistSearch
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
    setArtist: (String) -> Unit,
    albumsNames: List<String>,
    artistsNames: List<String>,
    updateAlbumsNames: (String) -> Unit,
    updateArtistsNames: (String) -> Unit,
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
            DropdownTextField(
                values = albumsNames,
                value = albumName,
                onValueChange = {
                    setName(it)
                    updateAlbumsNames(it)
                },
                focusManager = focusManager,
                labelName = strings.albumName
            )
            DropdownTextField(
                values = artistsNames,
                value = artistName,
                onValueChange = {
                    setArtist(it)
                    updateArtistsNames(it)
                },
                focusManager = focusManager,
                labelName = strings.artistName
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        )
    }
}