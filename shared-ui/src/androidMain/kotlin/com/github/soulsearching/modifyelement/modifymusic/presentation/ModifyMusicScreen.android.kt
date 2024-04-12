package com.github.soulsearching.modifyelement.modifymusic.presentation

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
import com.github.soulsearching.domain.viewmodel.ModifyMusicViewModel
import com.github.soulsearching.model.utils.AndroidUtils
import com.github.soulsearching.modifyelement.modifymusic.domain.ModifyMusicEvent
import com.github.soulsearching.modifyelement.modifymusic.presentation.composable.ModifyMusicComposable
import java.io.File

@Composable
actual fun ModifyMusicScreenView(
    modifyMusicViewModel: ModifyMusicViewModel,
    selectedMusicId: String,
    finishAction: () -> Unit
) {
    val context = LocalContext.current

    val state by modifyMusicViewModel.handler.state.collectAsState()

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

    val resultWriteFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            println("GOT RESULT FROM INTENT: ${result.resultCode}")
            /*
               We accept the finish action even if the user doesn't want to write the file modification on the device (it will
               just not do it then)
            */
            modifyMusicViewModel.handler.onEvent(ModifyMusicEvent.UpdateMusic)
            finishAction()

        }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultImageLauncher.launch(intent)
    }

    /**
     * Retrieve the media id from the media store of a music path.
     */
    fun musicPathToMediaId(musicPath: String): Long {
        var id: Long = 0

        val uri = MediaStore.Files.getContentUri("external")
        val selection = MediaStore.Audio.Media.DATA
        val selectionArgs = arrayOf(musicPath)
        val projection = arrayOf(MediaStore.Audio.Media._ID)

        context.contentResolver.query(
            uri,
            projection,
            "$selection=?",
            selectionArgs,
            null
        )?.let { cursor ->
            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                id = cursor.getString(idIndex).toLong()
            }
            cursor.close()
        }

        return id
    }


    fun acceptWriteFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val mediaId = musicPathToMediaId(musicPath = state.modifiedMusicInformation.path)
            println("MEDIA ID: $mediaId")
            val musicFileUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.getContentUri("external"),
                mediaId
            )

            println("Music uri: $musicFileUri")

            val intent = MediaStore.createWriteRequest(
                context.contentResolver,
                mutableListOf(musicFileUri)
            )
            val intentSenderRequest = IntentSenderRequest.Builder(intent.intentSender).build()
            resultWriteFileLauncher.launch(intentSenderRequest)
        } else {
            println("ELSE")
            modifyMusicViewModel.handler.onEvent(ModifyMusicEvent.UpdateMusic)
            finishAction()
        }
    }

    ModifyMusicComposable(
        modifyMusicViewModel = modifyMusicViewModel,
        selectedMusicId = selectedMusicId,
        finishAction = { acceptWriteFile() },
        selectImage = { selectImage() }
    )
}