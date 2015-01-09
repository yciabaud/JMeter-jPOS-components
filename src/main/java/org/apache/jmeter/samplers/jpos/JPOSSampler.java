package org.apache.jmeter.samplers.jpos;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.jmeter.gui.custom.CustomTCPConfigGui;
import org.apache.jmeter.iso.manager.ISOMUXSingleton;
import org.apache.jmeter.iso.manager.SocketInterface;
import org.apache.jmeter.protocol.tcp.sampler.LengthPrefixedBinaryTCPClientImpl;
import org.apache.jmeter.protocol.tcp.sampler.TCPSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.SocketProxy;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jpos.iso.ISOBinaryField;
import org.jpos.iso.ISOComponent;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.ISO87APackager;
import org.jpos.iso.packager.ISO87BPackager;

/**
 *
 * @author "Yoann Ciabaud" <yoann.ciabaud@monext.fr>
 */
public class JPOSSampler extends TCPSampler {

    private static final Logger log = LoggingManager.getLoggerForClass();
    private final static String VARIABLE_PREFIX = "JPOSSampler.variablePrefix";
    private final static String VARIABLE_SEPARATOR = "JPOSSampler.variableSeparator";
    private final static String HEXES = "0123456789ABCDEF";
    private ISOMsg msg = new ISOMsg();
    private boolean initialized = false;

    public JPOSSampler() {
        // Default value for TCP Client
        setClassname(LengthPrefixedBinaryTCPClientImpl.class.getName());
    }
    
    public void initialize() throws Exception {
//    	final String threadName = JMeterContextService.getContext().getThread().getThreadName();    	
    	initialized = true;
    }

    @Override
    public SampleResult sample(Entry e) {
        SampleResult res = new SampleResult();        
        res.setSampleLabel(getName());        
        boolean isOK = false;
        if(!initialized){
        	try {
				initialize();
			} catch (Exception e1) {
				res.setResponseMessage(e1.getMessage());
                res.setSuccessful(false);
                return res;
			}
        }                     

        ISOPackager customPackager = null;
		customPackager = new ISO87BPackager();
		
        // Listing All the Thread Context Variables
//        JMeterVariables variables = JMeterContextService.getContext().getVariables();
        
        String server = getPropertyAsString(CustomTCPConfigGui.SERVER);
        log.info("server = " + server);
        String port = getPropertyAsString(CustomTCPConfigGui.PORT);
        log.info("port = " + port);
        String timeout = getPropertyAsString(CustomTCPConfigGui.TIMEOUT);
        log.info("timeout = " + timeout);
        String requestData = getPropertyAsString(CustomTCPConfigGui.REQUEST);
        log.info("request data = " + requestData);        
        res.sampleStart();
        
        ISOMUXSingleton isoMUXSingleton = ISOMUXSingleton.getInstance(customPackager,server,Integer.parseInt(port));
        Thread threadIsoMux = new Thread(isoMUXSingleton.getISOMUX());
        log.info("start thread iso mux");
        threadIsoMux.start();        
        ISOMUX isoMUX = isoMUXSingleton.getISOMUX();
        SocketInterface socket = null;
        try {
            socket = new SocketProxy(isoMUX);
			ISOMsg isoSample = SampleISOMsg.getSampleISOMsg();
			log.info("hit to host destination");
			ISOMsg isoResponse = socket.isoRequest(isoSample);
			if(isoResponse!=null){
				log.info("iso response is not null");
				logISOMsg(isoResponse);
				res.setResponseCodeOK();
	            isOK = true;			
			}else{
				log.info("iso response is null or timeout");
				isOK = false;
				res.setResponseMessage("timeout");
	            res.setSuccessful(false);
			}
		} catch (ISOException e1) {
			log.error(e1.getMessage());
			isOK = false;
			res.setResponseMessage(e1.getMessage());
            res.setSuccessful(false);
		}
        res.sampleEnd();
        res.setSuccessful(isOK);        
        return res;
    }
    
	private void logISOMsg(ISOMsg msg) {
		System.out.println("----ISO MESSAGE-----");
		log.info("----ISO MESSAGE-----");
		try {
			System.out.println("  MTI : " + msg.getMTI());
			log.info("  MTI : " + msg.getMTI());
			for (int i=1;i<=msg.getMaxField();i++) {
				if (msg.hasField(i)) {
					System.out.println("Field-"+i+" : "+msg.getString(i)+", Length : "+msg.getString(i).length());
					log.info("Field-"+i+" : "+msg.getString(i)+", Length : "+msg.getString(i).length());
				}
			}
		} catch (ISOException e) {
			e.printStackTrace();
		} finally {
			log.info("--------------------");
			System.out.println("--------------------");
		}
	}

    private void createISOField(String tokens, String value) throws ISOException {

        ISOMsg parent = msg;
        StringTokenizer tokenizer = new StringTokenizer(tokens.replaceAll("[a-zA-Z]", ""), getVariablesSeparator());
        List<Integer> ids = new ArrayList<Integer>();

        while (tokenizer.hasMoreTokens()) {

            String token = tokenizer.nextToken();

            Integer id;
            try {
                id = Integer.parseInt(token);
                ids.add(id);
            } catch (NumberFormatException ex) {
                log.warn("Numer format exception: " + token, ex);
                continue;
            }

            ISOComponent field = null;

            // si on est sur un parent
            if (tokenizer.hasMoreTokens() && (parent == null || parent instanceof ISOMsg)) {

                log.info("Adding parent " + id);

                field = parent.getComponent(id);
                // on vérifie l'existence
                if (field == null) {
                    field = new ISOMsg(id);

                    parent.set(field);
                }

            } else if (!tokenizer.hasMoreTokens()) {

                log.info("Adding field " + id + "=" + value + "...");

                field = parent.getComponent(id);
                // on vérifie l'existence
                if (field == null) {
//                    if (PackagerUtil.isBinary(ids, id)) {
//                        field = new ISOBinaryField(id);
//                    } else {
//                        field = new ISOField(id);
//                    }
//                    parent.set(field);
                	log.warn("field is null");
                }
            } else {
                log.warn("Field " + id + "=" + value + " not set! Incompatible vars.");
                break;
            }

            // on est en derniere position
            if (!tokenizer.hasMoreTokens()) {
                if (field instanceof ISOBinaryField) {
                    field.setValue(hexStringToByteArray(value));
                } else {
                    field.setValue(value);
                }
            } else {
                parent = (ISOMsg) field;
            }

        }

    }

    /**
     *
     * @return
     */
    public String getVariablesPrefix() {
        return getPropertyAsString(VARIABLE_PREFIX);
    }

    /**
     *
     * @param variablePrefix
     */
    public void setVariablesPrefix(String variablePrefix) {
        setProperty(VARIABLE_PREFIX, variablePrefix);
    }

    /**
     *
     * @return
     */
    public String getVariablesSeparator() {
        return getPropertyAsString(VARIABLE_SEPARATOR);
    }

    /**
     *
     * @param separator
     */
    public void setVariablesSeparator(String separator) {
        setProperty(VARIABLE_SEPARATOR, separator);
    }

    public static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();

    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data;

        if (len == 1) {
            data = new byte[1];
            data[0] = (byte) Character.digit(s.charAt(0), 16);
            return data;
        } else {
            data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }
        }
        return data;
    }
}
