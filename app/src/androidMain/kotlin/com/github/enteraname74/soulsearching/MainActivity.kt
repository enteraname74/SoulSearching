package com.github.enteraname74.soulsearching

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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import coil3.annotation.ExperimentalCoilApi
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.di.appModule
import com.github.enteraname74.soulsearching.feature.appinit.MissingPermissionsComposable
import com.github.enteraname74.soulsearching.feature.application.ApplicationViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.ui.theme.SoulSearchingTheme
import kotlinx.coroutines.runBlocking
import org.jaudiotagger.tag.TagOptionSingleton
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class MainActivity : AppCompatActivity() {
    // Main page view models
    private val mainPageViewModel: MainPageViewModel by viewModel()
    private val applicationViewModel: ApplicationViewModel by viewModel()
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
                serviceReceiver, IntentFilter(com.github.enteraname74.soulsearching.features.playback.PlayerService.RESTART_SERVICE),
                RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(serviceReceiver, IntentFilter(com.github.enteraname74.soulsearching.features.playback.PlayerService.RESTART_SERVICE))
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    @SuppressLint("CoroutineCreationDuringComposition", "UnspecifiedRegisterReceiverFlag")
    override
    fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // For JAudiotagger to work on android.
        TagOptionSingleton.getInstance().isAndroid = true
        initializeBroadcastReceive()

        setContent {
            applicationViewModel.isReadPermissionGranted =
                SoulSearchingContext.checkIfReadPermissionGranted()
            applicationViewModel.isPostNotificationGranted =
                SoulSearchingContext.checkIfPostNotificationGranted()

            SoulSearchingTheme {
                val readPermissionLauncher = permissionLauncher { isGranted ->
                    applicationViewModel.isReadPermissionGranted = isGranted
                }

                val postNotificationLauncher = permissionLauncher { isGranted ->
                    applicationViewModel.isPostNotificationGranted = isGranted
                }

                if (
                    !applicationViewModel.isReadPermissionGranted ||
                    !applicationViewModel.isPostNotificationGranted
                ) {
                    MissingPermissionsComposable()
                    SideEffect {
                        checkAndAskMissingPermissions(
                            isReadPermissionGranted = applicationViewModel.isReadPermissionGranted,
                            isPostNotificationGranted = applicationViewModel.isPostNotificationGranted,
                            readPermissionLauncher = readPermissionLauncher,
                            postNotificationLauncher = postNotificationLauncher,
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
        postNotificationLauncher: ManagedActivityResultLauncher<String, Boolean>,
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
            mainPageViewModel.checkAndDeleteMusicIfNotExist()
        } catch (_: RuntimeException) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            // TODO maybe not useful anymore?
            unloadKoinModules(appModule)
            loadKoinModules(appModule)
        }
    }
}