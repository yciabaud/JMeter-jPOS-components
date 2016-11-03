/**
 * 
 */
package org.apache.jmeter.gui.custom;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.gui.util.FileDialoger;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.jpos.JPOSSampler;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author erlangga
 * email : erlangga258@gmail.com
 */
public class JPOSConfigGui extends AbstractConfigGui {

	private final static String SERVER = "server"; //$NON-NLS-1$
	private final static String PORT = "port"; //$NON-NLS-1$
	private final static String TIMEOUT = "timeout"; //$NON-NLS-1$
	private final static String CHANNEL_KEY = "channel";
	private final static String PACKAGER_KEY = "packager";
	private final static String REQUEST = "request";
	//private final static String RETURN_TYPE_KEY = "returnType"; //$NON-NLS-1$

	private JTextField server;
	private JTextField port;
	private JTextField timeout;
	private JComboBox comboChannel;
	private JLabel packagerPath;
	private JTextArea requestData;
	private JRadioButton rbText;
	private JRadioButton rbJSON;

	public static final String TEXT = "rb_text";
	public static final String JSON = "rb_json";
	
	private String packagerFile;
	private String channelSelected;
	
	private static final Logger log = LoggingManager.getLoggerForClass();

	private static final String[] CHANNEL_LIST = { "NACChannel" };

	private boolean displayName = true;

	public JPOSConfigGui() {
		this(true);
	}

	public JPOSConfigGui(boolean displayName) {
		this.displayName = displayName;
		init();
	}

	public String getLabelResource() {
		return "JPOS Sampler Config";
	}

	public void configure(TestElement element) {
		server.setText(element.getPropertyAsString(JPOSSampler.SERVER));
		port.setText(element.getPropertyAsString(JPOSSampler.PORT));
		timeout.setText(element.getPropertyAsString(JPOSSampler.TIMEOUT));

		if (element.getPropertyAsString(JPOSSampler.CHANNEL)!=null) {
			channelSelected = element.getPropertyAsString(JPOSSampler.CHANNEL);
			comboChannel.setSelectedItem(channelSelected);
		}

		packagerFile = element.getPropertyAsString(JPOSSampler.PACKAGER);
		packagerPath.setText(packagerFile);

		if(element.getPropertyAsString(JPOSSampler.RETURN_TYPE_KEY).equalsIgnoreCase(TEXT)){
			rbText.setSelected(true);
			rbJSON.setSelected(false);
		}else {
			rbJSON.setSelected(true);
			rbText.setSelected(false);
		}

		requestData.setText(element.getPropertyAsString(JPOSSampler.REQUEST));
		super.configure(element);
	}

	public TestElement createTestElement() {
		ConfigTestElement element = new ConfigTestElement();
		modifyTestElement(element);
		return element;
	}

	/**
	 * Modifies a given TestElement to mirror the data in the gui components.
	 * 
	 * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
	 */
	public void modifyTestElement(TestElement element) {
		element.clear();
		configureTestElement(element);
		element.setProperty(JPOSSampler.SERVER, server.getText());
		element.setProperty(JPOSSampler.PORT, port.getText());
		element.setProperty(JPOSSampler.TIMEOUT, timeout.getText());
		element.setProperty(JPOSSampler.CHANNEL, (String) comboChannel.getSelectedItem());
		element.setProperty(JPOSSampler.PACKAGER, packagerPath.getText());
		element.setProperty(JPOSSampler.REQUEST,requestData.getText());
		if(rbText.isSelected()){
			element.setProperty(JPOSSampler.RETURN_TYPE_KEY, TEXT);
		}else if(rbJSON.isSelected()){
			element.setProperty(JPOSSampler.RETURN_TYPE_KEY, JSON);
		}
	}

	private JPanel createTimeoutPanel() {
		JLabel label = new JLabel("Timeout");

		timeout = new JTextField(10);
		timeout.setName(TIMEOUT);
		label.setLabelFor(timeout);

		JPanel timeoutPanel = new JPanel(new BorderLayout(5, 0));
		timeoutPanel.add(label, BorderLayout.WEST);
		timeoutPanel.add(timeout, BorderLayout.CENTER);
		return timeoutPanel;
	}

