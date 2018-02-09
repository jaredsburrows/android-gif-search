package burrows.apps.example.gif.presentation

import io.reactivex.Scheduler

interface BaseSchedulerProvider {
  fun io(): Scheduler
  fun ui(): Scheduler
}
