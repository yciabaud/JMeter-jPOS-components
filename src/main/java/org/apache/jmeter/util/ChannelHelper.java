package org.apache.jmeter.util;

import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.channel.NACChannel;

/**
 * @author erlangga
 * email : erlangga258@gmail.com
 */
public class ChannelHelper {
	
	private String tpdu = "0000000000"; // default
	
	public String getTpdu() {
		return tpdu;
	}

	public void setTpdu(String tpdu) {
		this.tpdu = tpdu;
	}
	
	public BaseChannel getChannel(final String host, final int port, final ISOPackager packager, final String channelName){
		BaseChannel baseChannel = null;
		if(channelName.equalsIgnoreCase("nacchannel")){
			baseChannel = new NACChannel(host, port, packager, ISOUtil.hex2byte(tpdu));
		}
		return baseChannel;
	}	
}
