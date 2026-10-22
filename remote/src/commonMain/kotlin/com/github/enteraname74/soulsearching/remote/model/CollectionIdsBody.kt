package com.github.enteraname74.soulsearching.remote.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CollectionIdsBody(val ids: List<Uuid>)
