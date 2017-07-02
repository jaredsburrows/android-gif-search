package burrows.apps.example.gif.data.rest.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class RiffsyResponseTest : TestBase() {
  private val resultsTest = arrayListOf<Result>()
  private var sut = RiffsyResponse().apply { results = resultsTest; page = FLOAT_RANDOM }

  @Test fun testGetResults() {
    // Assert
    assertThat(sut.results).isEqualTo(resultsTest)
  }

  @Test fun testGetNext() {
    // Assert
    assertThat(sut.page).isEqualTo(FLOAT_RANDOM)
  }
}
