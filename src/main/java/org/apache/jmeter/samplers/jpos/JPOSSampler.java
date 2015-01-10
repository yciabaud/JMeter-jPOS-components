package org.apache.jmeter.samplers.jpos;

import org.apache.jmeter.gui.custom.CustomTCPConfigGui;
import org.apache.jmeter.iso.manager.ISOMUXSingleton;
import org.apache.jmeter.iso.manager.SocketInterface;
import org.apache.jmeter.protocol.tcp.sampler.LengthPrefixedBinaryTCPClientImpl;
import org.apache.jmeter.protocol.tcp.sampler.TCPSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.SocketProxy;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.ISO87BPackager;

/**
 *
 * @author 
 * "Yoann Ciabaud" <yoann.ciabaud@monext.fr>
 * "Erlangga" <erlangga258@gmail.com>
 */
public class JPOSSampler extends TCPSampler implements TestStateListener {

	private static final Logger log = LoggingManager.getLoggerForClass();
	private final static String HEXES = "0123456789ABCDEF";
	private boolean initialized = false;	
	private ISOMUXSingleton isoMUXSingleton;	
	private static final String HEADER_TPDU ="0000000000";

	public JPOSSampler() {
		// Default value for TCP Client
		setClassname(LengthPrefixedBinaryTCPClientImpl.class.getName());
	}

	public void initialize() throws Exception {
		// final String threadName =
		// JMeterContextService.getContext().getThread().getThreadName();
		
		log.info("call initilalize() ...");
		
		ISOPackager customPackager = new ISO87BPackager();		
		String server = getPropertyAsString(CustomTCPConfigGui.SERVER);
		String port = getPropertyAsString(CustomTCPConfigGui.PORT);
		
		isoMUXSingleton = ISOMUXSingleton.getInstance(
				customPackager, server, Integer.parseInt(port), HEADER_TPDU);
		
		initialized = true;
	}	

	@Override
	public SampleResult sample(Entry e) {
		log.info("call sample() ...");
		SampleResult res = new SampleResult();
		res.setSampleLabel(getName());
		boolean isOK = false;
		if (!initialized) {
			try {
				initialize();
			} catch (Exception e1) {
				res.setResponseMessage(e1.getMessage());
				res.setSuccessful(false);
				return res;
			}
		}		

		// Listing All the Thread Context Variables
		// JMeterVariables variables =
		// JMeterContextService.getContext().getVariables();
		String timeout = getPropertyAsString(CustomTCPConfigGui.TIMEOUT);
		int intTimeOut = 30*1000;
		if(timeout!=null && !timeout.equals("")){
			log.info("timeout = " + timeout);
			intTimeOut = Integer.parseInt(timeout);
		}
		
		String requestData = getPropertyAsString(CustomTCPConfigGui.REQUEST);
		if(requestData!=null && !requestData.equals("")){
			log.info("request data \n"+requestData);
		}
		res.sampleStart();
				
		/*
		 * Better solutions for running threads
		 */
//		ExecutorService executor = Executors.newFixedThreadPool(2);  
//		Runnable runnable = new Runnable() {                         
//		    @Override                                                
//		    public void run() {                                      
//		        System.out.println(Thread.currentThread().getName());
//		    }                                                        
//		};                                                           
//		executor.execute(runnable);                                  
//		executor.execute(runnable);                                  
//		                                                             
//		executor.shutdown();
		
		SocketInterface socket = null;
		ISOMUX isoMUX = isoMUXSingleton.getISOMUX();
		try {
			socket = new SocketProxy(isoMUX);
			ISOMsg isoSample = SampleISOMsg.getSampleISOMsg();
			log.info("hit to host destination");
			ISOMsg isoResponse = socket.isoRequest(isoSample, intTimeOut);
			if (isoResponse != null) {
				log.info("iso response is not null");
				String response = logISOMsg(isoResponse);
				if(response!=null){
					res.setResponseMessage(response);
				}
				res.setResponseCodeOK();
				isOK = true;
			} else {
				log.info("iso response is null or timeout");
				isOK = false;
				res.setResponseMessage("timeout");
			}
		} catch (ISOException e1) {
			log.error(e1.getMessage());
			isOK = false;
			res.setResponseMessage(e1.getMessage());
		}
				
		res.sampleEnd();
		res.setSuccessful(isOK);
		return res;
	}
	
	private String logISOMsg(ISOMsg msg) {		
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("----RESPONSE ISO MESSAGE-----\n");
		try {
			sBuffer.append("  MTI : " + msg.getMTI());
			for (int i=1;i<=msg.getMaxField();i++) {
				if (msg.hasField(i)) {
					sBuffer.append("Field-"+i+" : "+msg.getString(i)+", Length : "+msg.getString(i).length()+"\n");
				}
			}
		} catch (ISOException e) {
			e.printStackTrace();
		} finally {
			sBuffer.append("--------------------\n");
		}
		return sBuffer.toString();
	}

	public static String getHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
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
				data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
						.digit(s.charAt(i + 1), 16));
			}
		}
		return data;
	}

	@Override
	public void testEnded() {
		log.info("call testEnded()");		
	}

	@Override
	public void testEnded(String arg0) {
	}

	@Override
	public void testStarted() {
		log.info("call testStarted()");		
		int totalThreads = JMeterContextService.getTotalThreads();
		log.info("total threads = " + totalThreads );
	}

	@Override
	public void testStarted(String arg0) {
		isoMUXSingleton.terminate();
	}
}
