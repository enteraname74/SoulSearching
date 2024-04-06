package com.github.soulsearching.modifyelement.modifyplaylist.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.soulsearching.modifyelement.modifyplaylist.presentation.composable.ModifyPlaylistComposable
import com.github.soulsearching.domain.events.PlaylistEvent
import com.github.soulsearching.domain.viewmodel.ModifyPlaylistViewModel
import com.github.soulsearching.model.utils.AndroidUtils

@Composable
actual fun ModifyPlaylistScreenView(
    modifyPlaylistViewModel: ModifyPlaylistViewModel,
    selectedPlaylistId: String,
    finishAction: () -> Unit
) {
    val context = LocalContext.current

    val resultImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                modifyPlaylistViewModel.handler.onPlaylistEvent(
                    PlaylistEvent.SetCover(
                        AndroidUtils.getBitmapFromUri(uri as Uri, context.contentResolver)
                    )
                )
            }
        }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultImageLauncher.launch(intent)
    }

    ModifyPlaylistComposable(
        modifyPlaylistViewModel = modifyPlaylistViewModel,
        selectedPlaylistId = selectedPlaylistId,
        finishAction = finishAction,
        selectImage = { selectImage() }
    )
}