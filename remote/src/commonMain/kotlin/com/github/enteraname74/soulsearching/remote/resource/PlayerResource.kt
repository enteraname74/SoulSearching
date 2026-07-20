package com.github.enteraname74.soulsearching.remote.resource

import io.ktor.resources.Resource
import kotlin.uuid.Uuid

@Resource("/player")
class PlayerResource {

    @Resource("join")
    class Join(
        val parent: PlayerResource = PlayerResource()
    )

    @Resource("")
    class List(
        val parent: PlayerResource = PlayerResource(),
        val listId: Uuid,
        val deviceId: String,
    )

    @Resource("removeUser")
    class RemoveUser(
        val parent: PlayerResource = PlayerResource(),
    )

    @Resource("allMusics")
    class GetMusics(
        val parent: PlayerResource = PlayerResource(),
        val listId: Uuid,
        val deviceId: String,
        val lastUpdateAt: Long?,
        val maxPerPage: Int?,
        val page: Int?,
    ) {
    }

    @Resource("check")
    class Check(
        val parent: PlayerResource = PlayerResource()
    )

    @Resource("musics")
    class Musics(
        val parent: PlayerResource = PlayerResource()
    )

    @Resource("url")
    class Url(
        val parent: PlayerResource = PlayerResource()
    )
}