package burrows.apps.example.gif

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

open class SchedulerProvider @Inject constructor() : BaseSchedulerProvider {
  override fun io() = Schedulers.io()
  override fun ui() = AndroidSchedulers.mainThread()
}
