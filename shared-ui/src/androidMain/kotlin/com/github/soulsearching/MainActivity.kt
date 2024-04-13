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
import androidx.compose.runtime.SideEffect
import com.github.enteraname74.domain.domainModule
import com.github.enteraname74.localdb.localAndroidModule
import com.github.soulsearching.appinit.presentation.MissingPermissionsComposable
import com.github.soulsearching.colortheme.domain.model.ColorThemeManager
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.domain.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.domain.viewmodel.AllArtistsViewModel
import com.github.soulsearching.domain.viewmodel.AllImageCoversViewModel
import com.github.soulsearching.domain.viewmodel.AllMusicsViewModel
import com.github.soulsearching.domain.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.domain.viewmodel.MainActivityViewModel
import com.github.soulsearching.domain.viewmodel.PlayerViewModel
import com.github.soulsearching.model.playback.PlayerService
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import org.jaudiotagger.tag.TagOptionSingleton
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

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
    private val playbackManager: PlaybackManager by inject()


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

        // For JAudiotagger to work on android.
        TagOptionSingleton.getInstance().isAndroid = true

        val playbackManager by inject<PlaybackManager>()

        settings.initializeSorts(
            onMusicEvent = allMusicsViewModel.handler::onMusicEvent,
            onPlaylistEvent = allPlaylistsViewModel.handler::onPlaylistEvent,
            onArtistEvent = allArtistsViewModel.handler::onArtistEvent,
            onAlbumEvent = allAlbumsViewModel.handler::onAlbumEvent
        )

        with(playbackManager) {
            retrieveCoverMethod = allImageCoversViewModel.handler::getImageCover
        }
        initializeBroadcastReceive()

        setContent {
            SoulSearchingColorTheme.colorScheme = colorThemeManager.getColorTheme()
            mainActivityViewModel.handler.isReadPermissionGranted =
                SoulSearchingContext.checkIfReadPermissionGranted()
            mainActivityViewModel.handler.isPostNotificationGranted =
                SoulSearchingContext.checkIfPostNotificationGranted()

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

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            playbackManager.stopPlayback(resetPlayedList = false)
            unloadKoinModules(listOf(domainModule, appModule, localAndroidModule, commonModule))
            loadKoinModules(listOf(domainModule, appModule, localAndroidModule, commonModule))
        }
    }
}