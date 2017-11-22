package test

import burrows.apps.example.gif.presentation.IBaseSchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class ImmediateSchedulerProvider : IBaseSchedulerProvider {
  override fun io(): Scheduler = Schedulers.trampoline()
  override fun ui(): Scheduler = Schedulers.trampoline()
}
