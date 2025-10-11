package com.burrowsapps.gif.search.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.burrowsapps.gif.search.data.db.entity.GifEntity

@Dao
internal interface GifDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<GifEntity>)

  @Query("DELETE FROM gifs")
  suspend fun clearAll()

  @Query("SELECT COUNT(*) FROM gifs")
  suspend fun count(): Int

  @Query("SELECT * FROM gifs WHERE tinyGifUrl = :id LIMIT 1")
  suspend fun getById(id: String): GifEntity?
}
