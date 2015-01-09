/**
 * 
 */
package org.apache.jmeter.samplers.jpos;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

/**
 * @author erlangga
 *
 */
public class SampleISOMsg {	
	public static ISOMsg getSampleISOMsg() throws ISOException{
		ISOMsg isoRequest = new ISOMsg();		
		isoRequest.setMTI("0100");
		isoRequest.set(3, "340000");
		isoRequest.set(11, String.valueOf(System.currentTimeMillis() % 1000000)); //System Trace Audit Number (STAN)
		isoRequest.set(24, "001");
		isoRequest.set(32, "00000000002");
		isoRequest.set(35,"1101003009039314045=000000000000000");
		isoRequest.set(41, "00000099");
		isoRequest.set(42, "300004000050046");
		return isoRequest;
	}
}
