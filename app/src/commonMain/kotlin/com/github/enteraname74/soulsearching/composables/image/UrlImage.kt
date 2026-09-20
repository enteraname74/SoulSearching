package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.usecase.cloud.CommonCloudPreferencesUseCase
import com.github.enteraname74.domain.usecase.user.CommonUserUseCase
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement

@Composable
internal fun UrlImage(
    urlCover: Cover.Url,
    modifier: Modifier = Modifier,
    tint: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String?,
    onSuccess: ((bitmap: ImageBitmap?) -> Unit)? = null,
    commonCloudPreferencesUseCase: CommonCloudPreferencesUseCase = injectElement(),
    commonUserUseCase: CommonUserUseCase = injectElement(),
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
) {
    val user by commonUserUseCase.observeUser().collectAsStateWithLifecycle(null)
    val preferences by commonCloudPreferencesUseCase.observeUrl().collectAsStateWithLifecycle("")
    var shouldUseFallback: Boolean by remember(urlCover.url, preferences) {
        mutableStateOf(false)
    }

    if (shouldUseFallback) {
        InnerSoulImage(
            cover = urlCover.fallback,
            modifier = modifier,
            contentScale = contentScale,
            tint = tint,
            onSuccess = onSuccess,
            builderOptions = builderOptions,
        )
    } else {
        SoulDataImage(
            data = "$preferences/${urlCover.url}",
            contentScale = contentScale,
            modifier = modifier,
            onSuccess = { bitmap ->
                onSuccess?.invoke(bitmap)
            },
            onError = {
                shouldUseFallback = true
            },
            builderOptions = {
                builderOptions()
                    .memoryCacheKey("url-image:${urlCover.url}")
                    .placeholderMemoryCacheKey("url-image:${urlCover.url}")
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
}
