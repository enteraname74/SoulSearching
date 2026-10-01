package com.github.enteraname74.soulsearching.remote.resource

import io.ktor.resources.Resource

@Resource("/statistics")
class StatisticsResource {
    @Resource("ofUser")
    data class OfUser(
        val parent: StatisticsResource = StatisticsResource(),
        val lastUpdateAt: Long? = null,
        val maxPerPage: Int? = null,
        val page: Int? = null,
    )
}