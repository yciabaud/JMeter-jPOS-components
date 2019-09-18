package org.jpos.iso;

import org.apache.commons.codec.binary.Hex;
import org.jpos.iso.packager.GenericSubFieldPackager;
import org.jpos.util.LogEvent;
import org.jpos.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Packager de sous champ avec concaténation des caracteres avant passage en byte.
 * @author YCIABAUD
 *
 *
 * @author Yoann Ciabaud
 * @see GenericSubFieldPackager
 */
public class TLVSubFieldPackager extends CustomSubFieldPackager {
    
    public TLVSubFieldPackager() throws ISOException {
        super();
    }

    /**
     * Pack the subfield into a byte array
     */
    @Override
    public byte[] pack(ISOComponent m) throws ISOException {
        LogEvent evt = new LogEvent(this, "pack");
        try {
            ISOComponent c;
            List<byte[]> l = new ArrayList<byte[]>();
            Map fields = m.getChildren();
            int len = 0;

            if (emitBitMap()) {
                // BITMAP (-1 in HashTable)
                c = (ISOComponent) fields.get(new Integer(-1));
                byte[] b = getBitMapfieldPackager().pack(c);
                len += b.length;
                l.add(b);
            }

            StringBuilder sb = new StringBuilder();
            for (int i = getFirstField(); i <= m.getMaxField(); i++) {
                c = (ISOComponent) fields.get(new Integer(i));
                if (c == null && !emitBitMap()) {
                    c = new ISOField(i, "");
                }
                if (c != null) {
                    try {
                       if(fld[i]!=null){
                            byte[] b = fld[i].pack(c);

                            byte[] id = Hex.decodeHex(String.format("%04d", i).toCharArray());
                            byte[] mybyte = new byte[1];
                            mybyte[0] = new Integer(b.length).byteValue();
                            l.add(id);
                            l.add(mybyte);
                            l.add(b);
                            
                            len += b.length + 1 + id.length;
                        }

                    } catch (Exception e) {
                        evt.addMessage("error packing subfield " + i);
                        evt.addMessage(c);
                        evt.addMessage(e);
                        throw e;
                    }
                }
            }

            byte[] concatResult = ISOUtil.hex2byte(sb.toString());
            l.add(concatResult);
            len += concatResult.length;

            int k = 0;
            byte[] d = new byte[len];
            for (byte[] b : l) {
                System.arraycopy(b, 0, d, k, b.length);
                k += b.length;
            }
            if (logger != null) // save a few CPU cycle if no logger available
            {
                evt.addMessage(ISOUtil.hexString(d));
            }
            return d;
        } catch (ISOException e) {
            evt.addMessage(e);
            throw e;
        } catch (Exception e) {
            evt.addMessage(e);
            throw new ISOException(e);
        } finally {
            Logger.log(evt);
        }
    }
}