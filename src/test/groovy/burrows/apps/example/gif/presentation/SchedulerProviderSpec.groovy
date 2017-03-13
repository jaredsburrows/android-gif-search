package burrows.apps.example.gif.presentation

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import test.BaseSpec

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class SchedulerProviderSpec extends BaseSpec {
  SchedulerProvider sut = new SchedulerProvider()

  def "io"() {
    expect:
    sut.io() == Schedulers.io()
  }

  def "ui"() {
    expect:
    sut.ui() == AndroidSchedulers.mainThread()
  }
}
