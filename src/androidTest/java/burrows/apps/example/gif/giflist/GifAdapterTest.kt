package burrows.apps.example.gif.giflist

import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.view.ViewGroup
import android.widget.LinearLayout
import burrows.apps.example.gif.data.ImageService
import burrows.apps.example.gif.giflist.GifAdapter.OnItemClickListener
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class GifAdapterTest {
    private val targetContext = InstrumentationRegistry.getTargetContext()
    private val gifImageInfo = GifImageInfo(url = "http://some.url")
    private val gifImageInfo2 = GifImageInfo(url = "http://some.url2")
    private val gifImageInfo3 = GifImageInfo(url = "http://some.url3")
    private val testOnItemClickListener = object : OnItemClickListener {
        override fun onClick(imageInfoModel: GifImageInfo) {
        }
    }
    private lateinit var imageService: ImageService
    private lateinit var viewHolder: GifAdapter.ViewHolder
    private lateinit var sut: GifAdapter

    @Before @UiThreadTest fun setUp() {
        imageService = ImageService(targetContext)
        sut = GifAdapter(testOnItemClickListener, imageService).apply {
            add(gifImageInfo)
            add(gifImageInfo2)
        }
        viewHolder = sut.onCreateViewHolder(LinearLayout(targetContext), 0)
    }

    @Test @UiThreadTest fun testOnCreateViewHolder() {
        val parent = object : ViewGroup(targetContext) {
            override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {}
        }

        assertThat(sut.onCreateViewHolder(parent, 0))
            .isInstanceOf(GifAdapter.ViewHolder::class.java)
    }

    @Test @UiThreadTest fun testOnBindViewHolderOnAdapterItemClick() {
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

    @Test @UiThreadTest fun onViewRecycled() {
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
