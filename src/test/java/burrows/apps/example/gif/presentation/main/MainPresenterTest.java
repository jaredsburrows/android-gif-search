package burrows.apps.example.gif.presentation.main;

import burrows.apps.example.gif.data.rest.model.RiffsyResponse;
import burrows.apps.example.gif.data.rest.repository.RiffsyRepository;
import burrows.apps.example.gif.presentation.BaseSchedulerProvider;
import burrows.apps.example.gif.presentation.ImmediateSchedulerProvider;
import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import test.TestBase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class MainPresenterTest extends TestBase {
  private final BaseSchedulerProvider provider = new ImmediateSchedulerProvider();
  @Mock private MainContract.View view;
  @Mock private RiffsyRepository repository;
  private MainPresenter sut;

  @Before @Override public void setUp() throws Exception {
    super.setUp();

    initMocks(this);

    when(view.isActive()).thenReturn(true);
  }

  @Test public void testLoadTrendingImagesSuccess() {
    sut = new MainPresenter(view, repository, provider);
    when(repository.getTrendingResults(any(Integer.class), any(Float.class))).thenReturn(Observable.just(new RiffsyResponse()));

    sut.loadTrendingImages(0f);

    verify(repository).getTrendingResults(any(Integer.class), any(Float.class));
    verify(view).isActive();
    verify(view).addImages(any(RiffsyResponse.class));
  }

  @Test public void testLoadSearchImagesSuccess() {
    sut = new MainPresenter(view, repository, provider);
    when(repository.getSearchResults(any(String.class), any(Integer.class), any(Float.class))).thenReturn(Observable.just(new RiffsyResponse()));

    sut.loadSearchImages("gifs", 0f);

    verify(repository).getSearchResults(any(String.class), any(Integer.class), any(Float.class));
    verify(view).isActive();
    verify(view).addImages(any(RiffsyResponse.class));
  }
}
