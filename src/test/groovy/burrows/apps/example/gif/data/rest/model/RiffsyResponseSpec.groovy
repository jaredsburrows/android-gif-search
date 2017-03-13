package burrows.apps.example.gif.data.rest.model

import test.BaseSpec

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class RiffsyResponseSpec extends BaseSpec {
  List<Result> results = []
  RiffsyResponse sut = new RiffsyResponse.Builder().results(results).page(FLOAT_RANDOM).build()

  def "get results"() {
    expect:
    sut.results() == results
  }

  def "set results"() {
    given:
    List<Result> expected = []
    sut = sut.newBuilder().results(expected).build()

    expect:
    sut.results() == expected
  }

  def "get next"() {
    expect:
    sut.page() == FLOAT_RANDOM
  }

  def "set next"() {
    given:
    float expected = FLOAT_RANDOM + 1
    sut = sut.newBuilder().page(expected).build()

    expect:
    sut.page() == expected
  }
}
