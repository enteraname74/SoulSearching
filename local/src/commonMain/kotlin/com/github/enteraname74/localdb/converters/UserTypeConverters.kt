package com.github.enteraname74.localdb.converters

import androidx.room.TypeConverter
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.domain.model.user.UserType

internal object UserTypeConverters {

    @TypeConverter
    fun userTypeToString(userType: UserType): String =
        userType.value

    @TypeConverter
    fun stringToUserType(serialized: String): UserType =
        UserType.fromValue(serialized) ?: UserType.Unknown
}
