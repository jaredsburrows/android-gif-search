package burrows.apps.example.gif.presentation;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class SchedulerProviderTest {
  private final SchedulerProvider sut = new SchedulerProvider();

  @Test public void testIo() {
    assertThat(sut.io()).isEqualTo(Schedulers.io());
  }

  @Test public void testUi() {
    assertThat(sut.ui()).isEqualTo(AndroidSchedulers.mainThread());
  }
}
