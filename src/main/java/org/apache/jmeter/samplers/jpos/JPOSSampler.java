package org.apache.jmeter.samplers.jpos;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jmeter.gui.custom.CustomTCPConfigGui;
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
import org.jpos.iso.channel.NACChannel;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.FieldUtil;

/**
 * 
 * @author "Yoann Ciabaud" <yoann.ciabaud@monext.fr>
 * @author "Erlangga" <erlangga258@gmail.com>
 *
 */
public class JPOSSampler extends TCPSampler implements TestStateListener {

	// https://svn.apache.org/repos/asf/jmeter/tags/v2_8/src/protocol/tcp/org/apache/jmeter/protocol/tcp/sampler/TCPSampler.java

	private static final Logger LOGGER = LoggingManager.getLoggerForClass();
	private final static String HEXES = "0123456789ABCDEF";
	private boolean initialized = false;
	private static final Integer MAX_ISOBIT = Integer.valueOf(128);
	// private static final Integer MAX_NESTED_ISOBIT = Integer.valueOf(64);
	protected ISOPackager customPackager;
	protected Properties reqProp;
	private BaseChannel baseChannel;

	public JPOSSampler() {
		LOGGER.info("call constructor() ...");
		// Default value for TCP Client
		setClassname(LengthPrefixedBinaryTCPClientImpl.class.getName());
	}

	public void initialize() throws Exception {
		LOGGER.info("call initilalize() ...");
		processPackagerFile();
		processDataRequest();
		if (customPackager != null) {
			LOGGER.info("customPackager available ...");
			String server = obtainServer();
			int port = obtainPort();
			String channel = obtainChannel();
			final String threadName = getCurrentThreadName();
			LOGGER.info("current thread is " + threadName + "[" + channel + ":" + server + ":" + port+ "]");

			ChannelHelper channelHelper = new ChannelHelper();
			String header = reqProp.getProperty("header");
			if (header != null) {
				channelHelper.setTpdu(header);
			} else {
				channelHelper.setTpdu("0000000000"); // default
			}

			baseChannel = channelHelper.getChannel(server, port, customPackager, channel);
			LOGGER.info("initialize channel " + baseChannel.getHost() + " port " + baseChannel.getPort());
			initialized = true;
		}
	}

	protected String obtainServer() {
//		return getPropertyAsString(CustomTCPConfigGui.SERVER);
		return getServer();
	}

	protected int obtainPort() {
		return getPort();
//		String portStr = getPropertyAsString(CustomTCPConfigGui.PORT);
//		if (portStr.isEmpty()) return 0;
//		return Integer.parseInt(portStr);
	}

	protected String obtainChannel () {
		return getPropertyAsString(CustomTCPConfigGui.CHANNEL_KEY);
	}

	public String getCurrentThreadName() {
		return JMeterContextService.getContext().getThread().getThreadName();
	}

	private ISOMsg buildISOMsg() throws ISOException {
		LOGGER.info("building iso message");
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
		LOGGER.info(LOGGERISOMsg(isoReq));
		return isoReq;
	}

	@Override
	public SampleResult sample(Entry e) {
		LOGGER.info("call sample() ...");
		SampleResult res = new SampleResult();
		res.setSampleLabel(getName());
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
			LOGGER.info("timeout = " + timeout);
			intTimeOut = Integer.parseInt(timeout);
		}
		res.sampleStart();
		res.setSuccessful(false);
		res.setResponseMessage("time-out");
		res.setResponseCode("ER");


		try {
			ISOMsg isoReq = buildISOMsg();
			ISOMsg isoRes = execute(intTimeOut, isoReq);
			if (isoRes != null) {
				res.setResponseMessage(LOGGERISOMsg(isoRes));
				res.setResponseCodeOK();
				res.setResponseData(LOGGERISOMsg(isoRes), StandardCharsets.UTF_8.name());
				res.setSuccessful(true);
			}
		} catch (ISOException e1) {
			LOGGER.error(e1.getMessage());
			res.setResponseMessage(e1.getMessage());
		} catch (IOException e1) {
			LOGGER.error(e1.getMessage());
			res.setResponseMessage(e1.getMessage());
		}

		res.sampleEnd();
		return res;

