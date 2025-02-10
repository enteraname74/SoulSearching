package com.github.enteraname74.localdb.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime

/**
 * Used for converting complex types to more simple ones for the database.
 */
internal class Converters {
    @TypeConverter
    fun localDateToString(date : LocalDateTime) : String {
        return date.toString()
    }

    @TypeConverter
    fun stringToLocalDate(string : String) : LocalDateTime {
        return LocalDateTime.parse(string)
    }
}