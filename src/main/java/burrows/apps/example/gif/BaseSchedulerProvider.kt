package burrows.apps.example.gif

import io.reactivex.Scheduler

interface BaseSchedulerProvider {
  fun io(): Scheduler
  fun ui(): Scheduler
}
