/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jpos.iso;

import java.io.InputStream;

import org.jpos.iso.packager.GenericPackager;

/**
 * Packager avec l'option de lecture des classes de champs.
 * @author YCIABAUD
 */
class CustomPackager extends GenericPackager implements ReadablePackager {

    public CustomPackager(InputStream is) throws ISOException {
        super(is);
    }
    
    @Override
    public ISOFieldPackager getField(int fldNumber) {
        return fld[fldNumber];
    }
    

}
