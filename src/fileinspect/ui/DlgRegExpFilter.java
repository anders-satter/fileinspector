package fileinspect.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import fileinspect.ui.resource.ColumnLayout;
import fileinspect.ui.resource.FileInspectorFont;

public class DlgRegExpFilter extends JDialog{
	private static final long serialVersionUID = 1L;
	private JLabel iLbMessage;
	private JTextField iExpression;
	private JButton iBtOK;
	private JButton iCancel;
	private boolean iIsConfirmed;
	private String iReturnText;


	/**
	 * Konstruktor.
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public DlgRegExpFilter(Frame owner, String title, boolean modal, String aCurrentCharCount) throws HeadlessException {
		super(owner, title, modal);
		initComps(aCurrentCharCount);
	}

	
	
	private void initComps(String aCurrentCharCount){
		setResizable(false);
		Dimension tDim = new Dimension(400, 20);
		iLbMessage = new JLabel();
		iLbMessage.setFont(FileInspectorFont.getConsoleFont());
		iLbMessage.setPreferredSize(tDim);
		iLbMessage.setText("Enter new regular expression: (current setting "+ aCurrentCharCount + "):");

		iExpression = new JTextField();
		iExpression.requestFocus();
		iExpression.setFont(FileInspectorFont.getConsoleFont());
		iExpression.setPreferredSize(tDim);
		

		
		
		iBtOK = new JButton("Confirm");	
		iBtOK.setDefaultCapable(true);		
		iBtOK.addActionListener(new ConfirmActionListener());
		getRootPane().setDefaultButton(iBtOK);
				
		iCancel = new JButton("Cancel");
		iCancel.addActionListener(new CancelActionListener()); 

		ColumnLayout tLayout = new ColumnLayout();
		tLayout.setColumnSpanNumber(2);
		tLayout.addComponent(iLbMessage, 0, 0);
		tLayout.addComponent(iExpression, 1, 0);
		tLayout.setColumnSpanNumber(1);
		tLayout.addComponent(iBtOK, 2, 0);
		tLayout.addComponent(iCancel, 2, 1);

		setSize(new Dimension(100, 75));
		getContentPane().add(tLayout.getPanel());
		pack();

	}
	/**
	 * Hämtar IsConfirmed
	 * @return boolean med IsConfirmed
	 */
	public boolean getIsConfirmed() {
		return iIsConfirmed;
	}

	
	public void focusInputTextField(){
		iExpression.requestFocus();
	}
	
	/**
	 * Hämtar ReturnText
	 * @return String med ReturnText
	 */
	public String getReturnText() {
		return iReturnText;
	}

	public void setExpression(String aExpression){
		iExpression.setText(aExpression);
	}
	

	private class ConfirmActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			iReturnText = iExpression.getText();
			iIsConfirmed = true;
			DlgRegExpFilter.this.setVisible(false);
		}
	}
	private class CancelActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			iReturnText = "";
			iIsConfirmed = false;
			DlgRegExpFilter.this.setVisible(false);
		}
	}

	
	

	

}
