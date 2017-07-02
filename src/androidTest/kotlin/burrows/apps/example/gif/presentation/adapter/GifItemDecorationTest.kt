package burrows.apps.example.gif.presentation.adapter

import android.graphics.Rect
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import burrows.apps.example.gif.DummyActivity
import burrows.apps.example.gif.data.rest.repository.ImageApiRepository
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations.initMocks
import test.AndroidTestBase
import test.CustomTestRule

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class GifItemDecorationTest : AndroidTestBase() {
  @Rule @JvmField val activityTestRule = CustomTestRule(DummyActivity::class.java, true, true)
  @Mock private lateinit var state: RecyclerView.State
  @Mock private lateinit var onItemClickListener: GifAdapter.OnItemClickListener
  @Mock private lateinit var imageDownloader: ImageApiRepository
  @Mock private lateinit var layoutParams: RecyclerView.LayoutParams
  private val rect = Rect(0, 0, 0, 0)
  private lateinit var layoutManager: GridLayoutManager
  private lateinit var recyclerView: RecyclerView
  private lateinit var sut: GifItemDecoration

  @Before override fun setUp() {
    super.setUp()

    initMocks(this)

    layoutManager = GridLayoutManager(activityTestRule.targetContext, 3)

    val adapter = GifAdapter(onItemClickListener, imageDownloader)
    recyclerView = RecyclerView(activityTestRule.targetContext)

    // Setup RecyclerView
    recyclerView.adapter = adapter
    recyclerView.layoutManager = layoutManager

    // Add fake data
    for (i in 0..RiffsyApiClient.DEFAULT_LIMIT_COUNT - 1) {
      adapter.add(ImageInfoModel())
    }

    // Increase the childcount
    recyclerView.addView(AppCompatTextView(activityTestRule.targetContext))

    `when`(layoutParams.viewLayoutPosition).thenReturn(0)
    recyclerView.layoutParams = layoutParams
  }

  @Test fun testGetItemOffsetsContextResId() {
    // Arrange
    sut = GifItemDecoration(AndroidTestBase.INTEGER_RANDOM, layoutManager.spanCount)
    recyclerView.addItemDecoration(sut)

    // Act
    sut.getItemOffsets(rect, recyclerView, recyclerView, state)
  }
}
