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
  protected String mockEndPoint;

  @Before @Override public void setUp() throws Exception {
    super.setUp();

    mockEndPoint = server.url("/").toString();
  }

  @After @Override public void tearDown() throws Exception {
    super.tearDown();

    server.shutdown();
  }
}
