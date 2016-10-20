package burrows.apps.example.gif.presentation;

import io.reactivex.Scheduler;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public interface BaseSchedulerProvider {
  Scheduler io();
  Scheduler ui();
}
