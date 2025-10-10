package com.burrowsapps.gif.search.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import com.burrowsapps.gif.search.data.db.entity.QueryResultEntity
import com.burrowsapps.gif.search.ui.giflist.GifImageInfo

@Dao
internal interface QueryResultDao {
  // Keep first-seen ordering stable by ignoring duplicates
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertAll(items: List<QueryResultEntity>)

  @Query("DELETE FROM query_results WHERE searchKey = :searchKey")
  suspend fun clearQuery(searchKey: String)

  @Query("SELECT COALESCE(MAX(position) + 1, 0) FROM query_results WHERE searchKey = :searchKey")
  suspend fun nextPositionForQuery(searchKey: String): Long

  @RewriteQueriesToDropUnusedColumns
  @Query(
    """
    SELECT g.tinyGifUrl AS tinyGifUrl,
           g.tinyGifPreviewUrl AS tinyGifPreviewUrl,
           g.gifUrl AS gifUrl,
           g.gifPreviewUrl AS gifPreviewUrl
    FROM query_results qr
    INNER JOIN gifs g ON g.tinyGifUrl = qr.gifId
    WHERE qr.searchKey = :searchKey
    ORDER BY qr.position ASC
    """,
  )
  fun pagingSource(searchKey: String): PagingSource<Int, GifImageInfo>

  @RewriteQueriesToDropUnusedColumns
  @Query(
    """
    SELECT g.tinyGifUrl AS tinyGifUrl,
           g.tinyGifPreviewUrl AS tinyGifPreviewUrl,
           g.gifUrl AS gifUrl,
           g.gifPreviewUrl AS gifPreviewUrl
    FROM query_results qr
    INNER JOIN gifs g ON g.tinyGifUrl = qr.gifId
    WHERE qr.searchKey = :searchKey
    ORDER BY qr.position ASC
    """,
  )
  suspend fun allForQuery(searchKey: String): List<GifImageInfo>
}
