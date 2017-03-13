package burrows.apps.example.gif.data.rest.model

import test.BaseSpec

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class GifSpec extends BaseSpec {
  Gif sut = new Gif.Builder().url(STRING_UNIQUE).preview(STRING_UNIQUE2).build()

  def "get url"() {
    expect:
    sut.url() == STRING_UNIQUE
  }

  def "set url"() {
    given:
    sut = sut.newBuilder().url(STRING_UNIQUE2).build()

    expect:
    sut.url() == STRING_UNIQUE2
  }

  def "get url preview"() {
    expect:
    sut.preview() == STRING_UNIQUE2
  }

  def "set url preview"() {
    given:
    sut = sut.newBuilder().preview(STRING_UNIQUE3).build()

    expect:
    sut.preview() == STRING_UNIQUE3
  }
}
