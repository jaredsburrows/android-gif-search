package burrows.apps.giphy.example.rx;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class RxBus {
    private final Subject<Object, Object> mBus = new SerializedSubject<>(PublishSubject.create());

    public void send(final Object event) {
        this.mBus.onNext(event);
    }

    public Observable<Object> toObservable() {
        return this.mBus;
    }

    public boolean hasObservers() {
        return this.mBus.hasObservers();
    }
}
