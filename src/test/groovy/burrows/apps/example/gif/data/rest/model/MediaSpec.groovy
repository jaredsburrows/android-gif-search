package burrows.apps.example.gif.data.rest.model

import test.BaseSpec

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class MediaSpec extends BaseSpec {
  Gif gif = new Gif()
  Media sut = new Media.Builder().gif(gif).build()

  def "get gif"() {
    expect:
    sut.gif() == gif
  }

  def "set gif"() {
    given:
    Gif expected = new Gif()
    sut = sut.newBuilder().gif(expected).build()

    expect:
    sut.gif() == expected
  }
}
