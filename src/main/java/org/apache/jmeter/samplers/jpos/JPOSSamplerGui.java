package org.apache.jmeter.samplers.jpos;

import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.tcp.config.gui.TCPConfigGui;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 *
 * @author apc
 */
public class JPOSSamplerGui
        extends AbstractSamplerGui {

    private TCPConfigGui tcpDefaultPanel;
    
     private static final Logger log = LoggingManager.getLoggerForClass();

    /**
     *
     */
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
        tcpDefaultPanel.configure(element);
        
    }

    public TestElement createTestElement() {
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
        sampler.clear();
        sampler.addTestElement(tcpDefaultPanel.createTestElement());
        this.configureTestElement(sampler);
    }

    /**
     * Implements JMeterGUIComponent.clearGui
     */
    @Override
    public void clearGui() {
        super.clearGui();

        tcpDefaultPanel.clearGui();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        add(makeTitlePanel(), BorderLayout.NORTH);

        VerticalPanel mainPanel = new VerticalPanel();
        
        // Ajout de la section jPOS
        mainPanel.add(createJPOSPanel());
        

        tcpDefaultPanel = new TCPConfigGui(false);
        
        // On enleve la partie de définition du texte
        JPanel mainpanel2 = (JPanel) tcpDefaultPanel.getComponent(tcpDefaultPanel.getComponentCount()-1);
        
        Box box = (Box) mainpanel2.getComponent(mainpanel2.getComponentCount()-1);
        
        JPanel reqDataPanel = (JPanel) box.getComponent(10);
        
        reqDataPanel.setVisible(false);
        
        mainPanel.add(tcpDefaultPanel);

        /*loginPanel = new LoginConfigGui(false);
        loginPanel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString("login_config"))); // $NON-NLS-1$
        mainPanel.add(loginPanel);*/

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createJPOSPanel(){
        
        VerticalPanel panel = new VerticalPanel();
        
        // nom de la variable (préfixe) à utiliser pour les champs
        
        
        return panel;
    }
    
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }
}
