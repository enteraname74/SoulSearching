package com.github.enteraname74.localdb.ext

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.filter
import androidx.paging.map
import com.github.enteraname74.localdb.utils.PagingUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T : Any, R : Any> (() -> PagingSource<Int, T>).toPagingData(
    map: (T) -> R?,
): Flow<PagingData<R>> =
    Pager(
        config = PagingConfig(
            pageSize = PagingUtils.PAGE_SIZE,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = this,
    ).flow.map { pagingData ->
        pagingData
            .filter { map(it) != null }
            .map { map(it)!! }
    }