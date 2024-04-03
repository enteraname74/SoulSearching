package com.github.soulsearching.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.soulsearching.classes.utils.AndroidUtils
import com.github.soulsearching.composables.ModifyArtistComposable
import com.github.soulsearching.events.ArtistEvent
import com.github.soulsearching.viewmodel.ModifyArtistViewModel

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
                modifyArtistViewModel.handler.onArtistEvent(
                    ArtistEvent.SetCover(
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