package com.burrowsapps.gif.search.data.db.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RemoteKeysEntityTest {
  @Test
  fun fields_areAssigned() {
    val entity = RemoteKeysEntity(searchKey = "", nextKey = "42.0")

    assertThat(entity.searchKey).isEqualTo("")
    assertThat(entity.nextKey).isEqualTo("42.0")
  }
}
