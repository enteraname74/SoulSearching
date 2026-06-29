package com.github.enteraname74.localdb.converters

import androidx.room.TypeConverter
import kotlin.uuid.Uuid

object UuidTypeConverters {

    @TypeConverter
    fun uuidToString(uuid: Uuid): String =
        uuid.toString()

    @TypeConverter
    fun stringToUuid(serialized: String): Uuid =
        Uuid.parse(serialized)
}