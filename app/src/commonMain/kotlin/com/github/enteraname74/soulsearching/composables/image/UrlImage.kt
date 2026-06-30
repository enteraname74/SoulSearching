package com.github.enteraname74.soulsearching.composables.image

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import com.github.enteraname74.domain.model.CloudPreferences
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.domain.usecase.cloud.CommonCloudPreferencesUseCase
import com.github.enteraname74.domain.usecase.user.CommonUserUseCase
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement

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
    builderOptions: ImageRequest.Builder.() -> ImageRequest.Builder = { this },
) {
    var preferencesState: UrlImageDependencyState<CloudPreferences?> by remember {
        mutableStateOf(UrlImageDependencyState.Loading)
    }

    LaunchedEffect(commonCloudPreferencesUseCase) {
        commonCloudPreferencesUseCase.observePreferences().collect { preferences ->
            preferencesState = UrlImageDependencyState.Loaded(preferences)
        }
    }

    var userState: UrlImageDependencyState<User?> by remember {
        mutableStateOf(UrlImageDependencyState.Loading)
    }

    LaunchedEffect(commonUserUseCase) {
        commonUserUseCase.observeUser().collect { user ->
            userState = UrlImageDependencyState.Loaded(user)
        }
    }

    val preferences = (preferencesState as? UrlImageDependencyState.Loaded)?.value
    val user = (userState as? UrlImageDependencyState.Loaded)?.value
    val isLoadingDependencies =
        preferencesState == UrlImageDependencyState.Loading ||
            userState == UrlImageDependencyState.Loading

    var resolvedRequest: UrlImageRequest? by remember(url) {
        mutableStateOf(url?.let(UrlImageMemoryCache::get))
    }

    LaunchedEffect(
        url,
        preferences?.url,
        user?.accessToken,
        isLoadingDependencies,
    ) {
        if (isLoadingDependencies) {
            return@LaunchedEffect
        }

        val foundUrl = url?.takeIf { it.isNotBlank() }
        val baseUrl = preferences?.url?.takeIf { it.isNotBlank() }
        val accessToken = user?.accessToken?.takeIf { it.isNotBlank() }

        resolvedRequest = if (foundUrl != null && baseUrl != null && accessToken != null) {
            UrlImageRequest(
                url = "$baseUrl/$foundUrl",
                accessToken = accessToken,
            ).also {
                UrlImageMemoryCache.set(
                    key = foundUrl,
                    request = it,
                )
            }
        } else {
            foundUrl?.let(UrlImageMemoryCache::remove)
            null
        }
    }

    when (val request = resolvedRequest) {
        null -> {
            if (isLoadingDependencies && !url.isNullOrBlank()) {
                Box(modifier = modifier)
            } else {
                TemplateImage(
                    modifier = modifier,
                    contentScale = contentScale,
                    tint = tint,
                )
            }
        }

        else -> {
            SoulDataImage(
                data = request.url,
                contentScale = contentScale,
                modifier = modifier,
                onSuccess = onSuccess,
                builderOptions = {
                    builderOptions()
                        .memoryCacheKey(request.memoryCacheKey)
                        .placeholderMemoryCacheKey(request.memoryCacheKey)
                        .httpHeaders(
                            NetworkHeaders
                                .Builder()
                                .set(
                                    key = "Authorization",
                                    value = "Bearer ${request.accessToken}"
                                )
                                .build()
                        )
                },
                contentDescription = contentDescription,
                tint = tint,
                crossfade = false,
                showPlaceholder = false,
            )
        }
    }
}

private sealed interface UrlImageDependencyState<out T> {
    data object Loading : UrlImageDependencyState<Nothing>
    data class Loaded<T>(val value: T) : UrlImageDependencyState<T>
}

private data class UrlImageRequest(
    val url: String,
    val accessToken: String,
) {
    val memoryCacheKey: String = "url-image:$url"
}

private object UrlImageMemoryCache {
    private val requests: MutableMap<String, UrlImageRequest> = mutableMapOf()

    fun get(key: String): UrlImageRequest? =
        requests[key]

    fun set(
        key: String,
        request: UrlImageRequest,
    ) {
        requests[key] = request
    }

    fun remove(key: String) {
        requests.remove(key)
    }
}
