package com.github.enteraname74.soulsearching.domain.model.lyrics


import com.github.enteraname74.domain.model.lyrics.StringTimestampConverter
import kotlin.test.Test
import kotlin.test.assertEquals

class StringTimestampConverterTests {

    @Test
    fun givenStringTimestamp_whenConvertingToLong_thenShouldBeCorrect() {
        val stringTimestamp = "[02:25.50]"
        val expectedResult = 145050L

        val actualResult: Long? = StringTimestampConverter(stringTimestamp).convert()

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun givenLongStringTimestamp_whenConvertingToLong_thenShouldBeCorrect() {
        val stringTimestamp = "[120:25.50]"
        val expectedResult = 7225050L

        val actualResult: Long? = StringTimestampConverter(stringTimestamp).convert()

        assertEquals(expectedResult, actualResult)
    }
}