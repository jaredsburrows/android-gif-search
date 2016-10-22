package burrows.apps.example.gif.presentation.adapter.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import test.TestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ImageInfoTest extends TestBase {
  private ImageInfo sut = new ImageInfo.Builder().url(STRING_UNIQUE).build();

  @Test public void testGetUrl() {
    assertThat(sut.url()).isEqualTo(STRING_UNIQUE);
  }

  @Test public void testSetUrl() {
    sut = sut.newBuilder().url(STRING_UNIQUE2).build();

    assertThat(sut.url()).isEqualTo(STRING_UNIQUE2);
  }

  @Test public void testEqualsGashCode() {
    EqualsVerifier.forClass(ImageInfo.class)
      .withPrefabValues(ImageInfo.class, new ImageInfo(), new ImageInfo.Builder().url(STRING_UNIQUE).build())
      .suppress(Warning.NONFINAL_FIELDS)
      .verify();
  }
}
