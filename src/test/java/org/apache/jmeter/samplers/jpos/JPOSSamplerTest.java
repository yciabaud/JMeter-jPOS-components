package org.apache.jmeter.samplers.jpos;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.utils.TestJMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;
import org.junit.*;

import java.io.File;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
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
        assertThat(res.getResponseCode(), is("200"));
    }

    @Test
    public void testRegexHeader() throws Exception {
        String data = "<header>3830333231</header>";
        assertThat(instance.regex("\\d+", data), is("3830333231"));
    }

    @Test
    public void testRegexFieldMti() throws Exception {
        String data = "<field id=\"0\" value=\"0800\"/>";
        assertThat(instance.regex("id=\"\\d+\"", data,4,1), is("0"));
        assertThat(instance.regex("ue=\"\\d+\"", data,4,1), is("0800"));
    }

    @Test
    public void testRegexFieldTlv() throws Exception {
        String data = "<field id=\"46\" value=\"5F040B3030303030303030303031DF90080B3030303030303030303032\" type=\"binary\"/>";
        assertThat(instance.regex("id=\"\\d+\"", data,4,1), is("46"));
        assertThat(instance.regex("ue=\"[0-9A-F]+\"", data,4,1), is("5F040B3030303030303030303031DF90080B3030303030303030303032"));
    }

    /*
        https://github.com/erlanggaelfallujah/JMeter-jPOS-components/issues/29
     */
    @Test
    public void testHexStringToByteArray() throws DecoderException {

        String source = "F222B6EE252E3E43739F053A9F94F890A1953898AB625D98B47D0B0801B30032A374B6F0FA67A6BE40EDCA0D9AB69A7EE72961A2FDDC278FE088CF2E1B9029AD1761B28F420CFEF0620249CC06D7AED58245438CFD6B44D6BE17E906BA4D30AF508AE29A23C78B54161EFB18573D29325147FF46843C0803F60B340646B88E83CA86BCBA5A67AD1FC21CFEFBFCC832CD9DF2EE25236A4E5843A9F274A0FB84246AF3DD1C84264B7E78EB634CA868AE03615445FFC853197C3F1BC395FF748C7D1264778A195519092F64CEC406082B5D0226FFA927BC6CD956FF1F0E048298A14C047B5843F863E858E68C588E768D4BAB53FA1640ED7C9E2871F9F00DEA72E57E1E92B4FDE4CE131382127A1D18559C2975E333C03F08FD1596A46D259C033C45A802CE1B9B3AC5B10C35E6A5C8C89C06DC035C0C611DB6B611B59865EFD28D8932EE194281D8017333892097C8AC88668E2E91EF9D345DCFCA0A203F0B2D240F6E946D2874EBB8B5B119217C5A2D0FB85FB780EE48DCB3CD5F99F376259B10CB7E8070A0DE2E2804343983644A74DDA5F2EE33EEFD2C92BB1BFEF50E17D0FC9162D6EEA76DB51F8363FE591E8B520AB28B75132FB5332FFEC2D0E14CB4FC4EC7CDC89B44CAB0483F29975D24B77682642086EC1081FD90CFDFBD3E09A3F68146E7EEF4B64362ABF10EC4AD01C31D1CA86C72954FC249D0B2FC517D808E7BF312D9D95660A1A51BBB5D0718A969075";
        byte[] value = ISOUtil.hex2byte(source);

        String updateSource = "0F222B6EE252E3E43739F053A9F94F890A1953898AB625D98B47D0B0801B30032A374B6F0FA67A6BE40EDCA0D9AB69A7EE72961A2FDDC278FE088CF2E1B9029AD1761B28F420CFEF0620249CC06D7AED58245438CFD6B44D6BE17E906BA4D30AF508AE29A23C78B54161EFB18573D29325147FF46843C0803F60B340646B88E83CA86BCBA5A67AD1FC21CFEFBFCC832CD9DF2EE25236A4E5843A9F274A0FB84246AF3DD1C84264B7E78EB634CA868AE03615445FFC853197C3F1BC395FF748C7D1264778A195519092F64CEC406082B5D0226FFA927BC6CD956FF1F0E048298A14C047B5843F863E858E68C588E768D4BAB53FA1640ED7C9E2871F9F00DEA72E57E1E92B4FDE4CE131382127A1D18559C2975E333C03F08FD1596A46D259C033C45A802CE1B9B3AC5B10C35E6A5C8C89C06DC035C0C611DB6B611B59865EFD28D8932EE194281D8017333892097C8AC88668E2E91EF9D345DCFCA0A203F0B2D240F6E946D2874EBB8B5B119217C5A2D0FB85FB780EE48DCB3CD5F99F376259B10CB7E8070A0DE2E2804343983644A74DDA5F2EE33EEFD2C92BB1BFEF50E17D0FC9162D6EEA76DB51F8363FE591E8B520AB28B75132FB5332FFEC2D0E14CB4FC4EC7CDC89B44CAB0483F29975D24B77682642086EC1081FD90CFDFBD3E09A3F68146E7EEF4B64362ABF10EC4AD01C31D1CA86C72954FC249D0B2FC517D808E7BF312D9D95660A1A51BBB5D0718A969075";
        assertThat(Hex.encodeHexString(value).toUpperCase(),is(updateSource));
    }

}
