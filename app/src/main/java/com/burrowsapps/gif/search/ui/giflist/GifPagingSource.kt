package com.burrowsapps.gif.search.ui.giflist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.burrowsapps.gif.search.data.api.model.GifResponseDto
import com.burrowsapps.gif.search.data.api.model.NetworkResult
import com.burrowsapps.gif.search.data.repository.GifRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * PagingSource that loads Tenor GIFs using a cursor-based `pos` key.
 * If [query] is null or blank, it loads trending results; otherwise, it loads search results.
 */
internal class GifPagingSource(
  private val repository: GifRepository,
  private val dispatcher: CoroutineDispatcher,
  private val query: String?,
) : PagingSource<String, GifImageInfo>() {
  override suspend fun load(params: LoadParams<String>): LoadResult<String, GifImageInfo> =
    withContext(dispatcher) {
      val position: String? = params.key

      val result: NetworkResult<GifResponseDto> =
        runCatching {
          val q = query?.takeIf { it.isNotBlank() }
          if (q == null) {
            repository.getTrendingResults(position)
          } else {
            repository.getSearchResults(q, position)
          }
        }.getOrElse { throwable ->
          return@withContext LoadResult.Error(throwable)
        }

      when (result) {
        is NetworkResult.Success -> {
          val response = result.data
          val items = buildGifList(response)
          // Avoid re-using the same next key across sequential pages. If the API returns the
          // same cursor as the one we just used (params.key), signal end of pagination by
          // nulling nextKey; Paging3 forbids duplicate sequential nextKeys.
          val nextKey = response?.next?.takeUnless { it == position }

          Timber.i(
            "GifPagingSource query=%s, pos=%s, next=%s, loaded=%d",
            query,
            position,
            nextKey,
            items.size,
          )

          LoadResult.Page(
            data = items,
            prevKey = null, // Cursor-based API doesn't support backward keys easily
            nextKey = nextKey,
          )
        }
        is NetworkResult.Empty ->
          LoadResult.Page(
            data = emptyList(),
            prevKey = null,
            nextKey = null,
          )
        is NetworkResult.Error -> LoadResult.Error(Exception(result.message))
        is NetworkResult.Loading ->
          LoadResult.Error(IllegalStateException("Unexpected Loading state from repository"))
      }
    }

  override fun getRefreshKey(state: PagingState<String, GifImageInfo>): String? {
    // For cursor-based pagination, restart from the beginning
    return null
  }

  private fun buildGifList(response: GifResponseDto?): List<GifImageInfo> =
    response
      ?.results
      ?.map { result ->
        val media = result.media.first()
        val gif = media.gif
        val tinyGif = media.tinyGif

        GifImageInfo(
          gifUrl = gif.url,
          gifPreviewUrl = gif.preview,
          tinyGifUrl = tinyGif.url,
          tinyGifPreviewUrl = tinyGif.preview,
        )
      }.orEmpty()
}
