package com.burrowsapps.example.gif.ui.giflist

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.data.ImageService
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
class GifAdapterTest {
  @get:Rule(order = 0)
  val hiltRule = HiltAndroidRule(this)

  @Inject @ApplicationContext internal lateinit var context: Context
  @Inject internal lateinit var imageService: ImageService

  private val gifImageInfo = GifImageInfo("http://some.url")
  private val gifImageInfo2 = GifImageInfo("http://some.url2")
  private val gifImageInfo3 = GifImageInfo("http://some.url3")
  private lateinit var viewHolder: GifAdapter.ViewHolder
  private lateinit var sut: GifAdapter

  @Before
  fun setUp() {
    hiltRule.inject()

    sut = GifAdapter(
      onItemClick = {
      },
      imageService
    ).apply {
      add(listOf(gifImageInfo, gifImageInfo2))
    }
    viewHolder = sut.onCreateViewHolder(ConstraintLayout(context), 0)
  }

  @Test
  fun testOnCreateViewHolder() {
    val parent = object : ViewGroup(context) {
      override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {}
    }

    assertThat(sut.onCreateViewHolder(parent, 0))
      .isInstanceOf(GifAdapter.ViewHolder::class.java)
  }

  @Test
  fun testOnBindViewHolderOnAdapterItemClick() {
    sut.clear()
    sut.add(listOf(gifImageInfo, gifImageInfo2, GifImageInfo()))

    sut.onBindViewHolder(viewHolder, 0)

    assertThat(viewHolder.itemView.performClick()).isTrue()
  }

  @Test
  fun testGetItem() {
    sut.clear()

    val imageInfo = GifImageInfo()
    sut.add(listOf(imageInfo))

    assertThat(sut.getItem(0)).isEqualTo(imageInfo)
  }

  @Test
  fun onViewRecycled() {
    sut.add(listOf(GifImageInfo()))

    sut.onBindViewHolder(viewHolder, 0)
    sut.onViewRecycled(viewHolder)
  }

  @Test
  fun testGetItemCountShouldReturnCorrectValues() {
    assertThat(sut.itemCount).isEqualTo(2)
  }

  @Test
  fun testGetListCountShouldReturnCorrectValues() {
    assertThat(sut.getItem(0)).isEqualTo(gifImageInfo)
    assertThat(sut.getItem(1)).isEqualTo(gifImageInfo2)
  }

  @Test
  fun testGetItemShouldReturnCorrectValues() {
    assertThat(sut.getItem(1)).isEqualTo(gifImageInfo2)
  }

  @Test
  fun testClearShouldClearAdapter() {
    sut.clear()

    assertThat(sut.itemCount).isEqualTo(0)
  }

  @Test
  fun testAddCollectionShouldReturnCorrectValues() {
    val imageInfos = listOf(gifImageInfo3)

    sut.add(imageInfos)

    assertThat(sut.getItem(0)).isEqualTo(gifImageInfo)
    assertThat(sut.getItem(1)).isEqualTo(gifImageInfo2)
    assertThat(sut.getItem(2)).isEqualTo(gifImageInfo3)
  }
}
