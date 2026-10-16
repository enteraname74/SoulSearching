package com.github.enteraname74.soulsearching.remote.resource

import io.ktor.resources.Resource

@Resource("/collection")
class CollectionResource {
    @Resource("ofUser")
    data class OfUser(
        val parent: CollectionResource = CollectionResource(),
        val lastUpdateAt: Long? = null,
        val maxPerPage: Int? = null,
        val page: Int? = null,
    )

    @Resource("check")
    data class Check(val parent: CollectionResource = CollectionResource())
}
