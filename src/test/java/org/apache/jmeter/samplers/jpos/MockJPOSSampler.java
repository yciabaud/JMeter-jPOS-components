package org.apache.jmeter.samplers.jpos;

import org.jpos.iso.ISOException;
import org.jpos.iso.packager.GenericPackager;

/**
 * Created by awibowo on 12/04/2016.
 */
public class MockJPOSSampler extends JPOSSampler {

    public static final String PACKAGER_FILE_ISO87_BINARY_XML = "src/test/resources/iso87binary.xml";

    @Override
    public void processDataRequest() {
        super.processDataRequest();
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
    protected int obtainPort() {
        return this.getPort();
    }

    @Override
    protected String obtainServer() {
        return this.getServer();
    }
}
