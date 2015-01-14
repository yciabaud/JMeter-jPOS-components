package org.apache.jmeter.samplers.jpos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.jmeter.gui.custom.CustomTCPConfigGui;
import org.apache.jmeter.iso.manager.ISOMUXSingleton;
import org.apache.jmeter.iso.manager.SocketInterface;
import org.apache.jmeter.protocol.tcp.sampler.LengthPrefixedBinaryTCPClientImpl;
import org.apache.jmeter.protocol.tcp.sampler.TCPSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.ChannelHelper;
import org.apache.jmeter.util.SocketProxy;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.FieldUtil;

/**
 * 
 * @author "Yoann Ciabaud" <yoann.ciabaud@monext.fr> "Erlangga"
 *         <erlangga258@gmail.com>
 */
public class JPOSSampler extends TCPSampler implements TestStateListener {

	private static final Logger log = LoggingManager.getLoggerForClass();
	private final static String HEXES = "0123456789ABCDEF";
	private boolean initialized = false;
	private ISOMUXSingleton isoMUXSingleton;

	private static final Integer MAX_ISOBIT = Integer.valueOf(128);
	// private static final Integer MAX_NESTED_ISOBIT = Integer.valueOf(64);

	private ISOPackager customPackager;
	private Properties reqProp;

	public JPOSSampler() {
		// Default value for TCP Client
		setClassname(LengthPrefixedBinaryTCPClientImpl.class.getName());
	}

	public void initialize() throws Exception {
		log.info("call initilalize() ...");		
		processPackagerFile();
		processDataRequest();		
		if (customPackager != null) {
			String server = getPropertyAsString(CustomTCPConfigGui.SERVER);
			String port = getPropertyAsString(CustomTCPConfigGui.PORT);
			String channel = getPropertyAsString(CustomTCPConfigGui.CHANNEL_KEY);

			ChannelHelper channelHelper = new ChannelHelper();
			String header = reqProp.getProperty("header");
			if (header != null) {
				channelHelper.setTpdu(header);
			} else {
				channelHelper.setTpdu("0000000000"); // default
			}

			BaseChannel baseChannel = channelHelper.getChannel(server,
					Integer.parseInt(port), customPackager, channel);
			isoMUXSingleton = ISOMUXSingleton.getInstance(baseChannel);
		}
		initialized = true;
	}

	private ISOMsg buildISOMsg() throws ISOException {
		ISOMsg isoReq = new ISOMsg();
		isoReq.setMTI((String) reqProp.get("mti"));

		int i = 1;
		String field = null;
		while (i < MAX_ISOBIT.intValue()) {
			if ((field = (String) reqProp.get("bit." + i)) != null) {
				if (field.equalsIgnoreCase("auto")) {
					isoReq.set(i, FieldUtil.getValue(i));
					// else if (field.equalsIgnoreCase("stan"))
					// this.isoReq.set(i,
					// ISOUtil.zeropad(this.isoReq.getString(11), 8));
					// else if (field.equalsIgnoreCase("tlv"))
					// this.isoReq.set(i, tlvs.pack());
					// else if (field.equalsIgnoreCase("counter"))
					// this.isoReq.set(i, FieldUtil.getCounterValue());
					// else if (field.startsWith("+"))
					// this.isoReq.set(i, ISOUtil.hex2byte(field.substring(1)));
					// else if (field.equalsIgnoreCase("nested")) {
					// for (int n = 1; n < MAX_NESTED_ISOBIT.intValue(); n++) {
					// if ((field = (String) reqProp.get("bit." + i + "." + n))
					// != null)
					// isoReq.set(i + "." + n, field);
					// }
				} else {
					isoReq.set(i, field);
				}
			}
			i++;
		}

		// String stan_tid_req = isoReq.getString(11) + isoReq.getString(41);

		return isoReq;
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

		String timeout = getPropertyAsString(CustomTCPConfigGui.TIMEOUT);
		int intTimeOut = 30 * 1000; // default
		if (timeout != null && !timeout.equals("")) {
			log.info("timeout = " + timeout);
			intTimeOut = Integer.parseInt(timeout);
		}
		res.sampleStart();

		SocketInterface socket = null;
		ISOMUX isoMUX = isoMUXSingleton.getISOMUX();
		try {
			socket = new SocketProxy(isoMUX);
			ISOMsg isoReq = buildISOMsg();
			log.info("Sending Time : " + FieldUtil.getDate());
			ISOMsg isoResponse = socket.isoRequest(isoReq, intTimeOut);
			log.info("Receive\t: " + FieldUtil.getDate());
			if (isoResponse != null) {
				log.info("iso response is not null");
				String response = logISOMsg(isoResponse);
				if (response != null) {
					res.setResponseData(response);
					res.setResponseMessage(response);
				}
				res.setResponseCodeOK();
				isOK = true;
			} else {
				isOK = false;
				res.setResponseData("timeout cuy ...");
				res.setResponseMessage("timeout");
			}
		} catch (ISOException e1) {
			log.error(e1.getMessage());
			isOK = false;
			res.setResponseMessage(e1.getMessage());
			res.setResponseData(e1.getMessage());
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
			for (int i = 1; i <= msg.getMaxField(); i++) {
				if (msg.hasField(i)) {
					sBuffer.append("Field-" + i + " : " + msg.getString(i)
							+ ", Length : " + msg.getString(i).length() + "\n");
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
		isoMUXSingleton.terminate();
	}

	@Override
	public void testEnded(String arg0) {
	}

	private void logJMeter() {
		if (JMeterContextService.getContext() != null) {
			if (JMeterContextService.getContext().getThreadGroup() != null) {
				int numberOfThreads = JMeterContextService.getContext()
						.getThreadGroup().getNumberOfThreads();
				int numThreads = JMeterContextService.getContext()
						.getThreadGroup().getNumThreads();
				log.info("numThreads = " + numThreads + ", numberOfThreads = "
						+ numberOfThreads);

				int n = JMeterContextService.getTotalThreads();
				log.info("n = " + n);
			}
		}
	}

	@Override
	public void testStarted() {
		log.info("call testStarted()");
	}

	private void processDataRequest() {
		String reqFile = getPropertyAsString(CustomTCPConfigGui.REQ_KEY);
		if (reqFile != null) {
			reqProp = new Properties();
			InputStream input = null;
			try {
				File f = new File(reqFile);
				log.info("reqFile = " + f.getAbsolutePath());
				if (f.exists()) {
					log.info("file exists");
					input = new FileInputStream(new File(reqFile));
					if (input != null) {
						reqProp.load(input);
					}
				} else {
					log.info("file not exists");
				}
			} catch (FileNotFoundException e) {
				log.warn(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				log.warn(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void processPackagerFile() {
		String packagerFile = getPropertyAsString(CustomTCPConfigGui.PACKAGER_KEY);
		if (packagerFile != null) {
			File initialFile = new File(packagerFile);
			log.info("initFile = " + initialFile.getAbsolutePath());
			if (initialFile.exists()) {
				log.info("file exists");
				try {
					InputStream targetStream = new FileInputStream(initialFile);
					if (targetStream != null) {
						customPackager = new GenericPackager(targetStream);
					}				
				} catch (FileNotFoundException e) {
					log.warn(e.getMessage());
					e.printStackTrace();
				} catch (ISOException e) {
					log.warn(e.getMessage());
					e.printStackTrace();
				}
			} else {
				log.info("file not exists");
			}
		}
	}

	@Override
	public void testStarted(String arg0) {
	}
}
