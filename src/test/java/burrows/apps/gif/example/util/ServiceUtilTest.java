package burrows.apps.gif.example.util;

import burrows.apps.gif.example.rest.service.RiffsyService.RiffsyServiceApi;
import org.junit.Test;
import test.ServiceTestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ServiceUtilTest extends ServiceTestBase {
  @Test public void testCreateService() {
    assertThat(ServiceUtil.createService(RiffsyServiceApi.class, mockEndPoint))
      .isInstanceOf(RiffsyServiceApi.class);
  }
}
