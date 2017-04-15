package burrows.apps.example.gif.presentation;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ImmediateSchedulerProvider implements IBaseSchedulerProvider {
  @Override public Scheduler io() {
    return Schedulers.trampoline();
  }

  @Override public Scheduler ui() {
    return Schedulers.trampoline();
  }
}
