package com.github.soulsearching.modifyelement.modifyartist.presentation.composable

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
import com.github.soulsearching.domain.events.ArtistEvent
import com.github.soulsearching.elementpage.artistpage.domain.SelectedArtistState
import com.github.soulsearching.strings.strings
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.composables.AppTextField
import com.github.soulsearching.domain.model.types.ScreenOrientation
import com.github.soulsearching.domain.viewmodel.ModifyArtistViewModel
import java.util.UUID

@Composable
fun ModifyArtistComposable(
    modifyArtistViewModel: ModifyArtistViewModel,
    selectedArtistId: String,
    finishAction: () -> Unit,
    selectImage: () -> Unit
) {
    var isArtistFetched by rememberSaveable {
        mutableStateOf(false)
    }

    if (!isArtistFetched) {
        modifyArtistViewModel.handler.onArtistEvent(
            ArtistEvent.ArtistFromId(
                artistId = UUID.fromString(selectedArtistId)
            )
        )
        isArtistFetched = true
    }

    val focusManager = LocalFocusManager.current
    val artistState by modifyArtistViewModel.handler.state.collectAsState()

    Scaffold(
        topBar = {
            AppHeaderBar(
                title = strings.artistInformation,
                leftAction = finishAction,
                rightIcon = Icons.Rounded.Done,
                rightAction = {
                    modifyArtistViewModel.handler.onArtistEvent(ArtistEvent.UpdateArtist)
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
                                text = strings.artistCover,
                                color = SoulSearchingColorTheme.colorScheme.onSecondary
                            )
                            AppImage(
                                bitmap = artistState.cover,
                                size = 200.dp,
                                modifier = Modifier.clickable { selectImage() }
                            )
                        }
                        ModifyArtistTextFields(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(2F)
                                .background(color = SoulSearchingColorTheme.colorScheme.primary)
                                .padding(Constants.Spacing.medium),
                            selectedArtistState = artistState,
                            focusManager = focusManager,
                            setName = {
                                modifyArtistViewModel.handler.onArtistEvent(
                                    ArtistEvent.SetName(
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
                                text = strings.artistCover,
                                color = SoulSearchingColorTheme.colorScheme.onSecondary
                            )
                            AppImage(
                                bitmap = artistState.cover,
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
                                .padding(Constants.Spacing.medium),
                            selectedArtistState = artistState,
                            focusManager = focusManager,
                            setName = {
                                modifyArtistViewModel.handler.onArtistEvent(
                                    ArtistEvent.SetName(
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
fun ModifyArtistTextFields(
    modifier: Modifier,
    selectedArtistState: SelectedArtistState,
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
            verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
        ) {
            AppTextField(
                value = selectedArtistState.artistWithMusics.artist.artistName,
                onValueChange = { setName(it) },
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