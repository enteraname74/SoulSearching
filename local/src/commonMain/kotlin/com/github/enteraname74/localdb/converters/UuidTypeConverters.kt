package com.github.enteraname74.localdb.converters

import androidx.room3.ColumnTypeConverter
import kotlin.uuid.Uuid

object UuidTypeConverters {

    @ColumnTypeConverter
    fun uuidToString(uuid: Uuid): String =
        uuid.toString()

    @ColumnTypeConverter
    fun stringToUuid(serialized: String): Uuid =
        Uuid.parse(serialized)
}
