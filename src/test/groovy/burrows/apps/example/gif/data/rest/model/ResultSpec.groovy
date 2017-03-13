package burrows.apps.example.gif.data.rest.model

import test.BaseSpec

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class ResultSpec extends BaseSpec {
  List<Media> medias = []
  Result sut = new Result.Builder().media(medias).title(STRING_UNIQUE).build()

  def "get media"() {
    expect:
    sut.media() == medias
  }

  def "set media"() {
    given:
    List<Media> expected = []
    sut = sut.newBuilder().media(expected).build()

    expect:
    sut.media() == expected
  }

  def "get title"() {
    expect:
    sut.title() == STRING_UNIQUE
  }

  def "set title"() {
    given:
    sut = sut.newBuilder().title(STRING_UNIQUE2).build()

    expect:
    sut.title() == STRING_UNIQUE2
  }
}
