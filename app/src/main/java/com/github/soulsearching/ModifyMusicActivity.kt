package com.github.soulsearching

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soulsearching.composables.AppHeaderBar
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.viewModels.ModifyMusicViewModel
import java.util.*

class ModifyMusicActivity : ComponentActivity() {
    private val viewModel : ModifyMusicViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var musicId : UUID? by rememberSaveable {
                mutableStateOf(null)
            }

            if (musicId == null) {
                musicId = UUID.fromString(intent.extras?.getString("musicId"))
                viewModel.getMusicFromId(musicId as UUID)
            }

            val state = viewModel.state.collectAsState()

            SoulSearchingTheme {
                Scaffold(
                    topBar = {
                        AppHeaderBar(
                            title = stringResource(id = R.string.music_information),
                            leftAction = { finish() },
                            topRightIcon = Icons.Default.Done
                        )
                    },
                    content = { padding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(padding)
                                .padding(top = Constants.Spacing.medium)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(Constants.Spacing.medium),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    modifier = Modifier.padding(bottom = Constants.Spacing.medium),
                                    text = stringResource(id = R.string.album_cover),
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                AppImage(bitmap = null, size = 200.dp)
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1F)
                                    .clip(RoundedCornerShape(topStart = 30f, topEnd = 30f))
                                    .background(color = MaterialTheme.colorScheme.primary)
                                    .padding(Constants.Spacing.medium)
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
                                        value = state.value.name,
                                        onValueChange = {},
                                        label = { Text(text = stringResource(id = R.string.music_name)) },
                                        singleLine = true,
                                        colors = TextFieldDefaults.textFieldColors(
                                            containerColor = Color.Transparent,
                                            textColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                    TextField(
                                        value = state.value.album,
                                        onValueChange = {},
                                        label = { Text(text = stringResource(id = R.string.music_album_name)) },
                                        singleLine = true
                                    )
                                    TextField(
                                        value = state.value.artist,
                                        onValueChange = {},
                                        label = { Text(text = stringResource(id = R.string.music_artist_name)) },
                                        singleLine = true
                                    )
                                }
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1F)
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}