package com.github.enteraname74.localdb.converters

import androidx.room3.ColumnTypeConverter
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.domain.model.user.UserType

internal object UserTypeConverters {

    @ColumnTypeConverter
    fun userTypeToString(userType: UserType): String =
        userType.value

    @ColumnTypeConverter
    fun stringToUserType(serialized: String): UserType =
        UserType.fromValue(serialized) ?: UserType.Unknown
}
