package org.apache.jmeter.samplers.jpos;

import java.awt.BorderLayout;

import org.apache.jmeter.gui.custom.CustomTCPConfigGui;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 *
 * @author apc
 * "Erlangga" <erlangga258@gmail.com>
 */
public class JPOSSamplerGui
        extends AbstractSamplerGui {
	
	private CustomTCPConfigGui tcpDefaultPanel;	
    private static final Logger log = LoggingManager.getLoggerForClass();
    
    public JPOSSamplerGui() {
        init();
    }

    @Override
    public String getStaticLabel() {
        return "jPOS Sampler";
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        log.info("running configure ,,,");        
        tcpDefaultPanel.configure(element);        
    }

    public TestElement createTestElement() {
    	log.info("running createTestElement ,,,");
        JPOSSampler sampler = new JPOSSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    public void modifyTestElement(TestElement sampler) {
        //sampler.clear();
        //sampler.addTestElement(tcpDefaultPanel.createTestElement());  
    	log.info("running modifyTestElement ,,,");
        sampler.setProperty(CustomTCPConfigGui.SERVER, tcpDefaultPanel.getServer());
        sampler.setProperty(CustomTCPConfigGui.PORT, tcpDefaultPanel.getPort());
        sampler.setProperty(CustomTCPConfigGui.TIMEOUT, tcpDefaultPanel.getTimeout());
        sampler.setProperty(CustomTCPConfigGui.CHANNEL_KEY, tcpDefaultPanel.getChannel());
        
        if(tcpDefaultPanel.getPackagerFile()!=null){
        	sampler.setProperty(CustomTCPConfigGui.PACKAGER_KEY, tcpDefaultPanel.getPackagerFile());
        }
        
        if(tcpDefaultPanel.getRequestFile()!=null){
        	sampler.setProperty(CustomTCPConfigGui.REQ_KEY, tcpDefaultPanel.getRequestFile());
        }
        
        this.configureTestElement(sampler);
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        
        VerticalPanel mainPanel = new VerticalPanel();                
        tcpDefaultPanel = new CustomTCPConfigGui(false);        
        mainPanel.add(tcpDefaultPanel);

        add(mainPanel, BorderLayout.CENTER);
    }
    
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }
}
