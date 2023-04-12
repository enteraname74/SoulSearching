package com.github.soulsearching.classes

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size

class Utils {
    companion object {
        fun getBitmapFromUri(uri : Uri, contentResolver : ContentResolver) : Bitmap {
            return if (Build.VERSION.SDK_INT >= 29) {
                contentResolver.loadThumbnail(
                    uri,
                    Size(400, 400),
                    null
                )
            } else {
                Bitmap.createScaledBitmap(
                    MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        uri
                    ), 400, 400, false
                )
            }
        }
    }
}