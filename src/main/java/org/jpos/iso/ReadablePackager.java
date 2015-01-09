package org.jpos.iso;


/**
 * Interface qui permet de lire la classe d'un champ depuis un packager.
 * @see ISOPackager
 * @author YCIABAUD
 */
interface ReadablePackager {
    
    /**
     * Retourne le type d'un champs.
     * @param fldNumber id du champs
     * @return Classse associée
     */
    ISOFieldPackager getField(int fldNumber);

}
