package com.github.enteraname74.soulsearching.remote.resource

import io.ktor.resources.Resource

@Resource("/soulSearching/settings")
data class SettingsResource(val lastUpdateAt: Long? = null)
