package burrows.apps.example.gif.presentation.main

import burrows.apps.example.gif.data.rest.model.RiffsyResponse
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations.initMocks
import test.ImmediateSchedulerProvider
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class MainPresenterTest : TestBase() {
  private val provider = ImmediateSchedulerProvider()
  @Mock private val view: IMainView? = null
  @Mock private val repository: RiffsyApiClient? = null
  private var sut: MainPresenter? = null

  @Before @Throws(Throwable::class)
  override fun setUp() {
    super.setUp()

    initMocks(this)

    `when`(view!!.isActive).thenReturn(true)
  }

  @Test fun testLoadTrendingImagesNotActive() {
    // Arrange
    val next = 0f
    val response = RiffsyResponse()
    `when`(view!!.isActive).thenReturn(false)
    sut = MainPresenter(view, repository!!, provider)
    `when`(repository.getTrendingResults(eq(RiffsyApiClient.DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Observable.just(response))

    // Act
    sut!!.loadTrendingImages(next)

    // Assert
    verify(view).isActive
    verify(view, times(0)).addImages(eq(response))
  }

  @Test fun testLoadTrendingImagesSuccess() {
    // Arrange
    val next = 0f
    val response = RiffsyResponse()
    sut = MainPresenter(view!!, repository!!, provider)
    `when`(repository.getTrendingResults(eq(RiffsyApiClient.DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Observable.just(response))

    // Act
    sut!!.loadTrendingImages(next)

    // Assert
    verify(view)!!.isActive
    verify(view)!!.addImages(eq(response))
  }

  @Test fun testLoadSearchImagesSuccess() {
    // Arrange
    val searchString = "gifs"
    val next = 0f
    val response = RiffsyResponse()
    sut = MainPresenter(view!!, repository!!, provider)
    `when`(repository.getSearchResults(eq(searchString), eq(RiffsyApiClient.DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Observable.just(response))

    // Act
    sut!!.loadSearchImages(searchString, next)

    // Assert
    verify(view)!!.isActive
    verify(view)!!.addImages(eq(response))
  }
}
