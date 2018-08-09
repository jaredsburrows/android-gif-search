package burrows.apps.example.gif

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SchedulerProviderTest {
  private val sut = SchedulerProvider()

  @Test fun testIo() {
    assertThat(sut.io()).isEqualTo(Schedulers.io())
  }

  @Test fun testUi() {
    assertThat(sut.ui()).isEqualTo(AndroidSchedulers.mainThread())
  }
}
