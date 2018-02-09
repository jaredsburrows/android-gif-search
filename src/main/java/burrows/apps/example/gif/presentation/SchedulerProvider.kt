package burrows.apps.example.gif.presentation

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class SchedulerProvider @Inject constructor() : BaseSchedulerProvider {
  override fun io(): Scheduler = Schedulers.io()
  override fun ui(): Scheduler = AndroidSchedulers.mainThread()
}
