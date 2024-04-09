package com.github.soulsearching.modifyelement.modifyartist.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
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

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultImageLauncher.launch(intent)
    }

    ModifyArtistComposable(
        modifyArtistViewModel = modifyArtistViewModel,
        selectedArtistId = selectedArtistId,
        finishAction = finishAction,
        selectImage = { selectImage() }
    )
}