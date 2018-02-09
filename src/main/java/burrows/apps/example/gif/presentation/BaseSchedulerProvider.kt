package burrows.apps.example.gif.presentation

import io.reactivex.Scheduler

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
interface BaseSchedulerProvider {
  fun io(): Scheduler
  fun ui(): Scheduler
}