		/* =======================

		SocketInterface socket = null;
		try {
			if(isoMUX.isConnected()){
				socket = new SocketProxy(isoMUX);
				ISOMsg isoReq = buildISOMsg();
				LOGGER.info("Sending Time : " + FieldUtil.getDate());
				ISOMsg isoResponse = socket.isoRequest(isoReq, intTimeOut);
				LOGGER.info("Receive\t: " + FieldUtil.getDate());
				if (isoResponse != null) {
					LOGGER.info("iso response is not null");
					String response = LOGGERISOMsg(isoResponse);
					if (response != null) {
						res.setResponseMessage(response);
					}
					res.setResponseCodeOK();
					isOK = true;
				} else {
					isOK = false;
					res.setResponseMessage("timeout");
				}
			}else{
				try {
					initialize();
				} catch (Exception e1) {
					LOGGER.error(e1.getMessage());
					res.setResponseMessage(e1.getMessage());
					res.setSuccessful(false);
					return res;
				}
			}
		} catch (ISOException e1) {
			LOGGER.error(e1.getMessage());
			res.setResponseMessage(e1.getMessage());
			res.setSuccessful(false);
			return res;
		}

		 =================== */
	}

	protected ISOMsg execute(int intTimeOut, ISOMsg isoReq) throws IOException, ISOException {
		LOGGER.info("connect to " + baseChannel.getHost() + " port " + baseChannel.getPort() + " time-out " + intTimeOut);
		baseChannel.connect();
		baseChannel.setTimeout(intTimeOut);
		baseChannel.send(isoReq);
		return baseChannel.receive();
	}

	private String LOGGERISOMsg(ISOMsg msg) {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("----DEBUG ISO MESSAGE-----\n");
		try {
			sBuffer.append("  MTI : " + msg.getMTI() + ", ");
			for (int i = 1; i <= msg.getMaxField(); i++) {
				if (msg.hasField(i)) {
					sBuffer.append("Field-" + i + " : " + msg.getString(i)
							+ ", Length : " + msg.getString(i).length() + "\n");
				}
			}
		} catch (ISOException e) {
			e.printStackTrace();
			sBuffer.append(e.getMessage());
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
		LOGGER.info("call testEnded()");
		testEnded("");
	}

	@Override
	public void testEnded(String arg0) {
	}

	private void LOGGERJMeter() {
		if (JMeterContextService.getContext() != null) {
			if (JMeterContextService.getContext().getThreadGroup() != null) {
				int numberOfThreads = JMeterContextService.getContext()
						.getThreadGroup().getNumberOfThreads();
				int numThreads = JMeterContextService.getContext()
						.getThreadGroup().getNumThreads();
				LOGGER.info("numThreads = " + numThreads + ", numberOfThreads = "
						+ numberOfThreads);

				int n = JMeterContextService.getTotalThreads();
				LOGGER.info("n = " + n);
			}
		}
	}

	@Override
	public void testStarted() {
		LOGGER.info("call testStarted()");
		testStarted("");
	}

	@Override
	public void testStarted(String s) {

	}

	public void processDataRequest() {
		String reqFile = obtainDataRequestFilePath();
		if (reqFile != null) {
			try {
				reqProp = parsePropertiesString(reqFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Properties parsePropertiesString(String s) throws IOException {
		Properties p = new Properties();
		p.load(new StringReader(s));
		return p;
	}

	protected String obtainDataRequestFilePath() {
		return getPropertyAsString(CustomTCPConfigGui.REQ_KEY);
	}

	public void processPackagerFile() {
		String packagerFile = getPropertyAsString(CustomTCPConfigGui.PACKAGER_KEY);
		if (packagerFile != null) {
			File initialFile = new File(packagerFile);
			LOGGER.info("initFile = " + initialFile.getAbsolutePath());
			if (initialFile.exists()) {
				LOGGER.info("file exists");
				try {
					InputStream targetStream = new FileInputStream(initialFile);
					if (targetStream != null) {
						customPackager = new GenericPackager(targetStream);
					}				
				} catch (FileNotFoundException e) {
					LOGGER.warn(e.getMessage());
					e.printStackTrace();
				} catch (ISOException e) {
					LOGGER.warn(e.getMessage());
					e.printStackTrace();
				}
			} else {
				LOGGER.info("file not exists");
			}
		}
	}
}
