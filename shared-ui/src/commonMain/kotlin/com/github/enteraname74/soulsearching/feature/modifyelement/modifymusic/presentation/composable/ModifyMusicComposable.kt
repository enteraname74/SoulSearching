package com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.composable

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
import com.github.enteraname74.soulsearching.coreui.textfield.SoulDropdownTextField
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextField
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.domain.ModifyMusicEvent
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.domain.ModifyMusicViewModel
import java.util.*

@Composable
fun ModifyMusicComposable(
    modifyMusicViewModel: ModifyMusicViewModel,
    selectedMusicId: String,
    onModifyMusic: () -> Unit,
    onCancel: () -> Unit,
    selectImage: () -> Unit
) {
    val state by modifyMusicViewModel.state.collectAsState()

    var isSelectedMusicFetched by rememberSaveable {
        mutableStateOf(false)
    }

    if (!isSelectedMusicFetched) {
        modifyMusicViewModel.getMusicFromId(UUID.fromString(selectedMusicId))
        isSelectedMusicFetched = true
    }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            SoulTopBar(
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
                            SoulBitmapImage(
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
                                .padding(UiConstants.Spacing.medium),
                            name = state.modifiedMusicInformation.name,
                            album = state.modifiedMusicInformation.album,
                            artist = state.modifiedMusicInformation.artist,
                            focusManager = focusManager,
                            setName = { modifyMusicViewModel.onEvent(ModifyMusicEvent.SetName(it)) },
                            setAlbum = {
                                modifyMusicViewModel.onEvent(
                                    ModifyMusicEvent.SetAlbum(
                                        it
                                    )
                                )
                            },
                            setArtist = {
                                modifyMusicViewModel.onEvent(
                                    ModifyMusicEvent.SetArtist(
                                        it
                                    )
                                )
                            },
                            albumsNames = state.matchingAlbumsNames,
                            updateAlbumsNames = { albumSearch ->
                                modifyMusicViewModel.onEvent(
                                    ModifyMusicEvent.SetMatchingAlbums(
                                        search = albumSearch
                                    )
                                )
                            },
                            artistsNames = state.matchingArtistsNames,
                            updateArtistsNames = { artistSearch ->
                                modifyMusicViewModel.onEvent(
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
                            SoulBitmapImage(
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
                                .padding(UiConstants.Spacing.medium),
                            name = state.modifiedMusicInformation.name,
                            album = state.modifiedMusicInformation.album,
                            artist = state.modifiedMusicInformation.artist,
                            focusManager = focusManager,
                            setName = { modifyMusicViewModel.onEvent(ModifyMusicEvent.SetName(it)) },
                            setAlbum = {
                                modifyMusicViewModel.onEvent(
                                    ModifyMusicEvent.SetAlbum(
                                        it
                                    )
                                )
                            },
                            setArtist = {
                                modifyMusicViewModel.onEvent(
                                    ModifyMusicEvent.SetArtist(
                                        it
                                    )
                                )
                            },
                            albumsNames = state.matchingAlbumsNames,
                            updateAlbumsNames = { albumSearch ->
                                modifyMusicViewModel.onEvent(
                                    ModifyMusicEvent.SetMatchingAlbums(
                                        search = albumSearch
                                    )
                                )
                            },
                            artistsNames = state.matchingArtistsNames,
                            updateArtistsNames = { artistSearch ->
                                modifyMusicViewModel.onEvent(
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
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
        ) {
            SoulTextField(
                value = name,
                onValueChange = { setName(it) },
                labelName = strings.musicName,
                focusManager = focusManager
            )
            SoulDropdownTextField(
                values = albumsNames,
                value = album,
                onValueChange = {
                    setAlbum(it)
                    updateAlbumsNames(it)
                },
                focusManager = focusManager,
                labelName = strings.albumName
            )
            SoulDropdownTextField(
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