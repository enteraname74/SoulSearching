package com.github.enteraname74.domain.model.player

import kotlin.uuid.Uuid

data class SharedPlayedListPreview(
    val id: Uuid,
    val code: String,
    val totalUsers: Int,
    val connectedUsers: Int,
    val createdAtMillis: Long,
    val isOwner: Boolean,
)

fun SharedPlayedList.toPreview(
    userId: Uuid,
): SharedPlayedListPreview =
    SharedPlayedListPreview(
        id = id,
        code = inviteCode,
        totalUsers = users.size,
        connectedUsers = users.filter { it.status == PlayerUserStatus.Connected }.size,
        createdAtMillis = createdAt,
        isOwner = owner?.id == userId,
    )