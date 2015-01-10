package org.jpos.iso;

import java.io.InputStream;
import java.util.List;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * Instancie un packager et fournit des méthodes utilitaires.
 * @author YCIABAUD
 */
public class PackagerUtil {
    
    private static final Logger log = LoggingManager.getLoggerForClass();
    private static CustomPackager instance;

    public static ISOPackager getPackager() throws ISOException {
        if (instance == null) {
            String filename = "/stur.xml";
            InputStream is = PackagerUtil.class.getResourceAsStream(filename);
            instance = new CustomPackager(is);
        }

        return instance;
    }
    
    public static boolean isBinary(List<Integer> ids, int field) throws ISOException{
        
        ISOFieldPackager p = findCurrentPackager(ids);
        
        return IFB_BINARY.class.equals(p.getClass());
    }

    public static ISOFieldPackager findCurrentPackager(List<Integer> ids) {
        
        ISOFieldPackager currentPackager = null;
        
        // on ploge dans l arborescence
        for(Integer id : ids){
            
            // Root
            if(currentPackager == null)
                currentPackager = instance.getFieldPackager(id);
                    
            // Node
            else if(currentPackager instanceof ISOMsgFieldPackager) {
                
                if( ((ISOMsgFieldPackager)currentPackager).getISOMsgPackager() instanceof ReadablePackager ){
                    currentPackager = ((ReadablePackager)
                            ((ISOMsgFieldPackager)currentPackager).getISOMsgPackager()).getField(id);
                }else{

                    log.debug("This packager cannot be read: using ISOField "+ids.toString());
                    return null;

                }
            }
            // leaf... 
            

        }
        
        return currentPackager;
        
    }
}