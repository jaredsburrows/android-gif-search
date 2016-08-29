package burrows.apps.giphy.example.util;

import burrows.apps.giphy.example.rest.service.GiphyService;
import org.junit.Test;
import test.ServiceTestBase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class ServiceUtilTest extends ServiceTestBase {
    @Test public void testCreateService() {
        assertThat(ServiceUtil.createService(GiphyService.GiphyApi.class, this.mMockEndPoint))
                .isInstanceOf(GiphyService.GiphyApi.class);
    }
}
