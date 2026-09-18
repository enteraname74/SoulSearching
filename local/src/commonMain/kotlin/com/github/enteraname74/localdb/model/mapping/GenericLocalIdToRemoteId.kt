package com.github.enteraname74.localdb.model.mapping

import kotlin.uuid.Uuid

data class GenericLocalIdToRemoteId(
    val localId: Uuid,
    val remoteId: Uuid,
)
