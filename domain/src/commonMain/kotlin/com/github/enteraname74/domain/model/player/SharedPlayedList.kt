package com.github.enteraname74.domain.model.player

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@Serializable
data class SharedPlayedList(
    val id: Uuid,
    val inviteCode: String,
    val state: State,
    val owner: SharedPlayedListUser?,
    val users: List<SharedPlayedListUser>,
) {
    enum class State(val value: String) {
        Playing("playing"),
        Paused("paused");

        companion object {
            fun fromValueOrPaused(value: String): State =
                entries.find { it.value == value } ?: Paused
        }
    }
}
