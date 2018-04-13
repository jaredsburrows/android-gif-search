package burrows.apps.example.gif.giflist

import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import android.view.ViewGroup
import android.widget.LinearLayout
import burrows.apps.example.gif.data.ImageService
import burrows.apps.example.gif.giflist.GifAdapter.OnItemClickListener
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class GifAdapterTest {
  private val targetContext = InstrumentationRegistry.getTargetContext()
  private val gifImageInfo = GifImageInfo(url = "http://some.url")
  private val gifImageInfo2 = GifImageInfo(url = "http://some.url2")
  private val gifImageInfo3 = GifImageInfo(url = "http://some.url3")
  private val testOnItemClickListener = object: OnItemClickListener {
    override fun onClick(imageInfoModel: GifImageInfo) {
    }
  }
  private lateinit var imageService: ImageService
  private lateinit var viewHolder: GifAdapter.ViewHolder
  private lateinit var sut: GifAdapter

  @Before @UiThreadTest fun setUp() {
    imageService = ImageService(targetContext)
    sut = GifAdapter(testOnItemClickListener, imageService)
    sut.add(gifImageInfo)
    sut.add(gifImageInfo2)
    viewHolder = sut.onCreateViewHolder(LinearLayout(targetContext), 0)
  }

  @Test @UiThreadTest fun testOnCreateViewHolder() {
    // Arrange
    val parent = object : ViewGroup(targetContext) {
      override fun onLayout(b: Boolean, i: Int, i1: Int, i2: Int, i3: Int) {}
    }

    // Assert
    assertThat(sut.onCreateViewHolder(parent, 0)).isInstanceOf(GifAdapter.ViewHolder::class.java)
  }

  @Test @UiThreadTest fun testOnBindViewHolderOnAdapterItemClick() {
    // Arrange
    sut.clear()
    sut.add(gifImageInfo)
    sut.add(gifImageInfo2)
    sut.add(GifImageInfo())

    // Act
    sut.onBindViewHolder(viewHolder, 0)

    // Assert
    assertThat(viewHolder.itemView.performClick()).isTrue()
  }

  @Test fun testGetItem() {
    // Arrange
    sut.clear()

    // Act
    val imageInfo = GifImageInfo()
    sut.add(imageInfo)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfo)
  }

  @Test @UiThreadTest fun onViewRecycled() {
    // Arrange
    sut.add(GifImageInfo())

    // Act
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
    // Act
    sut.clear()

    // Assert
    assertThat(sut.itemCount).isEqualTo(0)
  }

  @Test fun testAddObjectShouldReturnCorrectValues() {
    // Act
    sut.add(gifImageInfo3)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(gifImageInfo)
    assertThat(sut.getItem(1)).isEqualTo(gifImageInfo2)
    assertThat(sut.getItem(2)).isEqualTo(gifImageInfo3)
  }

  @Test fun testAddCollectionShouldReturnCorrectValues() {
    // Arrange
    val imageInfos = listOf(gifImageInfo3)

    // Act
    sut.addAll(imageInfos)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(gifImageInfo)
    assertThat(sut.getItem(1)).isEqualTo(gifImageInfo2)
    assertThat(sut.getItem(2)).isEqualTo(gifImageInfo3)
  }

  @Test fun testAddLocationObjectShouldReturnCorrectValues() {
    // Act
    sut.add(0, gifImageInfo3)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(gifImageInfo3)
    assertThat(sut.getItem(1)).isEqualTo(gifImageInfo)
    assertThat(sut.getItem(2)).isEqualTo(gifImageInfo2)
  }
}
