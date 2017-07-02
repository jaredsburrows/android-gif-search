package burrows.apps.example.gif.presentation.adapter

import android.support.test.filters.SmallTest
import android.support.test.rule.UiThreadTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.ViewGroup
import android.widget.LinearLayout
import burrows.apps.example.gif.DummyActivity
import burrows.apps.example.gif.data.rest.repository.ImageRepository
import burrows.apps.example.gif.presentation.adapter.GifAdapter.OnItemClickListener
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.eq
import org.mockito.Mock
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations.initMocks
import test.AndroidTestBase
import test.CustomTestRule

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class GifAdapterTest : AndroidTestBase() {
  @Rule @JvmField val activityTestRule = CustomTestRule(DummyActivity::class.java, true, true)
  @Rule @JvmField val uiThreadTestRule = UiThreadTestRule()
  @Mock private lateinit var onItemClickListener: OnItemClickListener
  private val imageInfoModel = ImageInfoModel(AndroidTestBase.Companion.STRING_UNIQUE, null)
  private val imageInfoModel2 = ImageInfoModel(AndroidTestBase.Companion.STRING_UNIQUE2, null)
  private val imageInfoModel3 = ImageInfoModel(AndroidTestBase.Companion.STRING_UNIQUE3, null)
  private lateinit var viewHolder: GifAdapter.ViewHolder
  private lateinit var spyImageDownloader: ImageRepository
  private lateinit var sut: GifAdapter

  @Before @Throws(Throwable::class) override fun setUp() {
    super.setUp()

    activityTestRule.keepScreenOn()

    initMocks(this)

    spyImageDownloader = spy(ImageRepository(activityTestRule.targetContext))
    sut = GifAdapter(onItemClickListener, spyImageDownloader)
    sut.add(imageInfoModel)
    sut.add(imageInfoModel2)
    // Must be created on UI thread
    uiThreadTestRule.runOnUiThread { viewHolder = sut.onCreateViewHolder(LinearLayout(activityTestRule.targetContext), 0) }
  }

  @Test fun testOnCreateViewHolder() {
    // Arrange
    val parent = object : ViewGroup(activityTestRule.targetContext) {
      override fun onLayout(b: Boolean, i: Int, i1: Int, i2: Int, i3: Int) {}
    }

    // Assert
    // Must be created on UI thread
    uiThreadTestRule.runOnUiThread { assertThat(sut.onCreateViewHolder(parent, 0)).isInstanceOf(GifAdapter.ViewHolder::class.java) }
  }

  @Test fun testOnBindViewHolderOnAdapterItemClick() {
    // Arrange
    sut.clear()
    sut.add(imageInfoModel)
    sut.add(imageInfoModel2)
    sut.add(ImageInfoModel())

    // Act
    uiThreadTestRule.runOnUiThread { sut.onBindViewHolder(viewHolder, 0) }

    // Assert
    assertThat(viewHolder.itemView.performClick()).isTrue()
    verify(spyImageDownloader, atLeastOnce()).load(eq(AndroidTestBase.Companion.STRING_UNIQUE))
    verify(onItemClickListener).onClick(eq(imageInfoModel))
  }

  @Test fun testGetItem() {
    // Arrange
    sut.clear()

    // Act
    val imageInfo = ImageInfoModel()
    sut.add(imageInfo)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfo)
  }

  @Test fun onViewRecycled() {
    // Arrange
    sut.add(ImageInfoModel())

    // Act
    uiThreadTestRule.runOnUiThread {
      sut.onBindViewHolder(viewHolder, 0)
      sut.onViewRecycled(viewHolder)
    }
  }

  @Test fun testGetItemCountShouldReturnCorrectValues() {
    // Assert
    assertThat(sut.itemCount).isEqualTo(2)
  }

  @Test fun testGetListCountShouldReturnCorrectValues() {
    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel)
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2)
  }

  @Test fun testGetItemShouldReturnCorrectValues() {
    // Assert
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2)
  }

  @Test fun testGetLocationShouldReturnCorrectValues() {
    // Assert
    assertThat(sut.getLocation(imageInfoModel2)).isEqualTo(1)
  }

  @Test fun testClearShouldClearAdapter() {
    sut.clear()

    // Assert
    assertThat(sut.itemCount).isEqualTo(0)
  }

  @Test fun testAddObjectShouldReturnCorrectValues() {
    sut.add(imageInfoModel3)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel)
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2)
    assertThat(sut.getItem(2)).isEqualTo(imageInfoModel3)
  }

  @Test fun testAddCollectionShouldReturnCorrectValues() {
    val imageInfos = listOf(imageInfoModel3)

    sut.addAll(imageInfos)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel)
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2)
    assertThat(sut.getItem(2)).isEqualTo(imageInfoModel3)
  }

  @Test fun testAddLocationObjectShouldReturnCorrectValues() {
    sut.add(0, ImageInfoModel(AndroidTestBase.Companion.STRING_UNIQUE3, null))

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel3)
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel)
    assertThat(sut.getItem(2)).isEqualTo(imageInfoModel2)
  }

  @Test fun testRemoveLocationObjectShouldReturnCorrectValues() {
    sut.remove(0, imageInfoModel)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel2)
  }

  @Test fun testRemoveObjectShouldReturnCorrectValues() {
    sut.remove(imageInfoModel)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel2)
  }

  @Test fun testRemoveLocationShouldReturnCorrectValues() {
    sut.remove(0)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel2)
  }
}
