package com.github.soulsearching.screens

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.Utils
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.composables.AppTextField
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.ModifyPlaylistViewModel
import java.util.*

@Composable
fun ModifyPlaylistScreen(
    modifyPlaylistViewModel: ModifyPlaylistViewModel,
    selectedPlaylistId: String,
    finishAction: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val resultImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                modifyPlaylistViewModel.onPlaylistEvent(
                    PlaylistEvent.SetCover(
                        Utils.getBitmapFromUri(uri as Uri, context.contentResolver)
                    )
                )
            }
        }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultImageLauncher.launch(intent)
    }

    var isPlaylistFetched by rememberSaveable {
        mutableStateOf(false)
    }

    if (!isPlaylistFetched) {
        modifyPlaylistViewModel.onPlaylistEvent(
            PlaylistEvent.PlaylistFromId(
                playlistId = UUID.fromString(selectedPlaylistId)
            )
        )
        isPlaylistFetched = true
    }

    val state by modifyPlaylistViewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AppHeaderBar(
                title = stringResource(id = R.string.playlist_information),
                leftAction = finishAction,
                rightIcon = Icons.Default.Done,
                rightAction = {
                    modifyPlaylistViewModel.onPlaylistEvent(PlaylistEvent.UpdatePlaylist)
                    finishAction()
                }
            )
        },
        content = { padding ->
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DynamicColor.secondary)
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
                                text = stringResource(id = R.string.playlist_cover),
                                color = DynamicColor.onSecondary
                            )
                            AppImage(
                                bitmap = state.cover,
                                size = 200.dp,
                                modifier = Modifier.clickable { selectImage() }
                            )
                        }
                        ModifyPlaylistTextFields(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(2F)
                                .background(color = DynamicColor.primary)
                                .padding(Constants.Spacing.medium),
                            state = state,
                            focusManager = focusManager,
                            setName = {
                                modifyPlaylistViewModel.onPlaylistEvent(PlaylistEvent.SetName(it))
                            }
                        )
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DynamicColor.secondary)
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
                                text = stringResource(id = R.string.playlist_cover),
                                color = DynamicColor.onSecondary
                            )
                            AppImage(
                                bitmap = state.cover,
                                size = 200.dp,
                                modifier = Modifier.clickable { selectImage() }
                            )
                        }
                        ModifyPlaylistTextFields(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1F)
                                .clip(RoundedCornerShape(topStart = 50f, topEnd = 50f))
                                .background(color = DynamicColor.primary)
                                .padding(Constants.Spacing.medium),
                            state = state,
                            focusManager = focusManager,
                            setName = {
                                modifyPlaylistViewModel.onPlaylistEvent(PlaylistEvent.SetName(it))
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
    state: PlaylistState,
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
                value = state.name,
                onValueChange = { setName(it) },
                labelName = stringResource(id = R.string.playlist_name),
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