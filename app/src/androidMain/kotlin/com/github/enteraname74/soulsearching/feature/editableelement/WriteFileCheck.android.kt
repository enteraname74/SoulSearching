package com.github.enteraname74.soulsearching.feature.editableelement

import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.model.utils.AndroidUtils

@Composable
actual fun WriteFilesCheck(
    onSave: () -> Unit,
    musicsToSave: List<Music>,
    settings: SoulSearchingSettings,
    content: @Composable (onSave: () -> Unit) -> Unit,
) {
    val context = LocalContext.current
    val isMusicFileModificationOn: Boolean = settings.get(SoulSearchingSettingsKeys.IS_MUSIC_FILE_MODIFICATION_ON)
    val resultWriteFileLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _ ->
            /*
            We accept the finish action even if the user doesn't want to write the file modification on the device (it will
            just not do it then)
            */
            onSave()
        }

    fun acceptWriteFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && isMusicFileModificationOn) {

            val uris = mutableListOf<Uri>()

            musicsToSave.forEach { music ->
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
            onSave()
        }
    }

    content(::acceptWriteFile)
}