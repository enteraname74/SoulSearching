package com.github.enteraname74.localdb.converters

import androidx.room.TypeConverter
import com.github.enteraname74.domain.model.User

internal object UserTypeConverters {

    @TypeConverter
    fun userTypeToString(userType: User.Type): String =
        userType.value

    @TypeConverter
    fun stringToUserType(serialized: String): User.Type =
        User.Type.fromValue(serialized) ?: User.Type.Unknown
}