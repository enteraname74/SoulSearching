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
import com.github.soulsearching.classes.utils.AndroidUtils
import com.github.soulsearching.di.injectElement
import com.github.soulsearching.playback.PlaybackManagerAndroidImpl
import com.github.soulsearching.playback.PlayerService
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.ui.theme.SoulSearchingTheme
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.viewmodel.AllAlbumsViewModel
import com.github.soulsearching.viewmodel.AllAlbumsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllArtistsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllImageCoversViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllMusicsViewModel
import com.github.soulsearching.viewmodel.AllMusicsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllPlaylistsViewModel
import com.github.soulsearching.viewmodel.AllPlaylistsViewModelAndroidImpl
import com.github.soulsearching.viewmodel.AllQuickAccessViewModelAndroidImpl
import com.github.soulsearching.viewmodel.MainActivityViewModel
import com.github.soulsearching.viewmodel.MainActivityViewModelAndroidImpl
import com.github.soulsearching.viewmodel.PlayerMusicListViewModelAndroidImpl
import com.github.soulsearching.viewmodel.PlayerViewModel
import com.github.soulsearching.viewmodel.PlayerViewModelAndroidImpl
import com.github.soulsearching.viewmodel.handler.AllMusicsViewModelHandler
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

class MainActivity : AppCompatActivity() {
    // Main page view models
    private lateinit var allMusicsViewModel: AllMusicsViewModel

    private val colorThemeManager: ColorThemeManager by inject<ColorThemeManager>()

    private val serviceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("MAIN ACTIVITY", "BROADCAST RECEIVE INFO TO RELAUNCH SERVICE")
            AndroidUtils.launchService(
                context = context,
                isFromSavedList = false
            )
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

        setContent {
            val a: AllPlaylistsViewModel by inject<AllPlaylistsViewModel>()
            SoulSearchingColorTheme.colorScheme = colorThemeManager.getColorTheme()

            // Main page view models
            allMusicsViewModel = koinInject()

            // Player view model :
            val playerViewModel = injectElement<PlayerViewModel>()

            val mainActivityViewModel: MainActivityViewModel = injectElement()
            mainActivityViewModel.handler.isReadPermissionGranted =
                SoulSearchingContext.checkIfReadPermissionGranted()
            mainActivityViewModel.handler.isPostNotificationGranted =
                SoulSearchingContext.checkIfPostNotificationGranted()

            val playbackManager = injectElement<PlaybackManagerAndroidImpl>()
            PlayerUtils.playerViewModel = playerViewModel
            initializeBroadcastReceive()

            SoulSearchingTheme {
                val readPermissionLauncher = permissionLauncher { isGranted ->
                    mainActivityViewModel.handler.isReadPermissionGranted = isGranted
                }

                val postNotificationLauncher = permissionLauncher { isGranted ->
                    mainActivityViewModel.handler.isPostNotificationGranted = isGranted
                }

                if (!mainActivityViewModel.handler.isReadPermissionGranted || !mainActivityViewModel.handler.isPostNotificationGranted) {
                    SideEffect {
                        checkAndAskMissingPermissions(
                            isReadPermissionGranted = mainActivityViewModel.handler.isReadPermissionGranted,
                            isPostNotificationGranted = mainActivityViewModel.handler.isPostNotificationGranted,
                            readPermissionLauncher = readPermissionLauncher,
                            postNotificationLauncher = postNotificationLauncher
                        )
                    }
                }


                // launch the service for the playback if its needed.
                if (PlayerUtils.playerViewModel.handler.shouldServiceBeLaunched && !PlayerUtils.playerViewModel.handler.isServiceLaunched) {
                    AndroidUtils.launchService(
                        context = this@MainActivity,
                        isFromSavedList = false
                    )
                }


                SoulSearchingApplication(
                    playbackManager = playbackManager,
//                    allAlbumsViewModel = injectElement(),
//                    allArtistsViewModel = injectElement(),
//                    allImageCoversViewModel = injectElement(),
//                    allMusicsViewModel = allMusicsViewModel,
//                    allPlaylistsViewModel = injectElement(),
//                    playerMusicListViewModel = injectElement(),
//                    allQuickAccessViewModel = injectElement(),
//                    mainActivityViewModel = mainActivityViewModel
                )
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