	private JPanel createResponseType(){
		JLabel label = new JLabel("Return Type");

		rbText = new JRadioButton("Text");
		rbJSON = new JRadioButton("JSON");

		ButtonGroup bg=new ButtonGroup();
		bg.add(rbText);
		bg.add(rbJSON);

		JPanel panel = new JPanel(new GridLayout(1,3));
		panel.add(label);
		panel.add(rbText);
		panel.add(rbJSON);
		return panel;
	}

	public String getTimeout() {
		return timeout.getText();
	}

	private JPanel createServerPanel() {
		JLabel label = new JLabel("Server");

		server = new JTextField(10);
		server.setName(SERVER);
		label.setLabelFor(server);

		JPanel serverPanel = new JPanel(new BorderLayout(5, 0));
		serverPanel.add(label, BorderLayout.WEST);
		serverPanel.add(server, BorderLayout.CENTER);
		return serverPanel;
	}

	private JPanel createPortPanel() {
		JLabel label = new JLabel("Port");

		port = new JTextField(10);
		port.setName(PORT);
		label.setLabelFor(port);

		JPanel PortPanel = new JPanel(new BorderLayout(5, 0));
		PortPanel.add(label, BorderLayout.WEST);
		PortPanel.add(port, BorderLayout.CENTER);
		return PortPanel;
	}

	private JPanel createPackagerFileDialoger() {
		JLabel packagerlabel = new JLabel("Packager:");		
		packagerPath = new JLabel("");
		final JButton btnChoosen = new JButton("...");
		ActionListener al;
		al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {				
				if(ae.getSource() == btnChoosen){
					JFileChooser chooser = FileDialoger.promptToOpenFile(new String[]{"xml"});
					if(chooser!=null){
						packagerPath.setText(chooser.getSelectedFile().getAbsolutePath());
						packagerFile = chooser.getSelectedFile().getAbsolutePath();
						log.info("packager file selected = " + packagerFile);
					}else{
						return;
					}
				}				
			}
		};
		packagerlabel.setName(PACKAGER_KEY);
		btnChoosen.addActionListener(al);
		JPanel channelPanel = new JPanel(new BorderLayout(5, 0));
		channelPanel.add(packagerlabel, BorderLayout.WEST);
		channelPanel.add(packagerPath, BorderLayout.CENTER);
		channelPanel.add(btnChoosen, BorderLayout.EAST);
		return channelPanel;
	}

	private JPanel createRequestPanel() {
		JLabel reqLabel = new JLabel("Data");
		requestData = new JTextArea(10, 0);
		requestData.setLineWrap(true);
		requestData.setName(REQUEST);
		reqLabel.setLabelFor(requestData);

		JPanel reqDataPanel = new JPanel(new BorderLayout(5, 0));
		reqDataPanel.add(reqLabel, BorderLayout.WEST);
		reqDataPanel.add(new JScrollPane(requestData), BorderLayout.CENTER);
		return reqDataPanel;
	}

	private JPanel getChannelPanel() {
		JLabel channellabel = new JLabel("Channel");

		comboChannel = new JComboBox(CHANNEL_LIST);
		comboChannel.setSelectedIndex(0);
		comboChannel.setName(CHANNEL_KEY);
		channellabel.setLabelFor(comboChannel);

		JPanel channelPanel = new JPanel(new BorderLayout(5, 0));
		channelPanel.add(channellabel, BorderLayout.WEST);
		channelPanel.add(comboChannel, BorderLayout.CENTER);
		return channelPanel;
	}

	private void init() {
		setLayout(new BorderLayout(0, 5));

		if (displayName) {
			setBorder(makeBorder());
			add(makeTitlePanel(), BorderLayout.NORTH);
		}

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(getChannelPanel());
		mainPanel.add(createPackagerFileDialoger());
		mainPanel.add(createServerPanel());
		mainPanel.add(createPortPanel());
		mainPanel.add(createTimeoutPanel());
		mainPanel.add(createResponseType());
		mainPanel.add(createRequestPanel());

		add(mainPanel, BorderLayout.CENTER);
	}
}
