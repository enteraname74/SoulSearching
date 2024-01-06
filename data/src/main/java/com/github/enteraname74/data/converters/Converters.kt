package com.github.enteraname74.data.converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

/**
 * Used for converting complex types to more simple ones for the database.
 */
internal class Converters {
    @TypeConverter
    fun bitmapToString(bitmap: Bitmap?): String {

        if (bitmap == null) return ""

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val b = outputStream.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    @TypeConverter
    fun stringToBitmap(string: String?): Bitmap? {

        if (string == "" || string == null) return null

        val imageBytes = Base64.decode(string,0)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    @TypeConverter
    fun localDateToString(date : LocalDateTime) : String {
        return date.toString()
    }

    @TypeConverter
    fun stringToLocalDate(string : String) : LocalDateTime {
        return LocalDateTime.parse(string)
    }
}