package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import com.github.enteraname74.domain.usecase.cloud.CommonCloudPreferencesUseCase
import com.github.enteraname74.domain.usecase.user.CommonUserUseCase
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManager

@Composable
internal fun UrlImage(
    url: String?,
    modifier: Modifier = Modifier,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String?,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
    commonCloudPreferencesUseCase: CommonCloudPreferencesUseCase = injectElement(),
    commonUserUseCase: CommonUserUseCase = injectElement(),
    cachedCoverManager: CachedCoverManager = injectElement(),
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
) {
    val user by commonUserUseCase.observeUser().collectAsStateWithLifecycle(null)
    val preferences by commonCloudPreferencesUseCase.observeUrl().collectAsStateWithLifecycle("")

    SoulDataImage(
        data = "$preferences/$url",
        contentScale = contentScale,
        modifier = modifier,
        onSuccess = { bitmap ->
            bitmap?.let {
                cachedCoverManager.cacheImage(
                    key = url.orEmpty(),
                    imageBitmap = bitmap
                )
            }
            onSuccess?.invoke(bitmap)
        },
        builderOptions = {
            builderOptions()
                .memoryCacheKey("url-image:$url")
                .placeholderMemoryCacheKey("url-image:$url")
                .httpHeaders(
                    NetworkHeaders
                        .Builder()
                        .apply {
                            user?.accessToken?.let {
                                set(
                                    key = "Authorization",
                                    value = "Bearer $it"
                                )
                            }
                        }
                        .build()
                )
        },
        contentDescription = contentDescription,
        tint = tint,
    )
}
