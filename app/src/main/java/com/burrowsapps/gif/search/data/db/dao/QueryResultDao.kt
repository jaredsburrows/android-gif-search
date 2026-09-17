package com.burrowsapps.gif.search.data.db.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.RewriteQueriesToDropUnusedColumns
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import com.burrowsapps.gif.search.data.db.entity.QueryResultEntity
import com.burrowsapps.gif.search.ui.giflist.GifImageInfo

// Room 3 no longer special-cases PagingSource returns: paging support ships as a pluggable
// return-type converter in room3-paging that has to be declared explicitly.
@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
internal interface QueryResultDao {
  // Keep first-seen ordering stable by ignoring duplicates. Returns the inserted row ids (-1 for a
  // row that was ignored as a duplicate) so callers can tell how many new rows actually landed.
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertAll(items: List<QueryResultEntity>): List<Long>

  @Query("DELETE FROM query_results WHERE searchKey = :searchKey")
  suspend fun clearQuery(searchKey: String)

  // Evicts cached result rows for any search whose last fetch (remote_keys.lastUpdated) predates
  // [cutoff], except [exceptKey] (the query currently loading). Without this, every distinct search
  // a user ever runs lingers in the DB forever, since clearQuery only ever targets the active query.
  // GIFs left unreferenced by this delete are reclaimed by GifDao.deleteOrphanedGifs(). Returns the
  // number of rows removed.
  @Query(
    """
    DELETE FROM query_results
    WHERE searchKey IN (
      SELECT searchKey FROM remote_keys
      WHERE searchKey != :exceptKey AND lastUpdated < :cutoff
    )
    """,
  )
  suspend fun clearStaleQueries(
    cutoff: Long,
    exceptKey: String,
  ): Int

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
