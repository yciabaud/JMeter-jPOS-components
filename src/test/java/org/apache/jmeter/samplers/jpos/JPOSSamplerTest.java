package org.apache.jmeter.samplers.jpos;

import org.apache.jmeter.config.VariablesFromXMLFile;
import java.io.File;
import java.net.URISyntaxException;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.utils.TestJMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jpos.iso.ISOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author apc
 */
public class JPOSSamplerTest {

    private static final Logger log = LoggingManager.getLoggerForClass();
    
    private VariablesFromXMLFile importVars;
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
        testFile = new File(this.getClass().getResource("/test.xml").toURI());

        JMeterContext jmcx = JMeterContextService.getContext();
        jmcx.setVariables(new JMeterVariables());
        variables = jmcx.getVariables();

        importVars = new VariablesFromXMLFile();
        importVars.setXmlFile(testFile.getAbsolutePath());
        
        instance = new JPOSSampler();

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

        importVars.setVariablesPrefix("");
        instance.setVariablesPrefix("CB_");
        instance.setVariablesSeparator("_");
        instance.setServer("172.22.51.30");
        instance.setPort("4170");
        
        importVars.iterationStart(iterEvent);
        SampleResult res = instance.sample(null);
        log.info("Expected: "+"0110767c66c1aee0b861104074610000264522010000000000006000000000006000022405150161000000867494051501022414016011025000510001020406497650062169022504074610000264522d14012011972136300000f1f0f5f5f0f5f8f6f7f4f9f4d3c3d3c64040f0f0f0f0f0f0f0f5f2f1c2c6c3d6c940404040404040404040f0f0f0f0f0f5f2f1e0c281998183889689a2e040404040404040404040404040404040404040c6d9097809787c407f4fa5897d470801000000000000000bf9f2f5f0f0f0f2f3f6f1f5080102011102000170ffffffffffffffff".toUpperCase());
        log.info("Size   :  "+"0110767c66c1aee0b861104074610000264522010000000000006000000000006000022405150161000000867494051501022414016011025000510001020406497650062169022504074610000264522d14012011972136300000f1f0f5f5f0f5f8f6f7f4f9f4d3c3d3c64040f0f0f0f0f0f0f0f5f2f1c2c6c3d6c940404040404040404040f0f0f0f0f0f5f2f1e0c281998183889689a2e040404040404040404040404040404040404040c6d9097809787c407f4fa5897d470801000000000000000bf9f2f5f0f0f0f2f3f6f1f5080102011102000170ffffffffffffffff".length());
        
        assertEquals(
            "Requete generee",
            "0110767c66c1aee0b861104074610000264522010000000000006000000000006000022405150161000000867494051501022414016011025000510001020406497650062169022504074610000264522d14012011972136300000f1f0f5f5f0f5f8f6f7f4f9f4d3c3d3c64040f0f0f0f0f0f0f0f5f2f1c2c6c3d6c940404040404040404040f0f0f0f0f0f5f2f1e0c281998183889689a2e040404040404040404040404040404040404040c6d9097809787c407f4fa5897d470801000000000000000bf9f2f5f0f0f0f2f3f6f1f5080102011102000170ffffffffffffffff".toLowerCase(), 
            res.getSamplerData().toLowerCase());
 
    }

}
