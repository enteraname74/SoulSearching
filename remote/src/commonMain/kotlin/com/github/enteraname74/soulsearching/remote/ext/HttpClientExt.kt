package com.github.enteraname74.soulsearching.remote.ext

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest

fun HttpClient.withUrl(url: String): HttpClient =
    config {
        installOrReplace(DefaultRequest) {
            url(url)
        }
    }