package com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.github.enteraname74.soulsearching.model.utils.AndroidUtils
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.domain.ModifyAlbumEvent
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.presentation.composable.ModifyAlbumComposable
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.modifyelement.modifyalbum.domain.ModifyAlbumViewModel

@Composable
actual fun ModifyAlbumScreenView(
    modifyAlbumViewModel: ModifyAlbumViewModel,
    selectedAlbumId: String,
    finishAction: () -> Unit,
    viewSettingsManager: ViewSettingsManager
) {
    val context = LocalContext.current

    val state by modifyAlbumViewModel.state.collectAsState()

    val resultWriteFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _ ->
            /*
               We accept the finish action even if the user doesn't want to write the file modification on the device (it will
               just not do it then)
            */
            modifyAlbumViewModel.onEvent(ModifyAlbumEvent.UpdateAlbum)
            finishAction()
        }

    val resultImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                modifyAlbumViewModel.onEvent(
                    ModifyAlbumEvent.SetCover(
                        AndroidUtils.getBitmapFromUri(uri as Uri, context.contentResolver)
                    )
                )
            }
        }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultImageLauncher.launch(intent)
    }

    fun acceptWriteFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && viewSettingsManager.isMusicFileModificationOn) {
            val uris = mutableListOf<Uri>()

            state.albumWithMusics.musics.forEach { music ->
                val mediaId = AndroidUtils.musicPathToMediaId(
                    context = context,
                    musicPath = music.path
                )
                uris.add(
                    ContentUris.withAppendedId(
                        MediaStore.Audio.Media.getContentUri("external"),
                        mediaId
                    )
                )
            }

            val intent = MediaStore.createWriteRequest(
                context.contentResolver,
                uris
            )
            val intentSenderRequest = IntentSenderRequest.Builder(intent.intentSender).build()
            resultWriteFileLauncher.launch(intentSenderRequest)
        } else {
            modifyAlbumViewModel.onEvent(ModifyAlbumEvent.UpdateAlbum)
            finishAction()
        }
    }

    ModifyAlbumComposable(
        modifyAlbumViewModel = modifyAlbumViewModel,
        selectedAlbumId = selectedAlbumId,
        onModifyAlbum = { acceptWriteFile() },
        selectImage = { selectImage() },
        onCancel = finishAction
    )
}