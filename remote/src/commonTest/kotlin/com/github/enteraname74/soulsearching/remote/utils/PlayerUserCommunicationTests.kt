package com.github.enteraname74.soulsearching.remote.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class PlayerUserCommunicationTests {
    private val listId = Uuid.parse("11111111-1111-1111-1111-111111111111")
    private val userId = Uuid.parse("22222222-2222-2222-2222-222222222222")

    @Test
    fun givenHttpUrl_whenBuildingPlayerUrl_thenUsesWebSocket() {
        assertEquals(
            expected = "ws://localhost:8080/player/$listId?userId=$userId&deviceId=device",
            actual = buildUrl("http://localhost:8080"),
        )
    }

    @Test
    fun givenHttpsUrl_whenBuildingPlayerUrl_thenUsesSecureWebSocket() {
        assertEquals(
            expected = "wss://cloudy.example/player/$listId?userId=$userId&deviceId=device",
            actual = buildUrl("https://cloudy.example"),
        )
    }

    @Test
    fun givenBasePathAndTrailingSlash_whenBuildingPlayerUrl_thenPreservesBasePath() {
        assertEquals(
            expected = "wss://cloudy.example:8443/api/player/$listId?userId=$userId&deviceId=device",
            actual = buildUrl("https://cloudy.example:8443/api/"),
        )
    }

    @Test
    fun givenDeviceIdWithReservedCharacters_whenBuildingPlayerUrl_thenEncodesQueryParameter() {
        assertEquals(
            expected = "wss://cloudy.example/player/$listId?userId=$userId&deviceId=my+device%2F%2B%3F",
            actual = buildUrl(
                baseUrl = "https://cloudy.example",
                deviceId = "my device/+?",
            ),
        )
    }

    @Test
    fun givenUnsupportedProtocol_whenBuildingPlayerUrl_thenFails() {
        assertFailsWith<IllegalArgumentException> {
            buildUrl("ftp://cloudy.example")
        }
    }

    private fun buildUrl(
        baseUrl: String,
        deviceId: String = "device",
    ): String = buildPlayerWebSocketUrl(
        baseUrl = baseUrl,
        listId = listId,
        userId = userId,
        deviceId = deviceId,
    )
}
