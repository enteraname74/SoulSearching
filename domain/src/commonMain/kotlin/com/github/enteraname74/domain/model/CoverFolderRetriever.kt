package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CoverFolderRetriever(
    val isActivated: Boolean = true,
    val folderModePath: String?,
    val fileModePath: String?,
    val coverFileName: String?,
    val fileExtension: String?,
    val whiteSpaceRule: WhiteSpaceRule,
    val lowerCaseRule: Boolean?,
    val mode: DynamicMode,
) {

    fun String.applyWhiteSpaceRule(): String =
        whiteSpaceRule.takeIf { it.isActivated }?.replacement?.let { replacement ->
            replace(" ", replacement)
        } ?: this

    fun String.applyLowerCaseRule(): String =
        lowerCaseRule?.let { lowerCase ->
            if (lowerCase) {
                lowercase()
            } else {
                uppercase()
            }
        } ?: this

    private fun isValid(): Boolean =
        when(mode) {
            DynamicMode.Folder -> folderModePath?.takeIf { it.isNotBlank() }  != null && coverFileName?.takeIf { it.isNotBlank() } != null
            DynamicMode.File -> fileModePath?.takeIf { it.isNotBlank() }  != null && fileExtension?.takeIf { it.isNotBlank() } != null
        }

    fun buildDynamicCoverPath(dynamicName: String): String? {
        if (!isValid()) return null

        val finalDynamicName = dynamicName
            .applyLowerCaseRule()
            .applyWhiteSpaceRule()

        return when(mode) {
            DynamicMode.Folder -> "$folderModePath/$finalDynamicName/$coverFileName"
            DynamicMode.File -> {
                if (fileExtension == null) {
                    return null
                }
                val cleanedExtension = fileExtension.replace(".", "")
                "$fileModePath/$finalDynamicName.$cleanedExtension"
            }
        }
    }

    enum class DynamicMode {
        Folder,
        File;
    }

    @Serializable
    data class WhiteSpaceRule(
        val isActivated: Boolean,
        val replacement: String,
    )

    companion object {
        val default: CoverFolderRetriever = CoverFolderRetriever(
            isActivated = false,
            folderModePath = null,
            fileModePath = null,
            coverFileName = "cover.jpg",
            whiteSpaceRule = WhiteSpaceRule(
                isActivated = false,
                replacement = "",
            ),
            lowerCaseRule = null,
            mode = DynamicMode.Folder,
            fileExtension = "jpg",
        )
    }
}