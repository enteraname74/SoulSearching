package com.github.enteraname74.soulsearching.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class FetchFromUrlBody(
    val url: String,
)
