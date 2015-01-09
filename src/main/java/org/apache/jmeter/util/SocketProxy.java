package org.apache.jmeter.util;

import java.io.IOException;

import org.apache.jmeter.iso.manager.SocketInterface;
import org.apache.log4j.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequest;

public class SocketProxy implements SocketInterface {
	
	private ISOMUX isoMux;
	
	private static Logger logger = Logger.getLogger(SocketProxy.class.getName());
	
	public SocketProxy(ISOMUX isoMUX) throws ISOException {		
		this.isoMux = isoMUX;
	}
	
	private static void logISOMsg(ISOMsg msg) {		
		logger.info("----ISO MESSAGE-----");
		try {
			logger.info("  MTI : " + msg.getMTI());
			for (int i=1;i<=msg.getMaxField();i++) {
				if (msg.hasField(i)) {
					logger.info("Field-"+i+" : "+msg.getString(i)+", Length : "+msg.getString(i).length());
				}
			}
		} catch (ISOException e) {
			e.printStackTrace();
		} finally {
			logger.info("--------------------");
		}
	}

	public ISOMsg isoRequest(ISOMsg isoMsg) {			
		if(!isoMux.isConnected()){
			try {
				isoMux.getISOChannel().reconnect();
			} catch (IOException e) {
			}
		}
				
		ISORequest req = new ISORequest(isoMsg);		
		isoMux.queue(req);		
		logger.info("----Get Request----");
		logISOMsg(isoMsg);    	
		ISOMsg isoReply = req.getResponse(30*1000);
        if (isoReply != null) {
        	logger.info("----Get Response----");
        	logISOMsg(isoReply);
        }        
		return isoReply;
	}

	public void dispose() {		
//		isoMux = null;
//		threadIsoMux = null;
	}
}
