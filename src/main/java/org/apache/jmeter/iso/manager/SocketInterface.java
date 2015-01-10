package org.apache.jmeter.iso.manager;

import org.jpos.iso.ISOMsg;

/**
 * @author erlangga
 * email : erlangga258@gmail.com
 */
public interface SocketInterface {	
	public ISOMsg isoRequest(ISOMsg isoMsg, int timeout);
	public void dispose();
}
