package com.github.soulsearching.modifyelement.modifyalbum.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.soulsearching.domain.viewmodel.ModifyAlbumViewModel
import com.github.soulsearching.model.utils.AndroidUtils
import com.github.soulsearching.modifyelement.modifyalbum.domain.ModifyAlbumEvent
import com.github.soulsearching.modifyelement.modifyalbum.presentation.composable.ModifyAlbumComposable

@Composable
actual fun ModifyAlbumScreenView(
    modifyAlbumViewModel: ModifyAlbumViewModel,
    selectedAlbumId: String,
    finishAction: () -> Unit
) {
    val context = LocalContext.current

    val resultImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                modifyAlbumViewModel.handler.onEvent(
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

    ModifyAlbumComposable(
        modifyAlbumViewModel = modifyAlbumViewModel,
        selectedAlbumId = selectedAlbumId,
        finishAction = finishAction,
        selectImage = { selectImage() }
    )
}