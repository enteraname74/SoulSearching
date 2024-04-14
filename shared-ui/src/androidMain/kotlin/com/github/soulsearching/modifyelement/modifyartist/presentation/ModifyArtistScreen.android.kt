package com.github.soulsearching.modifyelement.modifyartist.presentation

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
import com.github.soulsearching.domain.viewmodel.ModifyArtistViewModel
import com.github.soulsearching.model.utils.AndroidUtils
import com.github.soulsearching.modifyelement.modifyartist.domain.ModifyArtistEvent
import com.github.soulsearching.modifyelement.modifyartist.presentation.composable.ModifyArtistComposable

@Composable
actual fun ModifyArtistScreenView(
    modifyArtistViewModel: ModifyArtistViewModel,
    selectedArtistId: String,
    finishAction: () -> Unit,
) {
    val context = LocalContext.current

    val state by modifyArtistViewModel.handler.state.collectAsState()

    val resultImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                modifyArtistViewModel.handler.onEvent(
                    ModifyArtistEvent.SetCover(
                        AndroidUtils.getBitmapFromUri(uri as Uri, context.contentResolver)
                    )
                )
            }
        }

    val resultWriteFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _ ->
            /*
               We accept the finish action even if the user doesn't want to write the file modification on the device (it will
               just not do it then)
            */
            modifyArtistViewModel.handler.onEvent(ModifyArtistEvent.UpdateArtist)
            finishAction()
        }

    fun acceptWriteFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            val uris = mutableListOf<Uri>()

            state.artistWithMusics.musics.forEach { music ->
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
            modifyArtistViewModel.handler.onEvent(ModifyArtistEvent.UpdateArtist)
            finishAction()
        }
    }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultImageLauncher.launch(intent)
    }

    ModifyArtistComposable(
        modifyArtistViewModel = modifyArtistViewModel,
        selectedArtistId = selectedArtistId,
        finishAction = { acceptWriteFile() },
        selectImage = { selectImage() }
    )
}