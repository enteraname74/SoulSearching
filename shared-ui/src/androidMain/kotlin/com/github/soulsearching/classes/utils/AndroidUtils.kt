package com.github.soulsearching.classes.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.github.soulsearching.playback.PlayerService
import com.github.soulsearching.utils.PlayerUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * All kind of different methods for android.
 */
object AndroidUtils {
    const val BITMAP_SIZE = 300

    /**
     * Launch the playback service.
     * It also assure that the service is launched in the player view model.
     */
    fun launchService(
        context: Context,
        isFromSavedList: Boolean,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val serviceIntent = Intent(context, PlayerService::class.java)
            serviceIntent.putExtra(PlayerService.IS_FROM_SAVED_LIST, isFromSavedList)
            context.startForegroundService(serviceIntent)
            PlayerUtils.playerViewModel.handler.isServiceLaunched = true
        }
    }

    /**
     * Retrieve a bitmap from a given Uri.
     */
    @Suppress("DEPRECATION")
    fun getBitmapFromUri(uri: Uri, contentResolver: ContentResolver): ImageBitmap {
        return if (Build.VERSION.SDK_INT >= 29) {
            contentResolver.loadThumbnail(
                uri,
                Size(BITMAP_SIZE, BITMAP_SIZE),
                null
            ).asImageBitmap()
        } else {
            Bitmap.createScaledBitmap(
                MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    uri
                ), BITMAP_SIZE, BITMAP_SIZE, false
            ).asImageBitmap()
        }
    }
}