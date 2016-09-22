package burrows.apps.gif.example.rx;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class RxBus {
  private final PublishSubject<Object> bus = PublishSubject.create();

  public void send(final Object event) {
    bus.onNext(event);
  }

  public Observable<Object> toObservable() {
    return bus;
  }

  public boolean hasObservers() {
    return bus.hasObservers();
  }
}
