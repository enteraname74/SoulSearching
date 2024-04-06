package com.github.soulsearching

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.composables.MissingPermissionsComposable
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.model.playback.PlayerService
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.PlayerMode
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.utils.ColorPaletteUtils
import com.github.soulsearching.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.viewmodel.AllArtistsViewModel
import com.github.soulsearching.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.viewmodel.AllMusicsViewModel
import com.github.soulsearching.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.viewmodel.MainActivityViewModel
import com.github.soulsearching.viewmodel.PlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject

class MainActivity : AppCompatActivity() {
    // Main page view models
    private val allMusicsViewModel: AllMusicsViewModel by inject()
    private val allPlaylistsViewModel: AllPlaylistsViewModel by inject()
    private val allAlbumsViewModel: AllAlbumsViewModel by inject()
    private val allArtistsViewModel: AllArtistsViewModel by inject()
    private val allImageCoversViewModel: AllImageCoversViewModel by inject()
    private val colorThemeManager: ColorThemeManager by inject()
    private val settings: SoulSearchingSettings by inject()
    private val mainActivityViewModel: MainActivityViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()


    private val serviceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("MAIN ACTIVITY", "BROADCAST RECEIVE INFO TO RELAUNCH SERVICE")
//            AndroidUtils.launchService(
//                context = context,
//                isFromSavedList = false
//            )
        }
    }

    /**
     * Initialize the broadcast receiver, used by the foreground service handling the playback.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun initializeBroadcastReceive() {
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(
                serviceReceiver, IntentFilter(PlayerService.RESTART_SERVICE),
                RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(serviceReceiver, IntentFilter(PlayerService.RESTART_SERVICE))
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition", "UnspecifiedRegisterReceiverFlag")
    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playbackManager by inject<PlaybackManager>()
        playbackManager.setCallback(callback = object : PlaybackManager.Companion.Callback {
            override fun onPlayedListUpdated(playedList: List<Music>) {
                super.onPlayedListUpdated(playedList)
                playerViewModel.handler.currentPlaylist = playedList
            }

            override fun onPlayerModeChanged(playerMode: PlayerMode) {
                super.onPlayerModeChanged(playerMode)
                playerViewModel.handler.playerMode = playerMode
            }

            override fun onCurrentPlayedMusicChanged(music: Music?) {
                super.onCurrentPlayedMusicChanged(music)
                playerViewModel.handler.currentMusic = music
            }

            override fun onCurrentMusicPositionChanged(position: Int) {
                super.onCurrentMusicPositionChanged(position)
                playerViewModel.handler.currentMusicPosition = position
            }

            override fun onPlayingStateChanged(isPlaying: Boolean) {
                super.onPlayingStateChanged(isPlaying)
                playerViewModel.handler.isPlaying = isPlaying
            }

            override fun onCurrentMusicCoverChanged(cover: ImageBitmap?) {
                super.onCurrentMusicCoverChanged(cover)
                println("New cover to set: $cover")
                if (playerViewModel.handler.currentMusicCover?.equals(cover) == true) return

                playerViewModel.handler.currentMusicCover = cover
                colorThemeManager.currentColorPalette = ColorPaletteUtils.getPaletteFromAlbumArt(
                    image = cover
                )
            }
        })

        settings.initializeSorts(
            onMusicEvent = allMusicsViewModel.handler::onMusicEvent,
            onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
            onArtistEvent = allArtistsViewModel.handler::onArtistEvent,
            onAlbumEvent = allAlbumsViewModel.handler::onAlbumEvent
        )

        with(playbackManager) {
            retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover
            updateNbPlayed = { allMusicsViewModel.handler.onMusicEvent(MusicEvent.AddNbPlayed(it)) }
        }

        setContent {
            SoulSearchingColorTheme.colorScheme = colorThemeManager.getColorTheme()
            mainActivityViewModel.handler.isReadPermissionGranted =
                SoulSearchingContext.checkIfReadPermissionGranted()
            mainActivityViewModel.handler.isPostNotificationGranted =
                SoulSearchingContext.checkIfPostNotificationGranted()

            initializeBroadcastReceive()

            SoulSearchingTheme {
                val readPermissionLauncher = permissionLauncher { isGranted ->
                    mainActivityViewModel.handler.isReadPermissionGranted = isGranted
                }

                val postNotificationLauncher = permissionLauncher { isGranted ->
                    mainActivityViewModel.handler.isPostNotificationGranted = isGranted
                }

                if (!mainActivityViewModel.handler.isReadPermissionGranted || !mainActivityViewModel.handler.isPostNotificationGranted) {
                    MissingPermissionsComposable()
                    SideEffect {
                        checkAndAskMissingPermissions(
                            isReadPermissionGranted = mainActivityViewModel.handler.isReadPermissionGranted,
                            isPostNotificationGranted = mainActivityViewModel.handler.isPostNotificationGranted,
                            readPermissionLauncher = readPermissionLauncher,
                            postNotificationLauncher = postNotificationLauncher
                        )
                    }
                    return@SoulSearchingTheme
                }

                SoulSearchingApplication()
            }
        }
    }

    /**
     * Build a permission launcher.
     */
    @Composable
    private fun permissionLauncher(
        onResult: (Boolean) -> Unit
    ): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            onResult(isGranted)
        }
    }

    /**
     * Check and ask for missing permissions.
     */
    private fun checkAndAskMissingPermissions(
        isReadPermissionGranted: Boolean,
        isPostNotificationGranted: Boolean,
        readPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
        postNotificationLauncher: ManagedActivityResultLauncher<String, Boolean>
    ) {
        if (!isReadPermissionGranted) {
            readPermissionLauncher.launch(
                if (Build.VERSION.SDK_INT >= 33) {
                    android.Manifest.permission.READ_MEDIA_AUDIO
                } else {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                }
            )
        }

        if (!isPostNotificationGranted && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)) {
            postNotificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            allMusicsViewModel.handler.checkAndDeleteMusicIfNotExist()
        } catch (_: RuntimeException) {

        }
    }
}