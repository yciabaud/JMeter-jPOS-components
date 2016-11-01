package org.apache.jmeter.samplers.jpos;

import java.awt.BorderLayout;

import org.apache.jmeter.gui.custom.JPOSConfigGui;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * @author apc
 */
public class JPOSSamplerGui
        extends AbstractSamplerGui {

    public static final String J_POS_SAMPLER_LABEL = "JPOS Sampler";
    private JPOSConfigGui tcpDefaultPanel;
    private static final Logger log = LoggingManager.getLoggerForClass();

    public JPOSSamplerGui() {
        init();
    }

    @Override
    public String getStaticLabel() {
        return J_POS_SAMPLER_LABEL;
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        log.info("running configure ...");
        tcpDefaultPanel.configure(element);
    }

    public TestElement createTestElement() {
        log.info("running createTestElement ...");
        JPOSSampler sampler = new JPOSSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    public void modifyTestElement(TestElement sampler) {
        log.info("running modifyTestElement ,,,");
        sampler.clear();
        ((JPOSSampler) sampler).addTestElement(tcpDefaultPanel.createTestElement());
        this.configureTestElement(sampler);
        //tcpDefaultPanel.modifyTestElement(sampler);
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        VerticalPanel mainPanel = new VerticalPanel();
        tcpDefaultPanel = new JPOSConfigGui(false);
        mainPanel.add(tcpDefaultPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    public String getLabelResource() {
        return "jpos_sample_title";
    }
}
