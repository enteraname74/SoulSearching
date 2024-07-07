package com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.presentation.composable

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
import com.github.enteraname74.soulsearching.domain.viewmodel.ModifyArtistViewModel
import com.github.enteraname74.soulsearching.coreui.textfield.SoulDropdownTextField
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.domain.ModifyArtistEvent
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import java.util.UUID

@Composable
fun ModifyArtistComposable(
    modifyArtistViewModel: ModifyArtistViewModel,
    selectedArtistId: String,
    onModifyArtist: () -> Unit,
    onCancel: () -> Unit,
    selectImage: () -> Unit
) {
    val state by modifyArtistViewModel.handler.state.collectAsState()

    var isSelectedAlbumFetched by rememberSaveable {
        mutableStateOf(false)
    }

    if (!isSelectedAlbumFetched) {
        modifyArtistViewModel.handler.onEvent(
            ModifyArtistEvent.ArtistFromId(
                artistId = UUID.fromString(selectedArtistId)
            )
        )
        isSelectedAlbumFetched = true
    }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            SoulTopBar(
                title = strings.artistInformation,
                leftAction = onCancel,
                rightIcon = Icons.Rounded.Done,
                rightAction = {
                    onModifyArtist()
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
                                text = strings.artistCover,
                                color = SoulSearchingColorTheme.colorScheme.onSecondary
                            )
                            SoulImage(
                                bitmap = state.cover,
                                size = 200.dp,
                                modifier = Modifier.clickable { selectImage() }
                            )
                        }
                        ModifyArtistTextFields(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(2F)
                                .background(color = SoulSearchingColorTheme.colorScheme.primary)
                                .padding(UiConstants.Spacing.medium),
                            artistName = state.artistWithMusics.artist.artistName,
                            focusManager = focusManager,
                            setName = {
                                modifyArtistViewModel.handler.onEvent(
                                    ModifyArtistEvent.SetName(
                                        it
                                    )
                                )
                            },
                            artistsNames = state.matchingArtistsNames,
                            updateArtistsNames = { artistSearch ->
                                modifyArtistViewModel.handler.onEvent(
                                    ModifyArtistEvent.SetMatchingArtists(
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
                                text = strings.artistCover,
                                color = SoulSearchingColorTheme.colorScheme.onSecondary
                            )
                            SoulImage(
                                bitmap = state.cover,
                                size = 200.dp,
                                modifier = Modifier.clickable { selectImage() }
                            )
                        }
                        ModifyArtistTextFields(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1F)
                                .clip(RoundedCornerShape(topStart = 50f, topEnd = 50f))
                                .background(color = SoulSearchingColorTheme.colorScheme.primary)
                                .padding(UiConstants.Spacing.medium),
                            artistName = state.artistWithMusics.artist.artistName,
                            focusManager = focusManager,
                            setName = {
                                modifyArtistViewModel.handler.onEvent(
                                    ModifyArtistEvent.SetName(
                                        it
                                    )
                                )
                            },
                            artistsNames = state.matchingArtistsNames,
                            updateArtistsNames = { artistSearch ->
                                modifyArtistViewModel.handler.onEvent(
                                    ModifyArtistEvent.SetMatchingArtists(
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
fun ModifyArtistTextFields(
    modifier: Modifier,
    artistName: String,
    focusManager: FocusManager,
    setName: (String) -> Unit,
    artistsNames: List<String>,
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
            SoulDropdownTextField(
                values = artistsNames,
                value = artistName,
                onValueChange = {
                    setName(it)
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