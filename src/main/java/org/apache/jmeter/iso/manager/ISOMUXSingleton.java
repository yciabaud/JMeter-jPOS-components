package org.apache.jmeter.iso.manager;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.channel.NACChannel;

/**
 * @author erlangga
 * email : erlangga258@gmail.com
 */
public class ISOMUXSingleton {
	
	private static volatile ISOMUXSingleton INSTANCE;
	private static ISOMUX isoMux;
	
	public static ISOMUXSingleton getInstance(ISOPackager packager, final String hostname, final int port){
		if(INSTANCE==null){
			synchronized (ISOMUXSingleton.class) {
				if(INSTANCE==null){			
					NACChannel channel = new NACChannel(hostname, port, packager, new byte[10]);
					INSTANCE = new ISOMUXSingleton(channel);
				}
			}
		}
		return INSTANCE;
	}

	private ISOMUXSingleton(NACChannel channel){
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
		isoMux.terminate();		
		isoMux = null;
	}	
}
