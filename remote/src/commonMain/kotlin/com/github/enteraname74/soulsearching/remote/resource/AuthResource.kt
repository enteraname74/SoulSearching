package com.github.enteraname74.soulsearching.remote.resource

import io.ktor.resources.Resource

@Resource("/auth")
class AuthResource {
    @Resource("signIn")
    data class SignIn(val parent: AuthResource = AuthResource())

    @Resource("logIn")
    data class LogIn(val parent: AuthResource = AuthResource())

    @Resource("refreshTokens")
    data class RefreshTokens(val parent: AuthResource = AuthResource())
}