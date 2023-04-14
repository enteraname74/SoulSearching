package com.github.soulsearching.database.converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import com.github.soulsearching.database.model.Artist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream

class Converters {
    @TypeConverter
    fun bitmapToString(bitmap: Bitmap?): String {

        if (bitmap == null) return ""

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    @TypeConverter
    fun stringToBitmap(string: String): Bitmap? {

        if (string == "") return null

        val imageBytes = Base64.decode(string,0)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}