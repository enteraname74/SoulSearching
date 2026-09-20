package com.github.enteraname74.soulsearching.remote.resource

import io.ktor.resources.Resource
import kotlin.uuid.Uuid

@Resource("/users")
class UserResource {

    @Resource("{id}")
    data class Delete(
        val parent: UserResource = UserResource(),
        val id: Uuid,
    )

    @Resource("generateCode")
    data class GenerateCode(
        val parent: UserResource = UserResource(),
    )

    @Resource("codes")
    data class AllCodes(
        val parent: UserResource = UserResource()
    )

    @Resource("codes/{id}")
    data class Code(
        val parent: UserResource = UserResource(),
        val id: Uuid
    )

    @Resource("storage")
    data class Storage(
        val parent: UserResource = UserResource(),
    )
}
