/*
 * Created on 2005-jul-04
 */
package fileinspect.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import fileinspect.ui.resource.ColumnLayout;
import fileinspect.ui.resource.FileInspectorFont;

/**
 * Search dialog
 */
public class DlgSearch extends JDialog {
	private static final long serialVersionUID = 1L;
	private JLabel iLbSearch;
	private JTextField iFldSearchText;
	private JButton iSearch;
	private JButton iCancel;
	private boolean iIsConfirmed;
	private String iReturnText;

	private void initComps() {
		Dimension tDim = new Dimension(400, 20);
		iLbSearch = new JLabel();
		iLbSearch.setFont(FileInspectorFont.getConsoleFont());
		iLbSearch.setPreferredSize(tDim);
		iLbSearch.setText("Enter search string:");

		iFldSearchText = new JTextField();
		iFldSearchText.requestFocus();
		iFldSearchText.setFont(FileInspectorFont.getConsoleFont());
		iFldSearchText.setPreferredSize(tDim);

		iSearch = new JButton("Search");	
		iSearch.setDefaultCapable(true);		
		iSearch.addActionListener(new ConfirmActionListener());
		getRootPane().setDefaultButton(iSearch);
		
//		iSearch.setAction(new ConfirmAction());
		
		iCancel = new JButton("Cancel");
		iCancel.addActionListener(new CancelActionListener()); 

		ColumnLayout tLayout = new ColumnLayout();
		tLayout.setColumnSpanNumber(2);
		tLayout.addComponent(iLbSearch, 0, 0);
		tLayout.addComponent(iFldSearchText, 1, 0);
		tLayout.setColumnSpanNumber(1);
		tLayout.addComponent(iSearch, 2, 0);
		tLayout.addComponent(iCancel, 2, 1);

		setSize(new Dimension(100, 75));
		getContentPane().add(tLayout.getPanel());
		pack();
	}

	public void focusSearchTextField(){
		iFldSearchText.requestFocus();
	}
	private class ConfirmActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			iReturnText = iFldSearchText.getText();
			iIsConfirmed = true;
			DlgSearch.this.setVisible(false);
		}
	}
	private class CancelActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			iReturnText = "";
			iIsConfirmed = false;
			DlgSearch.this.setVisible(false);
		}
	}
	/**
	 * H�mtar IsConfirmed
	 * @return boolean med IsConfirmed
	 */
	public boolean getIsConfirmed() {
		return iIsConfirmed;
	}

	/**
	 * H�mtar ReturnText
	 * @return String med ReturnText
	 */
	public String getReturnText() {
		return iReturnText;
	}

	/**
	 * Konstruktor.
	 * @throws java.awt.HeadlessException
	 */
	public DlgSearch() throws HeadlessException {
		super();
		initComps();
	}

	/**
	 * Konstruktor.
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public DlgSearch(Frame owner) throws HeadlessException {
		super(owner);
		initComps();
	}

	/**
	 * Konstruktor.
	 * @param owner
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public DlgSearch(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		initComps();
	}

	/**
	 * Konstruktor.
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public DlgSearch(Frame owner, String title) throws HeadlessException {
		super(owner, title);
		initComps();
	}

	/**
	 * Konstruktor.
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public DlgSearch(Frame owner, String title, boolean modal) throws HeadlessException {
		super(owner, title, modal);
		initComps();

	}

	/**
	 * Konstruktor.
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public DlgSearch(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		initComps();
	}

	/**
	 * Konstruktor.
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public DlgSearch(Dialog owner) throws HeadlessException {
		super(owner);
		initComps();

	}

	/**
	 * Konstruktor.
	 * @param owner
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public DlgSearch(Dialog owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		initComps();
	}

	/**
	 * Konstruktor.
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public DlgSearch(Dialog owner, String title) throws HeadlessException {
		super(owner, title);
		initComps();
	}

	/**
	 * Konstruktor.
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public DlgSearch(Dialog owner, String title, boolean modal) throws HeadlessException {
		super(owner, title, modal);
		initComps();
	}

	/**
	 * Konstruktor.
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 * @throws java.awt.HeadlessException
	 */
	public DlgSearch(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
		initComps();
	}

}
