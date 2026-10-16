package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.soulsearching.domain.model.Release
import com.github.enteraname74.soulsearching.domain.model.ReleaseAsset
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteRelease(
    @SerialName("html_url")
    val githubUrl: String,
    @SerialName("tag_name")
    val tag: String,
    val name: String,
    val body: String?,
    val assets: List<RemoteReleaseAsset> = emptyList(),
) {
    fun toRelease(): Release {
        val expectedApkName = "${ANDROID_APPLICATION_ID}_${tag.removePrefix("v")}.apk"
        val apkAssets = assets.filter { it.name.endsWith(APK_EXTENSION, ignoreCase = true) }
        val apkAsset = assets.firstOrNull { it.name == expectedApkName }
            ?: apkAssets.singleOrNull()

        return Release(
            name = name,
            tag = tag,
            githubUrl = githubUrl,
            changelog = body?.takeIf { it.isNotBlank() },
            apkAsset = apkAsset?.toReleaseAsset(),
        )
    }

    private companion object {
        const val ANDROID_APPLICATION_ID = "com.github.enteraname74.soulsearching"
        const val APK_EXTENSION = ".apk"
    }
}

@Serializable
data class RemoteReleaseAsset(
    val name: String,
    @SerialName("browser_download_url")
    val downloadUrl: String,
    val size: Long,
    val digest: String? = null,
) {
    fun toReleaseAsset(): ReleaseAsset = ReleaseAsset(
        name = name,
        downloadUrl = downloadUrl,
        size = size,
        sha256 = digest
            ?.takeIf { it.startsWith(SHA256_PREFIX, ignoreCase = true) }
            ?.substringAfter(':')
            ?.lowercase(),
    )

    private companion object {
        const val SHA256_PREFIX = "sha256:"
    }
}
