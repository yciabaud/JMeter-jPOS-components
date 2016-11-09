package org.apache.jmeter.samplers.jpos;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.utils.TestJMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.jpos.iso.ISOException;
import org.junit.*;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.assertThat;

/**
 *
 * @author apc
 */
public class JPOSSamplerTest {

    private static final Logger log = LoggingManager.getLoggerForClass();

    private JPOSSampler instance;
    private JMeterVariables variables;
    private LoopIterationEvent iterEvent;
    private File testFile = null;

    /**
     *
     */
    public JPOSSamplerTest() {
    }

    /**
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass()
            throws Exception {
        TestJMeterUtils.createJmeterEnv();
    }

    /**
     *
     * @throws Exception
     */
    @AfterClass
    public static void tearDownClass()
            throws Exception {
    }

    /**
     *
     */
    @Before
    public void setUp() throws URISyntaxException {
        instance = new MockJPOSSampler();
        iterEvent = new LoopIterationEvent(null, 1);
    }

    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of iterationStart method, of class VariablesFromCSVFile.
     */
    @Test
    public void testSample() throws ISOException {
        instance.setServer("172.30.44.120");
        instance.setPort("1085");

        SampleResult res = instance.sample(null);
        assertThat(res, CoreMatchers.<SampleResult>notNullValue());
        assertThat(res.getResponseCode(), CoreMatchers.is("200"));
    }


    @Test
    public void testRegexHeader() throws Exception {
        String data = "<header>3830333231</header>";
        assertThat(instance.regex("\\d+", data), CoreMatchers.is("3830333231"));
    }

    @Test
    public void testRegexFieldMti() throws Exception {
        String data = "<field id=\"0\" value=\"0800\"/>";
        assertThat(instance.regex("id=\"\\d+\"", data,4,1), CoreMatchers.is("0"));
        assertThat(instance.regex("ue=\"\\d+\"", data,4,1), CoreMatchers.is("0800"));
    }

    @Test
    public void testRegexFieldTlv() throws Exception {
        String data = "<field id=\"46\" value=\"5F040B3030303030303030303031DF90080B3030303030303030303032\" type=\"binary\"/>";
        assertThat(instance.regex("id=\"\\d+\"", data,4,1), CoreMatchers.is("46"));
        assertThat(instance.regex("ue=\"[0-9A-F]+\"", data,4,1), CoreMatchers.is("5F040B3030303030303030303031DF90080B3030303030303030303032"));
    }

}
