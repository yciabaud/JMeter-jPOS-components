package org.apache.jmeter.samplers.jpos;

import org.jpos.iso.PackagerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.protocol.tcp.sampler.LengthPrefixedBinaryTCPClientImpl;
import org.apache.jmeter.protocol.tcp.sampler.TCPSampler;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jpos.iso.ISOBinaryField;
import org.jpos.iso.ISOComponent;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOField;

/**
 *
 * @author "Yoann Ciabaud" <yoann.ciabaud@monext.fr>
 */
public class JPOSSampler extends TCPSampler {

    private static final Logger log = LoggingManager.getLoggerForClass();
    private final static String VARIABLE_PREFIX = "JPOSSampler.variablePrefix";
    private final static String VARIABLE_SEPARATOR = "JPOSSampler.variableSeparator";
    private final static String HEXES = "0123456789ABCDEF";
    private ISOMsg msg = new ISOMsg();

    public JPOSSampler() {

        // Default value for TCP Client
        setClassname(LengthPrefixedBinaryTCPClientImpl.class.getName());

    }

    @Override
    public SampleResult sample(Entry e) {

        SampleResult res = new SampleResult();
        res.setSampleLabel(getName());
        res.sampleStart();
        res.setSuccessful(true);

        ISOPackager customPackager = null;
        try {
            customPackager = PackagerUtil.getPackager();
        } catch (ISOException ex) {
            log.error("Erreur a la creation de l arborescence ISO", ex);
            res.sampleEnd();
            res.setSuccessful(false);

            return res;
        }

        // pour chaque variable correspondant au préfixe:
        JMeterVariables variables = JMeterContextService.getContext().getVariables();

        for (java.util.Map.Entry<String, Object> variable : variables.entrySet()) {

            // Si c'est une variable a traiter
            if (variable.getKey().startsWith(getVariablesPrefix())) {
                String tokens = variable.getKey().substring(getVariablesPrefix().length());

                try {
                    createISOField(tokens, (String) variable.getValue());
                } catch (ISOException ex) {
                    log.error("Erreur a la creation de l arborescence ISO", ex);
                    res.sampleEnd();
                    res.setSuccessful(false);

                    return res;
                }
            }

        }

        msg.setPackager(customPackager);
        msg.dump(System.out, " ");

        try {

            byte[] request = msg.pack();

            String sReq = getHex(request);
            res.setSamplerData(sReq);

            log.info("Size   :  " + sReq.length());
            log.info("Sending:  " + sReq);
            setRequestData(sReq);
        } catch (ISOException ex) {
            log.error("Erreur a la creation de l arborescence ISO", ex);
            res.sampleEnd();
            res.setSuccessful(false);

            return res;
        }
        return res;
        //return super.sample(e);
    }

    private void createISOField(String tokens, String value) throws ISOException {

        ISOMsg parent = msg;
        StringTokenizer tokenizer = new StringTokenizer(tokens.replaceAll("[a-zA-Z]", ""), getVariablesSeparator());
        List<Integer> ids = new ArrayList<Integer>();

        // On parcourt la liste des sous champs
        while (tokenizer.hasMoreTokens()) {

            String token = tokenizer.nextToken();

            Integer id;
            try {
                id = Integer.parseInt(token);
                ids.add(id);
            } catch (NumberFormatException ex) {
                log.warn("Ceci n'est pas un identifiant de champs: " + token, ex);
                continue;
            }

            ISOComponent field = null;

            // si on est sur un parent
            if (tokenizer.hasMoreTokens() && (parent == null || parent instanceof ISOMsg)) {

                log.info("Adding parent " + id);

                field = parent.getComponent(id);
                // on vérifie l'existence
                if (field == null) {
                    field = new ISOMsg(id);

                    parent.set(field);
                }

            } else if (!tokenizer.hasMoreTokens()) {

                log.info("Adding field " + id + "=" + value + "...");

                field = parent.getComponent(id);
                // on vérifie l'existence
                if (field == null) {

                    // si on est au premier niveau et que c est un champs bin
                    if (PackagerUtil.isBinary(ids, id)) {
                        field = new ISOBinaryField(id);
                    } else {
                        field = new ISOField(id);
                    }
                    parent.set(field);
                }
            } else {
                log.warn("Field " + id + "=" + value + " not set! Incompatible vars.");
                break;
            }

            // on est en derniere position
            if (!tokenizer.hasMoreTokens()) {
                if (field instanceof ISOBinaryField) {
                    field.setValue(hexStringToByteArray(value));
                } else {
                    field.setValue(value);
                }
            } else {
                parent = (ISOMsg) field;
            }

        }

    }

    /**
     *
     * @return
     */
    public String getVariablesPrefix() {
        return getPropertyAsString(VARIABLE_PREFIX);
    }

    /**
     *
     * @param variablePrefix
     */
    public void setVariablesPrefix(String variablePrefix) {
        setProperty(VARIABLE_PREFIX, variablePrefix);
    }

    /**
     *
     * @return
     */
    public String getVariablesSeparator() {
        return getPropertyAsString(VARIABLE_SEPARATOR);
    }

    /**
     *
     * @param separator
     */
    public void setVariablesSeparator(String separator) {
        setProperty(VARIABLE_SEPARATOR, separator);
    }

    public static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();

    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data;

        if (len == 1) {
            data = new byte[1];
            data[0] = (byte) Character.digit(s.charAt(0), 16);
            return data;
        } else {
            data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }
        }
        return data;
    }
}
