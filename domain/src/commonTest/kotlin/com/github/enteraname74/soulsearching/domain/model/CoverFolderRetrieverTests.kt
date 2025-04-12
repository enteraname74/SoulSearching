package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.features.serialization.SerializationUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class CoverFolderRetrieverTests {
    @Test
    fun givenDynamicFolderSettings_whenBuildingDynamicPath_thenPathShouldFollowGivenSettings() {
        var settings = CoverFolderRetriever(
            folderPath = "my/initial/folder",
            filePath = "",
            coverFileName = "cover.png",
            mode = CoverFolderRetriever.DynamicMode.Folder,
            lowerCaseRule = true,
            whiteSpaceRule = CoverFolderRetriever.WhiteSpaceRule(
                isActivated = true,
                replacement = "_"
            ),
        )

        var actualPath = settings.buildDynamicCoverPath("John Doe")
        var expectedPath = "my/initial/folder/john_doe/cover.png"

        assertEquals(
            expected = expectedPath,
            actual = actualPath,
        )

        settings = CoverFolderRetriever(
            folderPath = "my/initial/folder",
            filePath = "",
            coverFileName = "cover.png",
            mode = CoverFolderRetriever.DynamicMode.Folder,
            lowerCaseRule = false,
            whiteSpaceRule = CoverFolderRetriever.WhiteSpaceRule(
                isActivated = false,
                replacement = "_"
            ),
        )

        actualPath = settings.buildDynamicCoverPath("John Doe")
        expectedPath = "my/initial/folder/JOHN DOE/cover.png"

        assertEquals(
            expected = expectedPath,
            actual = actualPath,
        )
    }

    @Test
    fun givenDynamicFileSettings_whenBuildingDynamicPath_thenPathShouldFollowGivenSettings() {
        var settings = CoverFolderRetriever(
            folderPath = "",
            filePath = "my/folder/path",
            mode = CoverFolderRetriever.DynamicMode.File,
            coverFileName = "",
            lowerCaseRule = true,
            whiteSpaceRule = CoverFolderRetriever.WhiteSpaceRule(
                isActivated = true,
                replacement = "_"
            ),
        )

        var actualPath = settings.buildDynamicCoverPath("John Doe")
        var expectedPath = "my/folder/path/john_doe.png"

        assertEquals(
            expected = expectedPath,
            actual = actualPath,
        )

        settings = CoverFolderRetriever(
            folderPath = "",
            filePath = "my/folder/path",
            mode = CoverFolderRetriever.DynamicMode.File,
            coverFileName = "",
            lowerCaseRule = false,
            whiteSpaceRule = CoverFolderRetriever.WhiteSpaceRule(
                isActivated = true,
                replacement = ""
            ),
        )

        actualPath = settings.buildDynamicCoverPath("John Doe")
        expectedPath = "my/folder/path/JOHNDOE.png"

        assertEquals(
            expected = expectedPath,
            actual = actualPath,
        )
    }

    @Test
    fun givenDynamicSettings_whenSerializingSettings_thenDeserializedSettingsShouldBeTheSame() {
        var settings = CoverFolderRetriever(
            folderPath = "my/folder/path",
            filePath = "",
            mode = CoverFolderRetriever.DynamicMode.File,
            coverFileName = "",
            lowerCaseRule = true,
            whiteSpaceRule = CoverFolderRetriever.WhiteSpaceRule(
                isActivated = true,
                replacement = "_"
            ),
        )
        val serializedSettings = SerializationUtils.serialize(settings)
        val deserialized: CoverFolderRetriever = SerializationUtils.deserialize(serializedSettings)

        assertEquals(expected = deserialized, actual = deserialized)
    }
}