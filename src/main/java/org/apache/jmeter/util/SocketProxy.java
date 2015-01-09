package org.apache.jmeter.util;

import java.io.IOException;

import org.apache.jmeter.iso.manager.SocketInterface;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequest;

public class SocketProxy implements SocketInterface {
	
	private ISOMUX isoMux;
	
    private static final Logger logger = LoggingManager.getLoggerForClass();
	
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
		logger.info("masuk iso request ...");
		System.out.println("masuk iso request ...");
		if(!isoMux.isConnected()){
			try {
				isoMux.getISOChannel().reconnect();
			} catch (IOException e) {
			}
		}
				
		ISORequest req = new ISORequest(isoMsg);		
		isoMux.queue(req);		
		System.out.println("----Get Request----");
		logger.info("----Get Request----");
		logISOMsg(isoMsg);    	
		ISOMsg isoReply = req.getResponse(30*1000);
        if (isoReply != null) {
        	System.out.println("----Get Response----");
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
