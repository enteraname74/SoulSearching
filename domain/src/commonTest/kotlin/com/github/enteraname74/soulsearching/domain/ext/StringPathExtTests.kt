package com.github.enteraname74.soulsearching.domain.ext

import com.github.enteraname74.domain.ext.filenameFromPath
import kotlin.test.Test
import kotlin.test.assertEquals

class StringPathExtTests {
    @Test
    fun givenUnixPath_whenGettingFilename_thenReturnsLastPathSegment() {
        assertEquals(
            expected = "Music",
            actual = "/home/noah/Music".filenameFromPath(),
        )
    }

    @Test
    fun givenWindowsPath_whenGettingFilename_thenReturnsLastPathSegment() {
        assertEquals(
            expected = "Music",
            actual = "C:\\Users\\noah\\Music".filenameFromPath(),
        )
    }

    @Test
    fun givenTrailingSeparator_whenGettingFilename_thenIgnoresSeparator() {
        assertEquals(
            expected = "Music",
            actual = "/home/noah/Music/".filenameFromPath(),
        )
    }
}
