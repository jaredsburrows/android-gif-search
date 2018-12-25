package com.burrowsapps.example.gif.giflist

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.example.gif.data.ImageService
import com.burrowsapps.example.gif.giflist.GifAdapter.OnItemClickListener
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.core.app.ApplicationProvider

@RunWith(AndroidJUnit4::class)
class GifAdapterTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val gifImageInfo = GifImageInfo("http://some.url")
    private val gifImageInfo2 = GifImageInfo("http://some.url2")
    private val gifImageInfo3 = GifImageInfo("http://some.url3")
    private val testOnItemClickListener = object : OnItemClickListener {
        override fun onClick(imageInfoModel: GifImageInfo) {
        }
    }
    private lateinit var imageService: ImageService
    private lateinit var viewHolder: GifAdapter.ViewHolder
    private lateinit var sut: GifAdapter

    @Before fun setUp() {
        imageService = ImageService(context)
        sut = GifAdapter(testOnItemClickListener, imageService).apply {
            add(gifImageInfo)
            add(gifImageInfo2)
        }
        viewHolder = sut.onCreateViewHolder(LinearLayout(context), 0)
    }

    @Test fun testOnCreateViewHolder() {
        val parent = object : ViewGroup(context) {
            override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {}
        }

        assertThat(sut.onCreateViewHolder(parent, 0))
            .isInstanceOf(GifAdapter.ViewHolder::class.java)
    }

    @Test fun testOnBindViewHolderOnAdapterItemClick() {
        sut.clear()
        sut.add(gifImageInfo)
        sut.add(gifImageInfo2)
        sut.add(GifImageInfo())

        sut.onBindViewHolder(viewHolder, 0)

        assertThat(viewHolder.itemView.performClick()).isTrue()
    }

    @Test fun testGetItem() {
        sut.clear()

        val imageInfo = GifImageInfo()
        sut.add(imageInfo)

        assertThat(sut.getItem(0)).isEqualTo(imageInfo)
    }

    @Test fun onViewRecycled() {
        sut.add(GifImageInfo())

        sut.onBindViewHolder(viewHolder, 0)
        sut.onViewRecycled(viewHolder)
    }

    @Test fun testGetItemCountShouldReturnCorrectValues() {
        assertThat(sut.itemCount).isEqualTo(2)
    }

    @Test fun testGetListCountShouldReturnCorrectValues() {
        assertThat(sut.getItem(0)).isEqualTo(gifImageInfo)
        assertThat(sut.getItem(1)).isEqualTo(gifImageInfo2)
    }

    @Test fun testGetItemShouldReturnCorrectValues() {
        assertThat(sut.getItem(1)).isEqualTo(gifImageInfo2)
    }

    @Test fun testClearShouldClearAdapter() {
        sut.clear()

        assertThat(sut.itemCount).isEqualTo(0)
    }

    @Test fun testAddObjectShouldReturnCorrectValues() {
        sut.add(gifImageInfo3)

        assertThat(sut.getItem(0)).isEqualTo(gifImageInfo)
        assertThat(sut.getItem(1)).isEqualTo(gifImageInfo2)
        assertThat(sut.getItem(2)).isEqualTo(gifImageInfo3)
    }

    @Test fun testAddCollectionShouldReturnCorrectValues() {
        val imageInfos = listOf(gifImageInfo3)

        sut.addAll(imageInfos)

        assertThat(sut.getItem(0)).isEqualTo(gifImageInfo)
        assertThat(sut.getItem(1)).isEqualTo(gifImageInfo2)
        assertThat(sut.getItem(2)).isEqualTo(gifImageInfo3)
    }

    @Test fun testAddLocationObjectShouldReturnCorrectValues() {
        sut.add(0, gifImageInfo3)

        assertThat(sut.getItem(0)).isEqualTo(gifImageInfo3)
        assertThat(sut.getItem(1)).isEqualTo(gifImageInfo)
        assertThat(sut.getItem(2)).isEqualTo(gifImageInfo2)
    }
}
