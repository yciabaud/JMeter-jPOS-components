/**
 * 
 */
package org.apache.jmeter.gui.custom;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.gui.util.FileDialoger;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.tcp.sampler.TCPSampler;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * @author erlangga email : erlangga258@gmail.com
 */
public class CustomTCPConfigGui extends AbstractConfigGui {

	public final static String SERVER = "server"; //$NON-NLS-1$
	public final static String PORT = "port"; //$NON-NLS-1$    
	public final static String TIMEOUT = "timeout"; //$NON-NLS-1$
	public final static String CHANNEL_KEY = "channel";
	public final static String PACKAGER_KEY = "packager";
	public final static String REQ_KEY = "request2";

	private JTextField server;
	private JTextField port;
	private JTextField timeout;
	private JComboBox comboChannel;
	private JTextArea sendISO;
	private JLabel packagerPath;
	private JLabel reqPath;
	
	private String packagerFile;
	private String fileRequestData;
	
	private static final Logger log = LoggingManager.getLoggerForClass();

	private static final String[] CHANNEL_LIST = { "NACChannel" };

	private boolean displayName = true;

	public CustomTCPConfigGui() {
		this(true);
	}
	
	@Override
	public void clearGui() {
		super.clearGui();
		
		server.setText("");
		port.setText("");
		timeout.setText("");
		packagerPath.setText("");
		reqPath.setText("");
		packagerFile = "";
		fileRequestData = "";
		comboChannel.setSelectedIndex(0);
	}

	public CustomTCPConfigGui(boolean displayName) {
		this.displayName = displayName;
		init();
	}

	public String getLabelResource() {
		return "tcp_config_title";
	}

	public void configure(TestElement element) {
		server.setText(element.getPropertyAsString(TCPSampler.SERVER));
		port.setText(element.getPropertyAsString(TCPSampler.PORT));
		timeout.setText(element.getPropertyAsString(TCPSampler.TIMEOUT));
		
		if(element.getPropertyAsString(PACKAGER_KEY)!=null){
			packagerFile = element.getPropertyAsString(PACKAGER_KEY);
			packagerPath.setText(packagerFile);
		}
		
		if(element.getPropertyAsString(REQ_KEY)!=null){
			fileRequestData = element.getPropertyAsString(REQ_KEY);
			reqPath.setText(fileRequestData);
		}		
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
		element.setProperty(TCPSampler.SERVER, server.getText());
		element.setProperty(TCPSampler.PORT, port.getText());
		element.setProperty(TCPSampler.TIMEOUT, timeout.getText());		
		
		if(packagerFile!=null){
			element.setProperty(PACKAGER_KEY, packagerPath.getText());
		}		
		if(fileRequestData!=null){
			element.setProperty(REQ_KEY, reqPath.getText());
		}
	}

	private JPanel getTimeoutPanel() {
		JLabel label = new JLabel(JMeterUtils.getResString("tcp_timeout"));

		timeout = new JTextField(10);
		timeout.setName(TIMEOUT);
		label.setLabelFor(timeout);

		JPanel timeoutPanel = new JPanel(new BorderLayout(5, 0));
		timeoutPanel.add(label, BorderLayout.WEST);
		timeoutPanel.add(timeout, BorderLayout.CENTER);
		return timeoutPanel;
	}

	public String getTimeout() {
		return timeout.getText();
	}

	public String getPackagerFile() {
		if(packagerFile!=null){
			return packagerFile;
		}		
		return null;
	}
	
	public String getRequestFile(){
		if(fileRequestData!=null){
			return fileRequestData;
		}
		return null;
	}

	private JPanel getServerPanel() {
		JLabel label = new JLabel(JMeterUtils.getResString("server"));

		server = new JTextField(10);
		server.setName(SERVER);
		label.setLabelFor(server);

		JPanel serverPanel = new JPanel(new BorderLayout(5, 0));
		serverPanel.add(label, BorderLayout.WEST);
		serverPanel.add(server, BorderLayout.CENTER);
		return serverPanel;
	}

	public String getServer() {
		return server.getText();
	}

	private JPanel getPortPanel() {
		JLabel label = new JLabel(JMeterUtils.getResString("tcp_port"));

		port = new JTextField(10);
		port.setName(PORT);
		label.setLabelFor(port);

		JPanel PortPanel = new JPanel(new BorderLayout(5, 0));
		PortPanel.add(label, BorderLayout.WEST);
		PortPanel.add(port, BorderLayout.CENTER);
		return PortPanel;
	}
	
