package com.github.enteraname74.localdb.model.mapping

import kotlin.uuid.Uuid

data class MusicIdToRemoteId(
    val musicId: Uuid,
    val remoteId: String,
)
