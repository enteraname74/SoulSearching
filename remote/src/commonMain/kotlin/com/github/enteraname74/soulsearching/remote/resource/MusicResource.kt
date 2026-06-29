package com.github.enteraname74.soulsearching.remote.resource

import io.ktor.resources.Resource

@Resource("/music")
class MusicResource {

    @Resource("check")
    data class Check(
        val parent: MusicResource = MusicResource()
    )

    @Resource("ofUser")
    data class OfUser(
        val parent: MusicResource = MusicResource(),
        val lastUpdateAt: Long? = null,
        val maxPerPage: Int? = null,
        val page: Int? = null,
    )
}