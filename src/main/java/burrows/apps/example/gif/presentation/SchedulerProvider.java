package burrows.apps.example.gif.presentation;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class SchedulerProvider implements BaseSchedulerProvider {
  @Override public Scheduler io() {
    return Schedulers.io();
  }

  @Override public Scheduler ui() {
    return AndroidSchedulers.mainThread();
  }
}
