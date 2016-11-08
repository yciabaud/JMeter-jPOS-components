package org.apache.jmeter.samplers.jpos;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.utils.TestJMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.hamcrest.CoreMatchers;
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
        instance.setServer("172.30.44.208");
        instance.setPort("1085");

        SampleResult res = instance.sample(null);
        assertThat(res, CoreMatchers.<SampleResult>notNullValue());
        assertThat(res.getResponseCode(), CoreMatchers.is("200"));
    }

}
