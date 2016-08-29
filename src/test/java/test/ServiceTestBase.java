package test;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

/**
 * JUnit + OkHTTP Mock Server Tests.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public abstract class ServiceTestBase extends TestBase {
    @Rule public final MockWebServer server = new MockWebServer();
    protected String mMockEndPoint;

    @Override @Before public void setUp() throws Exception {
        super.setUp();

        this.mMockEndPoint = this.server.url("/").toString();
    }

    @Override @After public void tearDown() throws Exception {
        super.tearDown();

        this.server.shutdown();
    }
}
