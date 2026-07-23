package com.github.enteraname74.soulsearching

import android.annotation.SuppressLint
import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import coil3.annotation.ExperimentalCoilApi
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.feature.appinit.MissingPermissionsComposable
import com.github.enteraname74.soulsearching.feature.application.ApplicationViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jaudiotagger.tag.TagOptionSingleton
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    // Main page view models
    private val mainPageViewModel: MainPageViewModel by viewModel()
    private val applicationViewModel: ApplicationViewModel by viewModel()
    private val playbackManager: PlaybackManager by inject()
    private val workDispatcher: WorkDispatcher by inject()

    @OptIn(ExperimentalCoilApi::class)
    @SuppressLint("CoroutineCreationDuringComposition", "UnspecifiedRegisterReceiverFlag")
    override
    fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // For JAudiotagger to work on android.
        TagOptionSingleton.getInstance().isAndroid = true

        setContent {
            applicationViewModel.isReadPermissionGranted =
                SoulSearchingContext.checkIfReadPermissionGranted()
            applicationViewModel.isPostNotificationGranted =
                SoulSearchingContext.checkIfPostNotificationGranted()

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
            } else {
                SoulSearchingApplication()
            }
        }

        if (savedInstanceState == null) {
            handleIncomingIntent(intent)
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
            CoroutineScope(workDispatcher.dispatcher).launch {
                playbackManager.stopPlayback(resetPlayedList = false)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            val uri: Uri? = intent.data
            if (uri != null) {
                applicationViewModel.handleMusicLink(uri.toString())
            }
        }

        intent.data = null
    }
}