	private JPanel getAuthorPanel(){
		Box verticalBox = Box.createVerticalBox();
		
		JLabel authorLabel = new JLabel("Author:");
		JLabel yoanLabel = new JLabel("Yoann Ciabaud < y.ciabaud@gmail.com >");
		JLabel erlanggaLabel = new JLabel("Erlangga < erlangga258@gmail.com >");
		
		verticalBox.add(authorLabel);
		verticalBox.add(yoanLabel);
		verticalBox.add(erlanggaLabel);
					
		JPanel authorPanel = new JPanel(new BorderLayout(5, 0));
		authorPanel.add(verticalBox, BorderLayout.CENTER);
		return authorPanel;
	}

	public String getPort() {
		return port.getText();
	}
	
	/*
	 * https://jmeter.apache.org/api/org/apache/jmeter/gui/util/FileDialoger.html
	 */
	private JPanel getPackagerFileDialoger() {		
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
		btnChoosen.addActionListener(al);
		JPanel channelPanel = new JPanel(new BorderLayout(5, 0));
		channelPanel.add(packagerlabel, BorderLayout.WEST);
		channelPanel.add(packagerPath, BorderLayout.CENTER);
		channelPanel.add(btnChoosen, BorderLayout.EAST);
		return channelPanel;
	}
	
	private JPanel getRequestDataFromPropertiesFileDialoger() {
		JLabel reqlabel = new JLabel("Data:");		
		reqPath = new JLabel("");
		final JButton btnChoosen = new JButton("...");
		ActionListener al;
		al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if(ae.getSource()==btnChoosen){
					JFileChooser chooser = FileDialoger.promptToOpenFile(new String[]{"properties"});
					if(chooser!=null){
						reqPath.setText(chooser.getSelectedFile().getAbsolutePath());
						fileRequestData = chooser.getSelectedFile().getAbsolutePath();
						log.info("req file selected = " + fileRequestData);
					}else{
						return;
					}
				}
			}
		};
		btnChoosen.addActionListener(al);

		JPanel reqPanel = new JPanel(new BorderLayout(5, 0));
		reqPanel.add(reqlabel, BorderLayout.WEST);
		reqPanel.add(reqPath, BorderLayout.CENTER);
		reqPanel.add(btnChoosen, BorderLayout.EAST);
		return reqPanel;
	}

	
	private JPanel getSendISOPanel() {
		JLabel reqlabel = new JLabel("Field:");

		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System
				.getProperty("user.home")));		
		final JLabel reqPath = new JLabel("");
		JButton btnChoosen = new JButton("...");
		ActionListener al;
		al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				switch (fileChooser.showOpenDialog(CustomTCPConfigGui.this)) {
				case JFileChooser.APPROVE_OPTION:
					reqPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
					fileRequestData = fileChooser.getSelectedFile().getAbsolutePath();
					log.info("req file selected = " + fileRequestData);
					break;
				}
			}
		};
		btnChoosen.addActionListener(al);

		JPanel reqPanel = new JPanel(new BorderLayout(5, 0));
		reqPanel.add(reqlabel, BorderLayout.WEST);
		reqPanel.add(reqPath, BorderLayout.CENTER);
		reqPanel.add(btnChoosen, BorderLayout.EAST);
		return reqPanel;
	}


	private JPanel getChannelPanel() {
		JLabel channellabel = new JLabel("Channel");

		comboChannel = new JComboBox(CHANNEL_LIST);
		comboChannel.setSelectedIndex(0);
		channellabel.setLabelFor(comboChannel);

		JPanel channelPanel = new JPanel(new BorderLayout(5, 0));
		channelPanel.add(channellabel, BorderLayout.WEST);
		channelPanel.add(comboChannel, BorderLayout.CENTER);
		return channelPanel;
	}

	public String getChannel() {
		String channel = (String) comboChannel.getSelectedItem();
		return channel.trim().toLowerCase();
	}

	private void init() {
		setLayout(new BorderLayout(0, 5));

		if (displayName) {
			setBorder(makeBorder());
			add(makeTitlePanel(), BorderLayout.NORTH);
		}

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(getChannelPanel());
		mainPanel.add(getPackagerFileDialoger());
		mainPanel.add(getServerPanel());
		mainPanel.add(getPortPanel());
		mainPanel.add(getTimeoutPanel());
		mainPanel.add(getRequestDataFromPropertiesFileDialoger());
		mainPanel.add(getAuthorPanel());

		add(mainPanel, BorderLayout.CENTER);
	}
}
