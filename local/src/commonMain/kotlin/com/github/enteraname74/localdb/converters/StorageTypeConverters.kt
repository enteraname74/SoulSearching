package com.github.enteraname74.localdb.converters

import androidx.room3.ColumnTypeConverter
import com.github.enteraname74.domain.model.user.UserStorage
import com.github.enteraname74.soulsearching.features.serialization.SerializationUtils

object StorageTypeConverters {
    @ColumnTypeConverter
    fun toString(storageType: UserStorage.StorageType): String =
        SerializationUtils.serialize(storageType)

    @ColumnTypeConverter
    fun fromString(serialized: String): UserStorage.StorageType =
        SerializationUtils.deserialize(serialized)

}