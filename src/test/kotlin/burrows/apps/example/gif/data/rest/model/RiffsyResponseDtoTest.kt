package burrows.apps.example.gif.data.rest.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class RiffsyResponseDtoTest : TestBase() {
  private val resultsTest = arrayListOf<ResultDto>()
  private var sut = RiffsyResponseDto().apply { results = resultsTest; page = FLOAT_RANDOM }

  @Test fun testGetResults() {
    assertThat(sut.results).isEqualTo(resultsTest)
  }

  @Test fun testGetNext() {
    assertThat(sut.page).isEqualTo(FLOAT_RANDOM)
  }
}
