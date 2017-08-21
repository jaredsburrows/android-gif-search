package burrows.apps.example.gif.presentation.main

import burrows.apps.example.gif.data.rest.model.RiffsyResponseDto
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient.Companion.DEFAULT_LIMIT_COUNT
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
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
  @Mock private lateinit var view: IMainView
  @Mock private lateinit var repository: RiffsyApiClient
  private lateinit var sut: MainPresenter

  @Before override fun setUp() {
    super.setUp()

    initMocks(this)

    `when`(view.isActive()).thenReturn(true)
  }

  @Test fun testLoadTrendingImagesNotActive() {
    // Arrange
    val next = 0
    val response = RiffsyResponseDto()
    `when`(view.isActive()).thenReturn(false)
    sut = MainPresenter(view, repository, provider)
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
    val next = 0
    val response = RiffsyResponseDto()
    sut = MainPresenter(view, repository, provider)
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
    val next = 0
    val response = RiffsyResponseDto()
    sut = MainPresenter(view, repository, provider)
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
    sut = MainPresenter(view, repository, provider)
    val response = RiffsyResponseDto()
    val observable = Observable.just(response)

    // Act
    sut.loadImages(observable)

    // Assert
    verify(view, never()).addImages(any())
  }
}
