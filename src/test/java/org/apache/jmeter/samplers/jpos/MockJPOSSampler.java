package org.apache.jmeter.samplers.jpos;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by awibowo on 12/04/2016.
 */
public class MockJPOSSampler extends JPOSSampler {

    public static final String PACKAGER_FILE_ISO87_BINARY_XML = "src/test/resources/iso87binary.xml";

    @Override
    public void processDataRequest() {
        isoMap = new HashMap<String, String>();
        isoMap.put("mti","0800");
        isoMap.put("bit.41", "1234567");
        isoMap.put("bit.42", "12345678765432");
    }

    @Override
    public String getCurrentThreadName() {
        return 	Thread.currentThread().getName();

    }

    @Override
    public void processPackagerFile() {
        try {
            customPackager = new GenericPackager(PACKAGER_FILE_ISO87_BINARY_XML);
        } catch (ISOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String obtainChannel() {
        return "nacchannel";
    }

    @Override
    protected ISOMsg execute(int intTimeOut, ISOMsg isoReq) throws IOException, ISOException {
        ISOMsg isoRes = (ISOMsg) isoReq.clone();
        isoRes.setResponseMTI();
        isoRes.set(39, "00");
        return isoRes;
    }
}
