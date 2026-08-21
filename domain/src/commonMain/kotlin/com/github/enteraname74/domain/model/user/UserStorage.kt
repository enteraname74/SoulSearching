package com.github.enteraname74.domain.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserStorage(
    val max: StorageType,
    val current: Double,
) {

    @Serializable
    sealed interface StorageType {
        val total: Double

        fun copyData(total: Double): StorageType

        @Serializable
        @SerialName("AllAvailable")
        data class AllAvailable(override val total: Double) : StorageType {
            override fun copyData(total: Double): StorageType =
                AllAvailable(total)

        }

        @Serializable
        @SerialName("Reduced")
        data class Reduced(override val total: Double) : StorageType {
            override fun copyData(total: Double): StorageType = Reduced(total)
        }
    }
}