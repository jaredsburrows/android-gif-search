package test

import com.burrowsapps.example.gif.SchedulerProvider
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TestImmediateSchedulerProvider @Inject constructor() : SchedulerProvider() {
  override fun io() = Schedulers.trampoline()
  override fun ui() = Schedulers.trampoline()
}
