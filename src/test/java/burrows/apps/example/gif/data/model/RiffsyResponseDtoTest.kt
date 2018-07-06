package burrows.apps.example.gif.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RiffsyResponseDtoTest {
  private val nextResponse = 1.0
  private val results = arrayListOf<ResultDto>()
  private var sutDefault = RiffsyResponseDto()
  private var sut = RiffsyResponseDto(results = results, next = nextResponse)

  @Test fun `test Get Results`() {
    assertThat(sutDefault.results).isEmpty()
    assertThat(sut.results).isEqualTo(results)
  }

  @Test fun `test Get Next`() {
    assertThat(sutDefault.next).isNull()
    assertThat(sut.next).isEqualTo(nextResponse)
  }
}
