package org.apache.jmeter.util;

import java.io.IOException;

import org.apache.jmeter.iso.manager.SocketInterface;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequest;

/**
 * @author erlangga
 * email : erlangga258@gmail.com
 */
public class SocketProxy implements SocketInterface {
	
	private ISOMUX isoMux;
	
    private static final Logger logger = LoggingManager.getLoggerForClass();
	
	public SocketProxy(ISOMUX isoMUX) throws ISOException {		
		this.isoMux = isoMUX;
	}

	public ISOMsg isoRequest(final ISOMsg isoMsg, final int timeout) {		
		logger.info("masuk iso request ...");
		if(!isoMux.isConnected()){
			try {
				isoMux.getISOChannel().reconnect();
			} catch (IOException e) {
			}
		}
				
		ISORequest req = new ISORequest(isoMsg);		
		isoMux.queue(req);		
		//logger.info("----Get Request----");
		//logISOMsg(isoMsg);
		ISOMsg isoReply = req.getResponse(timeout);
        if (isoReply != null) {
        	//logger.info("----Get Response----");
        	//logISOMsg(isoReply);
        }        
		return isoReply;
	}

	@Override
	public void dispose() {
		
	}
}
