package burrows.apps.example.gif.presentation.adapter.model;

import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import test.TestBase;

import static nl.jqno.equalsverifier.EqualsVerifier.forClass;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ImageInfoTest extends TestBase {
  private ImageInfoModel sut = new ImageInfoModel.Builder().url(STRING_UNIQUE).previewUrl(STRING_UNIQUE2).build();

  @Test public void testGetUrl() {
    assertThat(sut.url()).isEqualTo(STRING_UNIQUE);
  }

  @Test public void testSetUrl() {
    sut = sut.newBuilder().url(STRING_UNIQUE2).build();

    assertThat(sut.url()).isEqualTo(STRING_UNIQUE2);
  }

  @Test public void testGetUrlPreview() {
    assertThat(sut.previewUrl()).isEqualTo(STRING_UNIQUE2);
  }

  @Test public void testSetUrlPreview() {
    sut = sut.newBuilder().previewUrl(STRING_UNIQUE3).build();

    assertThat(sut.previewUrl()).isEqualTo(STRING_UNIQUE3);
  }

  @Test public void testEqualsGashCode() {
    forClass(ImageInfoModel.class)
      .withPrefabValues(ImageInfoModel.class, new ImageInfoModel(), new ImageInfoModel.Builder().url(STRING_UNIQUE).build())
      .suppress(Warning.NONFINAL_FIELDS)
      .verify();
  }
}
