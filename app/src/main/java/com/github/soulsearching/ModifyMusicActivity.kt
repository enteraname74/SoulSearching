package com.github.soulsearching

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.classes.Utils
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.viewModels.ModifyMusicViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ModifyMusicActivity : ComponentActivity() {
    private val viewModel: ModifyMusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val configuration = LocalConfiguration.current
            val focusManager = LocalFocusManager.current

            var musicId: UUID? by rememberSaveable {
                mutableStateOf(null)
            }

            if (musicId == null) {
                musicId = UUID.fromString(intent.extras?.getString("musicId"))
                viewModel.getMusicFromId(musicId as UUID)
            }

            val state by viewModel.state.collectAsState()

            SoulSearchingTheme {
                Scaffold(
                    topBar = {
                        AppHeaderBar(
                            title = stringResource(id = R.string.music_information),
                            leftAction = { finish() },
                            rightIcon = Icons.Default.Done,
                            rightAction = {
                                viewModel.onMusicEvent(MusicEvent.UpdateMusic)
                                finish()
                            }
                        )
                    },
                    content = { padding ->
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_LANDSCAPE -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.secondary)
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
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                        AppImage(
                                            bitmap = state.cover,
                                            size = 200.dp,
                                            modifier = Modifier.clickable { selectImage() }
                                        )
                                    }
                                    TextFields(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .weight(2F)
                                            .background(color = MaterialTheme.colorScheme.primary)
                                            .padding(Constants.Spacing.medium),
                                        state = state,
                                        focusManager = focusManager
                                    )
                                }
                            }
                            else -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.secondary)
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
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                        AppImage(
                                            bitmap = state.cover,
                                            size = 200.dp,
                                            modifier = Modifier.clickable { selectImage() }
                                        )
                                    }
                                    TextFields(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .weight(1F)
                                            .clip(RoundedCornerShape(topStart = 50f, topEnd = 50f))
                                            .background(color = MaterialTheme.colorScheme.primary)
                                            .padding(Constants.Spacing.medium),
                                        state = state,
                                        focusManager = focusManager
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun TextFields(
        modifier: Modifier,
        state: MusicState,
        focusManager: FocusManager
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
                    .weight(3F),
                verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium)
            ) {
                TextField(
                    value = state.name,
                    onValueChange = { viewModel.onMusicEvent(MusicEvent.SetName(it)) },
                    label = { Text(text = stringResource(id = R.string.music_name)) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        focusedContainerColor = Color.Transparent
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )
                TextField(
                    value = state.album,
                    onValueChange = { viewModel.onMusicEvent(MusicEvent.SetAlbum(it)) },
                    label = { Text(text = stringResource(id = R.string.album_name)) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        focusedContainerColor = Color.Transparent
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )
                TextField(
                    value = state.artist,
                    onValueChange = { viewModel.onMusicEvent(MusicEvent.SetArtist(it)) },
                    label = { Text(text = stringResource(id = R.string.artist_name)) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        focusedContainerColor = Color.Transparent
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F)
            )
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultImageLauncher.launch(intent)
    }

    private var resultImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                viewModel.onMusicEvent(
                    MusicEvent.SetCover(
                        Utils.getBitmapFromUri(uri as Uri, contentResolver)
                    )
                )
            }
        }
}