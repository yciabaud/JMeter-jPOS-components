package org.apache.jmeter.iso.manager;

import org.jpos.iso.ISOMsg;

public interface SocketInterface {	
	public ISOMsg isoRequest(ISOMsg isoMsg);
	public void dispose();
}
