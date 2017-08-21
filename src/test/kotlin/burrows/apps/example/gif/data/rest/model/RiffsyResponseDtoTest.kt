package burrows.apps.example.gif.data.rest.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class RiffsyResponseDtoTest : TestBase() {
  private val TEST_RESULTS = arrayListOf<ResultDto>()
  private var sut = RiffsyResponseDto().apply { results = TEST_RESULTS; page = INT_RANDOM }

  @Test fun testGetResults() {
    assertThat(sut.results).isEqualTo(TEST_RESULTS)
  }

  @Test fun testGetNext() {
    assertThat(sut.page).isEqualTo(INT_RANDOM)
  }
}
