package com.burrowsapps.gif.search.data.db.entity

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteKeysEntityTest {
  @Test
  fun fields_areAssigned() {
    val entity = RemoteKeysEntity(searchKey = "", nextKey = "42.0")

    assertThat(entity.searchKey).isEqualTo("")
    assertThat(entity.nextKey).isEqualTo("42.0")
  }
}
