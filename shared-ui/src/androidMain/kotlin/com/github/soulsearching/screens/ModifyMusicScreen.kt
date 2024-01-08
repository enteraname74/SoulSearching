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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.Constants
import com.github.soulsearching.R
import com.github.soulsearching.classes.utils.AndroidUtils
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.composables.AppTextField
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.theme.DynamicColor
import com.github.soulsearching.viewmodel.ModifyMusicViewModel
import java.util.UUID

@Composable
fun ModifyMusicScreen(
    modifyMusicViewModel: ModifyMusicViewModel,
    selectedMusicId: String,
    finishAction: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val resultImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                modifyMusicViewModel.onMusicEvent(
                    MusicEvent.SetCover(
                        AndroidUtils.getBitmapFromUri(uri as Uri, context.contentResolver)
                    )
                )
            }
        }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultImageLauncher.launch(intent)
    }


    var isMusicFetched by rememberSaveable {
        mutableStateOf(false)
    }

    if (!isMusicFetched) {
        modifyMusicViewModel.getMusicFromId(UUID.fromString(selectedMusicId))
        isMusicFetched = true
    }

    val musicState by modifyMusicViewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AppHeaderBar(
                title = stringResource(id = R.string.music_information),
                leftAction = finishAction,
                rightIcon = Icons.Rounded.Done,
                rightAction = {
                    modifyMusicViewModel.onMusicEvent(MusicEvent.UpdateMusic)
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
                                text = stringResource(id = R.string.album_cover),
                                color = DynamicColor.onSecondary
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
                                .background(color = DynamicColor.primary)
                                .padding(Constants.Spacing.medium),
                            state = musicState,
                            focusManager = focusManager,
                            setName = { modifyMusicViewModel.onMusicEvent(MusicEvent.SetName(it)) },
                            setAlbum = {
                                modifyMusicViewModel.onMusicEvent(
                                    MusicEvent.SetAlbum(
                                        it
                                    )
                                )
                            },
                            setArtist = {
                                modifyMusicViewModel.onMusicEvent(
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
                                text = stringResource(id = R.string.album_cover),
                                color = DynamicColor.onSecondary
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
                                .background(color = DynamicColor.primary)
                                .padding(Constants.Spacing.medium),
                            state = musicState,
                            focusManager = focusManager,
                            setName = { modifyMusicViewModel.onMusicEvent(MusicEvent.SetName(it)) },
                            setAlbum = {
                                modifyMusicViewModel.onMusicEvent(
                                    MusicEvent.SetAlbum(
                                        it
                                    )
                                )
                            },
                            setArtist = {
                                modifyMusicViewModel.onMusicEvent(
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
                labelName = stringResource(id = R.string.music_name),
                focusManager = focusManager
            )
            AppTextField(
                value = state.album,
                onValueChange = { setAlbum(it) },
                labelName = stringResource(id = R.string.album_name),
                focusManager = focusManager
            )
            AppTextField(
                value = state.artist,
                onValueChange = { setArtist(it) },
                labelName = stringResource(id = R.string.artist_name),
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