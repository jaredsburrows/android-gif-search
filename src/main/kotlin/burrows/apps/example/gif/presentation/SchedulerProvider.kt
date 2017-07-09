package burrows.apps.example.gif.presentation

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class SchedulerProvider @Inject constructor() : IBaseSchedulerProvider {
  override fun io(): Scheduler {
    return Schedulers.io()
  }

  override fun ui(): Scheduler {
    return AndroidSchedulers.mainThread()
  }
}
