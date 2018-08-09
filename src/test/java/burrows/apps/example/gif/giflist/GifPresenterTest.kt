package burrows.apps.example.gif.giflist

import burrows.apps.example.gif.data.RiffsyApiClient
import burrows.apps.example.gif.data.RiffsyApiClient.Companion.DEFAULT_LIMIT_COUNT
import burrows.apps.example.gif.data.model.RiffsyResponseDto
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.never
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import test.ImmediateSchedulerProvider

@RunWith(MockitoJUnitRunner::class)
class GifPresenterTest {
    private val provider = ImmediateSchedulerProvider()
    @Mock private lateinit var view: GifContract.View
    @Mock private lateinit var repository: RiffsyApiClient
    private lateinit var sut: GifPresenter

    @Before fun setUp() {
        `when`(view.isActive()).thenReturn(true)
    }

    @Test fun testLoadTrendingImageNotActive() {
        val next = 0.0
        val response = RiffsyResponseDto()
        `when`(view.isActive()).thenReturn(false)
        sut = GifPresenter(repository, provider)
        sut.takeView(view)
        `when`(repository.getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next)))
            .thenReturn(Observable.just(response))

        sut.loadTrendingImages(next)

        verify(view).isActive()
        verify(view, times(0)).addImages(eq(response))
    }

    @Test fun testLoadTrendingImagesSuccess() {
        val next = 0.0
        val response = RiffsyResponseDto()
        sut = GifPresenter(repository, provider)
        sut.takeView(view)
        `when`(repository.getTrendingResults(eq(DEFAULT_LIMIT_COUNT), eq(next)))
            .thenReturn(Observable.just(response))

        sut.loadTrendingImages(next)

        verify(view).isActive()
        verify(view).addImages(eq(response))
    }

    @Test fun testLoadSearchImagesSuccess() {
        val searchString = "gifs"
        val next = 0.0
        val response = RiffsyResponseDto()
        sut = GifPresenter(repository, provider)
        sut.takeView(view)
        `when`(repository.getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next)))
            .thenReturn(Observable.just(response))

        sut.loadSearchImages(searchString, next)

        verify(view).isActive()
        verify(view).addImages(eq(response))
    }

    @Test fun testLoadSearchImageViewInactive() {
        `when`(view.isActive()).thenReturn(false)
        val searchString = "gifs"
        val next = 0.0
        val response = RiffsyResponseDto()
        sut = GifPresenter(repository, provider)
        sut.takeView(view)
        `when`(repository.getSearchResults(eq(searchString), eq(DEFAULT_LIMIT_COUNT), eq(next)))
            .thenReturn(Observable.just(response))

        sut.loadSearchImages(searchString, next)

        verify(view, never()).addImages(any())
    }
}
