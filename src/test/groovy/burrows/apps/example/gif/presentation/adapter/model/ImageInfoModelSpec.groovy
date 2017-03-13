package burrows.apps.example.gif.presentation.adapter.model

import test.BaseSpec

import static nl.jqno.equalsverifier.EqualsVerifier.forClass
import static nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class ImageInfoModelSpec extends BaseSpec {
  ImageInfoModel sut = new ImageInfoModel.Builder().url(STRING_UNIQUE).
          previewUrl(STRING_UNIQUE2).
          build()

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
    sut.previewUrl() == STRING_UNIQUE2
  }

  def "set url preview"() {
    given:
    sut = sut.newBuilder().previewUrl(STRING_UNIQUE3).build()

    expect:
    sut.previewUrl() == STRING_UNIQUE3
  }

  def "equals hash code"() {
    when:
    forClass(ImageInfoModel.class)
            .withPrefabValues(ImageInfoModel.class, new ImageInfoModel(),
            new ImageInfoModel.Builder().url(STRING_UNIQUE).build())
            .suppress(NONFINAL_FIELDS)
            .verify()

    then:
    noExceptionThrown()
  }
}
