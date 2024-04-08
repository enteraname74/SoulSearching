package com.github.soulsearching.modifyelement.modifymusic.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.soulsearching.domain.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.model.utils.AndroidUtils
import com.github.soulsearching.modifyelement.modifymusic.domain.ModifyMusicEvent
import com.github.soulsearching.modifyelement.modifymusic.presentation.composable.ModifyMusicComposable

@Composable
actual fun ModifyMusicScreenView(
    modifyMusicViewModel: ModifyMusicViewModel,
    selectedMusicId: String,
    finishAction: () -> Unit
) {
    val context = LocalContext.current

    val resultImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                modifyMusicViewModel.handler.onEvent(
                    ModifyMusicEvent.SetCover(
                        AndroidUtils.getBitmapFromUri(uri as Uri, context.contentResolver)
                    )
                )
            }
        }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultImageLauncher.launch(intent)
    }

    ModifyMusicComposable(
        modifyMusicViewModel = modifyMusicViewModel,
        selectedMusicId = selectedMusicId,
        finishAction = finishAction,
        selectImage = { selectImage() }
    )
}