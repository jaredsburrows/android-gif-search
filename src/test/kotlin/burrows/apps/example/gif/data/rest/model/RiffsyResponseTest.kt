package burrows.apps.example.gif.data.rest.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import test.TestBase

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class RiffsyResponseTest : TestBase() {
  private val results = ArrayList<Result>()
  private var sut = RiffsyResponse.Builder().results(results).page(TestBase.FLOAT_RANDOM).build()

  @Test fun testGetResults() {
    // Assert
    assertThat(sut.results()).isEqualTo(results)
  }

  @Test fun testSetResults() {
    // Arrange
    val expected = ArrayList<Result>()

    // Act
    sut = sut.newBuilder().results(expected).build()

    // Assert
    assertThat(sut.results()).isEqualTo(expected)
  }

  @Test fun testGetNext() {
    // Assert
    assertThat(sut.page()).isEqualTo(TestBase.FLOAT_RANDOM)
  }

  @Test fun testSetNext() {
    // Act
    sut = sut.newBuilder().page(TestBase.FLOAT_RANDOM + 1).build()

    // Assert
    assertThat(sut.page()).isEqualTo(TestBase.FLOAT_RANDOM + 1)
  }
}
