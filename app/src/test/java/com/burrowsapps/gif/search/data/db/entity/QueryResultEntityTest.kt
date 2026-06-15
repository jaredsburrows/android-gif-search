package com.burrowsapps.gif.search.data.db.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class QueryResultEntityTest {
  @Test
  fun fields_areAssigned() {
    val entity = QueryResultEntity(searchKey = "cats", gifId = "tiny", position = 7)

    assertThat(entity.searchKey).isEqualTo("cats")
    assertThat(entity.gifId).isEqualTo("tiny")
    assertThat(entity.position).isEqualTo(7)
  }
}
