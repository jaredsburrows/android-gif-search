package burrows.apps.example.gif.presentation.adapter

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import android.view.ViewGroup
import android.widget.LinearLayout
import burrows.apps.example.gif.data.ImageService
import burrows.apps.example.gif.presentation.adapter.GifAdapter.OnItemClickListener
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import test.TestBase

@SmallTest
@RunWith(AndroidJUnit4::class)
class GifAdapterTest : TestBase() {
  private val targetContext: Context = InstrumentationRegistry.getTargetContext()
  private val imageInfoModel = ImageInfoModel().apply { url = STRING_UNIQUE }
  private val imageInfoModel2 = ImageInfoModel().apply { url = STRING_UNIQUE2 }
  private val imageInfoModel3 = ImageInfoModel().apply { url = STRING_UNIQUE3 }
  private lateinit var onItemClickListener: TestOnItemClickListener
  private lateinit var imageService: ImageService
  private lateinit var viewHolder: GifAdapter.ViewHolder
  private lateinit var sut: GifAdapter

  @Before @UiThreadTest override fun setUp() {
    super.setUp()

    onItemClickListener = TestOnItemClickListener()
    imageService = ImageService(targetContext)
    sut = GifAdapter(onItemClickListener, imageService)
    sut.add(imageInfoModel)
    sut.add(imageInfoModel2)
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
    sut.add(imageInfoModel)
    sut.add(imageInfoModel2)
    sut.add(ImageInfoModel())

    // Act
    sut.onBindViewHolder(viewHolder, 0)

    // Assert
    assertThat(viewHolder.itemView.performClick()).isTrue()
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

  @Test @UiThreadTest fun onViewRecycled() {
    // Arrange
    sut.add(ImageInfoModel())

    // Act
    sut.onBindViewHolder(viewHolder, 0)
    sut.onViewRecycled(viewHolder)
  }

  @Test fun testGetItemCountShouldReturnCorrectValues() {
    assertThat(sut.itemCount).isEqualTo(2)
  }

  @Test fun testGetListCountShouldReturnCorrectValues() {
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel)
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2)
  }

  @Test fun testGetItemShouldReturnCorrectValues() {
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2)
  }

  @Test fun testClearShouldClearAdapter() {
    // Act
    sut.clear()

    // Assert
    assertThat(sut.itemCount).isEqualTo(0)
  }

  @Test fun testAddObjectShouldReturnCorrectValues() {
    // Act
    sut.add(imageInfoModel3)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel)
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2)
    assertThat(sut.getItem(2)).isEqualTo(imageInfoModel3)
  }

  @Test fun testAddCollectionShouldReturnCorrectValues() {
    // Arrange
    val imageInfos = listOf(imageInfoModel3)

    // Act
    sut.addAll(imageInfos)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel)
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel2)
    assertThat(sut.getItem(2)).isEqualTo(imageInfoModel3)
  }

  @Test fun testAddLocationObjectShouldReturnCorrectValues() {
    // Act
    sut.add(0, imageInfoModel3)

    // Assert
    assertThat(sut.getItem(0)).isEqualTo(imageInfoModel3)
    assertThat(sut.getItem(1)).isEqualTo(imageInfoModel)
    assertThat(sut.getItem(2)).isEqualTo(imageInfoModel2)
  }

  private class TestOnItemClickListener : OnItemClickListener {
    override fun onClick(imageInfoModel: ImageInfoModel) {
    }
  }
}
