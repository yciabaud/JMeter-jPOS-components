package org.apache.jmeter.iso.manager;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;

/**
 * @author erlangga
 * email : erlangga258@gmail.com
 */
public class ISOMUXSingleton {
	
	private static volatile ISOMUXSingleton INSTANCE;
	private static ISOMUX isoMux;
    private static final Logger logger = LoggingManager.getLoggerForClass();
    
//    private static Thread threadIsoMux;
//    private static ExecutorService executor; // Better solutions for running threads
	
	public static ISOMUXSingleton getInstance(final BaseChannel channel){
		if(INSTANCE==null){
			logger.info("Call ISOMUXSingleton getInstance()");
			synchronized (ISOMUXSingleton.class) {
				if(INSTANCE==null){
					logger.info("ISOMUXSingleton instanceciated");
					INSTANCE = new ISOMUXSingleton(channel);
					
//					executor = Executors.newSingleThreadExecutor();
//					executor.execute(INSTANCE.getISOMUX());
					
//					threadIsoMux = new Thread(INSTANCE.getISOMUX());		
//					logger.info("start thread iso mux");
//					threadIsoMux.start();					
				}
			}
		}
		return INSTANCE;
	}

	private ISOMUXSingleton(final BaseChannel channel){
		isoMux = new ISOMUX(channel) {
			@Override
			protected String getKey(ISOMsg m) throws ISOException {
				return super.getKey(m);
			}
		};	
	}
	
	public ISOMUX getISOMUX(){
		return isoMux;
	}
	
	public boolean isConnected(){
		return isoMux.isConnected();
	}
	
	public void terminate(){
		if(isoMux!=null){
			isoMux.terminate();		
			isoMux = null;
		}
		
//		if(threadIsoMux!=null){
//			threadIsoMux = null;
//		}
		
//		if(executor!=null){
//			executor.shutdown();
//			executor = null;
//		}
		
		if(INSTANCE!=null){
			INSTANCE = null;
		}		
	}	
}
