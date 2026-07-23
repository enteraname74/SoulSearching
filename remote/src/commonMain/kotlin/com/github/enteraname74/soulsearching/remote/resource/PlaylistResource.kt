package com.github.enteraname74.soulsearching.remote.resource

import io.ktor.resources.Resource

@Resource("/playlist")
class PlaylistResource {

    @Resource("ofUser")
    data class OfUser(
        val parent: PlaylistResource = PlaylistResource(),
        val lastUpdateAt: Long? = null,
        val maxPerPage: Int? = null,
        val page: Int? = null,
    )

    @Resource("check")
    data class Check(
        val parent: PlaylistResource = PlaylistResource()
    )
}