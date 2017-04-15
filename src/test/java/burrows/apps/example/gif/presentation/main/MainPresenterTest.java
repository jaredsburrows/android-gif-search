package burrows.apps.example.gif.presentation.main;

import burrows.apps.example.gif.data.rest.model.RiffsyResponse;
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient;
import burrows.apps.example.gif.presentation.IBaseSchedulerProvider;
import burrows.apps.example.gif.presentation.ImmediateSchedulerProvider;
import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import test.TestBase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class MainPresenterTest extends TestBase {
  private final IBaseSchedulerProvider provider = new ImmediateSchedulerProvider();
  @Mock private IMainView view;
  @Mock private RiffsyApiClient repository;
  private MainPresenter sut;

  @Before @Override public void setUp() throws Throwable {
    super.setUp();

    initMocks(this);

    when(view.isActive()).thenReturn(true);
  }

  @Test public void testLoadTrendingImagesNotActive() {
    // Arrange
    final RiffsyResponse response = new RiffsyResponse();
    when(view.isActive()).thenReturn(false);
    sut = new MainPresenter(view, repository, provider);
    when(repository.getTrendingResults(any(Integer.class), any(Float.class))).thenReturn(Observable.just(response));

    // Act
    sut.loadTrendingImages(0f);

    // Assert
    verify(view).isActive();
    verify(view, times(0)).addImages(eq(response));
  }

  @Test public void testLoadTrendingImagesSuccess() {
    // Arrange
    final RiffsyResponse response = new RiffsyResponse();
    sut = new MainPresenter(view, repository, provider);
    when(repository.getTrendingResults(any(Integer.class), any(Float.class))).thenReturn(Observable.just(response));

    // Act
    sut.loadTrendingImages(0f);

    // Assert
    verify(view).isActive();
    verify(view).addImages(eq(response));
  }

  @Test public void testLoadSearchImagesSuccess() {
    // Arrange
    final RiffsyResponse response = new RiffsyResponse();
    sut = new MainPresenter(view, repository, provider);
    when(repository.getSearchResults(any(String.class), any(Integer.class), any(Float.class))).thenReturn(Observable.just(response));

    // Act
    sut.loadSearchImages("gifs", 0f);

    // Assert
    verify(view).isActive();
    verify(view).addImages(eq(response));
  }
}
