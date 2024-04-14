package com.github.soulsearching.modifyelement.modifymusic.presentation.composable

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
import com.github.soulsearching.domain.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.modifyelement.composable.DropdownTextField
import com.github.soulsearching.modifyelement.modifymusic.domain.ModifyMusicEvent
import com.github.soulsearching.strings.strings
import java.util.UUID

@Composable
fun ModifyMusicComposable(
    modifyMusicViewModel: ModifyMusicViewModel,
    selectedMusicId: String,
    onModifyMusic: () -> Unit,
    onCancel: () -> Unit,
    selectImage: () -> Unit
) {
    val state by modifyMusicViewModel.handler.state.collectAsState()

    var isSelectedMusicFetched by rememberSaveable {
        mutableStateOf(false)
    }

    if (!isSelectedMusicFetched) {
        modifyMusicViewModel.handler.getMusicFromId(UUID.fromString(selectedMusicId))
        isSelectedMusicFetched = true
    }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            AppHeaderBar(
                title = strings.musicInformation,
                leftAction = onCancel,
                rightIcon = Icons.Rounded.Done,
                rightAction = {
                    onModifyMusic()
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
                                bitmap = state.cover,
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
                            name = state.modifiedMusicInformation.name,
                            album = state.modifiedMusicInformation.album,
                            artist = state.modifiedMusicInformation.artist,
                            focusManager = focusManager,
                            setName = { modifyMusicViewModel.handler.onEvent(ModifyMusicEvent.SetName(it)) },
                            setAlbum = {
                                modifyMusicViewModel.handler.onEvent(
                                    ModifyMusicEvent.SetAlbum(
                                        it
                                    )
                                )
                            },
                            setArtist = {
                                modifyMusicViewModel.handler.onEvent(
                                    ModifyMusicEvent.SetArtist(
                                        it
                                    )
                                )
                            },
                            albumsNames = state.matchingAlbumsNames,
                            updateAlbumsNames = { albumSearch ->
                                modifyMusicViewModel.handler.onEvent(
                                    ModifyMusicEvent.SetMatchingAlbums(
                                        search = albumSearch
                                    )
                                )
                            },
                            artistsNames = state.matchingArtistsNames,
                            updateArtistsNames = { artistSearch ->
                                modifyMusicViewModel.handler.onEvent(
                                    ModifyMusicEvent.SetMatchingArtists(
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
                                bitmap = state.cover,
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
                            name = state.modifiedMusicInformation.name,
                            album = state.modifiedMusicInformation.album,
                            artist = state.modifiedMusicInformation.artist,
                            focusManager = focusManager,
                            setName = { modifyMusicViewModel.handler.onEvent(ModifyMusicEvent.SetName(it)) },
                            setAlbum = {
                                modifyMusicViewModel.handler.onEvent(
                                    ModifyMusicEvent.SetAlbum(
                                        it
                                    )
                                )
                            },
                            setArtist = {
                                modifyMusicViewModel.handler.onEvent(
                                    ModifyMusicEvent.SetArtist(
                                        it
                                    )
                                )
                            },
                            albumsNames = state.matchingAlbumsNames,
                            updateAlbumsNames = { albumSearch ->
                                modifyMusicViewModel.handler.onEvent(
                                    ModifyMusicEvent.SetMatchingAlbums(
                                        search = albumSearch
                                    )
                                )
                            },
                            artistsNames = state.matchingArtistsNames,
                            updateArtistsNames = { artistSearch ->
                                modifyMusicViewModel.handler.onEvent(
                                    ModifyMusicEvent.SetMatchingArtists(
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
fun ModifyMusicTextFields(
    modifier: Modifier,
    name: String,
    album: String,
    artist: String,
    focusManager: FocusManager,
    setName: (String) -> Unit,
    setAlbum: (String) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
        ) {
            AppTextField(
                value = name,
                onValueChange = { setName(it) },
                labelName = strings.musicName,
                focusManager = focusManager
            )
            DropdownTextField(
                values = albumsNames,
                value = album,
                onValueChange = {
                    setAlbum(it)
                    updateAlbumsNames(it)
                },
                focusManager = focusManager,
                labelName = strings.albumName
            )
            DropdownTextField(
                values = artistsNames,
                value = artist,
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