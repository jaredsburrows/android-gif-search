package burrows.apps.example.gif.giflist

import burrows.apps.example.gif.data.model.RiffsyResponseDto
import burrows.apps.example.gif.data.RiffsyApiClient
import burrows.apps.example.gif.data.RiffsyApiClient.Companion.DEFAULT_LIMIT_COUNT
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.never
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations.initMocks
import test.ImmediateSchedulerProvider

class GifPresenterTest {
  private val provider = ImmediateSchedulerProvider()
  @Mock private lateinit var view: GifContract.View
  @Mock private lateinit var repository: RiffsyApiClient
  private lateinit var sut: GifPresenter

  @Before fun setUp() {
    initMocks(this)

    `when`(view.isActive()).thenReturn(true)
  }

  @Test fun testLoadTrendingImagesNotActive() {
    // Arrange
    val next = 0.0
    val response = RiffsyResponseDto()
    `when`(view.isActive()).thenReturn(false)
    sut = GifPresenter(repository, provider)
    sut.takeView(view)
    `when`(repository.getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Observable.just(response))

    // Act
    sut.loadTrendingImages(next)

    // Assert
    verify(view).isActive()
    verify(view, times(0)).addImages(eq(response))
  }

  @Test fun testLoadTrendingImagesSuccess() {
    // Arrange
    val next = 0.0
    val response = RiffsyResponseDto()
    sut = GifPresenter(repository, provider)
    sut.takeView(view)
    `when`(repository.getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Observable.just(response))

    // Act
    sut.loadTrendingImages(next)

    // Assert
    verify(view).isActive()
    verify(view).addImages(eq(response))
  }

  @Test fun testLoadSearchImagesSuccess() {
    // Arrange
    val searchString = "gifs"
    val next = 0.0
    val response = RiffsyResponseDto()
    sut = GifPresenter(repository, provider)
    sut.takeView(view)
    `when`(repository.getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Observable.just(response))

    // Act
    sut.loadSearchImages(searchString, next)

    // Assert
    verify(view).isActive()
    verify(view).addImages(eq(response))
  }

  @Test fun testLoadSearchImagesViewInactive() {
    // Arrange
    `when`(view.isActive()).thenReturn(false)
    val searchString = "gifs"
    val next = 0.0
    val response = RiffsyResponseDto()
    sut = GifPresenter(repository, provider)
    sut.takeView(view)
    `when`(repository.getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next)))
      .thenReturn(Observable.just(response))

    // Act
    sut.loadSearchImages(searchString, next)

    // Assert
    verify(view, never()).addImages(any())
  }
}
