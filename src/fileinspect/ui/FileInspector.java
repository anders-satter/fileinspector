/*
 * Created on 2005-jun-29
 */
package fileinspect.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import fileinspect.command.InspectionPerformer;
import fileinspect.sql.Sql;
import fileinspect.ui.resource.ColumnLayout;
import fileinspect.ui.resource.Console;
import fileinspect.ui.resource.FileInspectorColor;
import fileinspect.ui.resource.FileInspectorFont;
import fileinspect.util.PersistenceManager;
import fileinspect.util.SettingsListener;
import fileinspect.util.Utils;

/**
 * Main class
 */
public class FileInspector extends JFrame implements Console, SettingsListener {
	private static final long serialVersionUID = 1L;
	
	protected String iDirectory = "c:/ws/";
	protected JScrollPane iConsoleScrollPane;
	protected JTextPane iConsole;
	private ColumnLayout tTextPaneLayout;
	private Timer iConsoleScrollBarTimer;
	//private Timer iMonitorTimer;
	protected int iTimerVisitIndex = 0;
	protected Sql iSql;
	private int iMonitorFrequency = 2000;
	//private Collection iMonitoredFileList;
	private DlgSearch iDlgSearch;
	private boolean iVerbose = true;
	private final boolean cDEBUG = false;

	protected JPopupMenu iPopup;
	private boolean iScrollToMax = true;
	private DisableScrollUpdate iDisableScrollUpdate = new DisableScrollUpdate();
	private EnableScrollUpdate iEnableScrollUpdate = new EnableScrollUpdate();
	private DisableConsoleUpdate iDisableConsoleUpdate = new DisableConsoleUpdate();
	private EnableConsoleUpdate iEnableConsoleUpdate = new EnableConsoleUpdate();
	
	private int iMaxNumberOfCharsShown = 500000;
	private boolean iPerformUpdate = true;
	
	private String iFileFilterRegExp="";
	private JLabel iRegExpLabel;
	private static final String CURRENT_REGEXP_FILTER = "Current File Filter: ";
	private static final String CURRENT_LINE_REGEXP_FILTER = "Current Line Filter: ";
	
	private String iLineFilterRegExpString = "";



	/**
	 * Konstruktor.
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public FileInspector(String title) throws HeadlessException {
		super(title);
		PersistenceManager.getInstance().addSettingsListener(this);
		PersistenceManager.getInstance().unPersist();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	FileInspector.this.performClose();
            }
        };
        this.addWindowListener(exitListener);
		
		
		iConsole = new JTextPane();

		manageStyles(iConsole);

		iConsole.setFont(FileInspectorFont.getConsoleFont());
		iConsole.setBorder(new EtchedBorder(1));
		iConsole.setBackground(FileInspectorColor.consoleBackColor());
		iConsole.setForeground(FileInspectorColor.consoleFontColor());
		iConsole.addKeyListener(new ConsoleKeyListener());
		iConsoleScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		iConsoleScrollPane.getViewport().add(iConsole);
		iConsoleScrollPane.setPreferredSize(new Dimension(689, 500));
		
		ColumnLayout tTotalLayout = new ColumnLayout();
		
		
		tTextPaneLayout = new ColumnLayout();
		tTextPaneLayout.setColumnSpanNumber(2);
		GridBagConstraints tConstraints = tTextPaneLayout.getGridConstraints();
		tConstraints.weightx = 1.0;
		tConstraints.weighty = 1.0;
		tTextPaneLayout.addComponent(iConsoleScrollPane, 0, 0);
		
		//getContentPane().add(tTextPaneLayout.getPanel());
		
		
		int tDelay = 500; //milliseconds
		iConsoleScrollBarTimer = new Timer(tDelay, new MoveConsoleToLastRow());
		ColumnLayout tMenuLayout = new ColumnLayout();
		tMenuLayout.getGridConstraints().weighty = 0.0;
		createMenus(tMenuLayout);
		
		iRegExpLabel = new JLabel();
		//iRegExpLabel.setText(iFileMatchRegExp);
		iRegExpLabel.setText(CURRENT_REGEXP_FILTER + iFileFilterRegExp);
		iRegExpLabel.setEnabled(true);
		tMenuLayout.addComponent(iRegExpLabel, 0, 1);
		
		//getContentPane().add(tMenuLayout.getPanel());
		
		//tMenuLayout.getGridConstraints().fill= GridBagConstraints.NONE;
//		tMenuLayout.getGridConstraints().weighty = 0.0;
//		tMenuLayout.getGridConstraints().gridheight = 0;
//		tMenuLayout.getGridConstraints().gridwidth = 0;
		
		tTotalLayout.getGridConstraints().weighty = 0.;
//		tTotalLayout.getGridConstraints().gridheight = 0;
//		tTotalLayout.getGridConstraints().gridwidth = 0;

		
		tTotalLayout.addComponent(tMenuLayout.getPanel(),0,0);
		
		tTotalLayout.getGridConstraints().weighty = 1.0;
//		tTotalLayout.getGridConstraints().gridheight = 0;
//		tTotalLayout.getGridConstraints().gridwidth = 0;
		
		tTotalLayout.addComponent(tTextPaneLayout.getPanel(), 1, 0);
		

		getContentPane().add(tTotalLayout.getPanel());
		
		
		createPopup();
		/* read the default file-if it exists */		
		 BufferedImage image = null;
         try {
             image = ImageIO.read(this.getClass().getResource("magn_glass2.jpg"));
         } catch (IOException e) {
             e.printStackTrace();
         }        
		setIconImage(image);
		pack();
		setVisible(true);
		FileInspector.this.iConsole.requestFocus();
		if (!iDirectory.equals("c:/ws/")){
			startMonitor();
		}

	}

	public void setConsoleTitle(String aTitle){
		setTitle(aTitle);
	}

	public void setNumOfChars() {
		DlgRegExpFilter tDlgCharNum = new DlgRegExpFilter(this, "Num of chars", true, String.valueOf(iMaxNumberOfCharsShown));
		tDlgCharNum.setVisible(true);
		tDlgCharNum.focusInputTextField();
		if (tDlgCharNum.getIsConfirmed()) {
			try {
				iMaxNumberOfCharsShown = new Integer(tDlgCharNum.getReturnText()).intValue();
			} catch (NumberFormatException e) {
				// just ignore ...
			}
		} else {
			return;
		}
	}

	
	
	
	
	
	public void createPopup() {
		/*popup*/
		iPopup = new JPopupMenu("excution");
		iPopup.add(iDisableScrollUpdate);
		iPopup.add(new CharCount());
		iPopup.add(iDisableConsoleUpdate);		
		iPopup.add(new SetNumberOfCharsAction());
		iConsole.addMouseListener(new MouseHandler());
	}

	public void changeAction(int aRemoveActionIndex, AbstractAction aAddAction) {
		iPopup.remove(aRemoveActionIndex);
		iPopup.insert(aAddAction, aRemoveActionIndex);
	}

	private class DisableConsoleUpdate extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public DisableConsoleUpdate() {
			super("Disable update");
		}

		public void actionPerformed(ActionEvent e) {
			iPerformUpdate = false;
			FileInspector.this.changeAction(2, FileInspector.this.iEnableConsoleUpdate);
		}
	}

	private class EnableConsoleUpdate extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public EnableConsoleUpdate() {
			super("Enable update");
		}

		public void actionPerformed(ActionEvent e) {
			iPerformUpdate = true;
			FileInspector.this.changeAction(2, FileInspector.this.iDisableConsoleUpdate);
		}
	}

	private class CharCount extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public CharCount() {
			super("Character count");
		}

		public void actionPerformed(ActionEvent e) {
			Document tDoc = FileInspector.this.iConsole.getDocument();
			JOptionPane.showMessageDialog(
				null,
				"Number of characters shown: " + String.valueOf(tDoc.getLength()),
				"FileInspector",
				JOptionPane.ERROR_MESSAGE);
		}
	}

	private class DisableScrollUpdate extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public DisableScrollUpdate() {
			super("Disable autoscrolling");
		}

		public void actionPerformed(ActionEvent e) {
			FileInspector.this.iScrollToMax = false;
			changeAction(0, FileInspector.this.iEnableScrollUpdate);
		}
	}

	private class EnableScrollUpdate extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public EnableScrollUpdate() {
			super("Enable autoscrolling");
		}

		public void actionPerformed(ActionEvent e) {
			FileInspector.this.iScrollToMax = true;
			changeAction(0, FileInspector.this.iDisableScrollUpdate);
		}
	}

	private class MouseHandler extends MouseAdapter {
		//		private Cursor hand = new Cursor(Cursor.HAND_CURSOR);
		//		private Cursor def_curs = new Cursor(Cursor.DEFAULT_CURSOR);

		public void mouseEntered(MouseEvent e) {
			//e.getComponent().setCursor(hand);
		}

		public void mouseExited(MouseEvent e) {
			//e.getComponent().setCursor(def_curs);
		}

		public void mouseClicked(MouseEvent e) {
			//System.out.println("MouseClicked");
			int modifiers = e.getModifiers();

			if ((modifiers & InputEvent.BUTTON3_MASK) != 0) {
				//System.out.println("Rightclicked!");
				//OptionList.this.prj_menu.setLocation(e.getX(),e.getY());
				//OptionList.this.prj_menu.setVisible(true);
				iPopup.show((Component) e.getSource(), e.getX(), e.getY());
			}
		}
	}

	public void createMenus(ColumnLayout tLayout) {
		JMenuBar tMenuBar = new JMenuBar();
		tMenuBar.setMinimumSize(new Dimension(25,25));
		tMenuBar.setMaximumSize(new Dimension(25,30));
		//tLayout.getGridConstraints().weighty = 0.0;
		//tLayout.getGridConstraints().weightx = 0.0;
		tLayout.getGridConstraints().fill = GridBagConstraints.NONE;
		tLayout.getGridConstraints().anchor = GridBagConstraints.NORTHWEST;
		tLayout.addComponent(tMenuBar, 0, 0);
		//setJMenuBar(tMenuBar);
		//Arkiv menyn
		JMenu tSettings = new JMenu("Settings");
		tSettings.setMnemonic('s');
		//Action settings = new SettingsAction();
		Action tAddDir = new SetDirectoryAction();
		
		//Action tSetNumChars = new SetNumberOfCharsAction();
		Action tSetRegExp = new SetRegExpFilterAction();
		Action tSetLineRegExp = new SetLineFilterRegExpAction();
		Action tExit = new ExitAction();
		tSettings.add(tAddDir);
		tSettings.add(tSetRegExp);
		tSettings.add(tSetLineRegExp);
		
		//tFile.add(tSetNumChars);
		
		//tFile.add(save);
		//tFile.add(settings);
		tSettings.add(tExit);
		tMenuBar.add(tSettings);
	}

	private void manageStyles(JTextPane aTextPane) {
		Style tDef = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		Style tBold = aTextPane.addStyle("bold", tDef);
		StyleConstants.setBold(tBold, true);
		StyleConstants.setForeground(tBold, new Color(164,164,255));

		Style tRed = aTextPane.addStyle("red", tDef);
		StyleConstants.setForeground(tRed, Color.RED);

		Style tDebug = aTextPane.addStyle("debug", tDef);
		StyleConstants.setForeground(tDebug, Color.darkGray);
		StyleConstants.setItalic(tDebug, true);

		Style tVerbose = aTextPane.addStyle("verbose", tDef);
		StyleConstants.setForeground(tVerbose, new Color(206, 140, 17));
		//StyleConstants.setBackground(tVerbose, Color.lightGray);
		StyleConstants.setItalic(tVerbose, true);
	}

	public void decorateExceptions() {
		StyledDocument tDoc = iConsole.getStyledDocument();
		String tDocStr = null;
		try {
			tDocStr = tDoc.getText(0, tDoc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		if (tDocStr != null && tDocStr.length() > 0) {
			int tDocLength = tDocStr.length();
			int tOffSet = 0;
			while (tOffSet < tDocLength && tOffSet > -1) {
				tOffSet = tDocStr.indexOf("Exception", tOffSet);
				if (tOffSet < 0) {
					return;
				}

				/* move backward to package start */
				int tStartIndex = tOffSet;
				while (tStartIndex > 0) {
					tStartIndex--;
					if (tStartIndex < 0) {
						tStartIndex++;
						break;
					}
					if (isDelimiter(String.valueOf(tDocStr.charAt(tStartIndex)))) {
						tStartIndex++;
						break;
					}
				}

				/* move forward to exception end */
				while (tOffSet <= tDocLength) {
					tOffSet++;
					if (tOffSet >= tDocLength) {
						break;
					}
					if (isDelimiter(String.valueOf(tDocStr.charAt(tOffSet)))) {
						break;
					}
				}
				tDoc.setCharacterAttributes(tStartIndex, tOffSet - tStartIndex, tDoc.getStyle("red"), true);
			}
		}

	}

	protected boolean isDelimiter(String aCharacter) {
		String tDelimiters = "\n\t,:{}()[]+-%<=>!&|^~*";

		if (Character.isWhitespace(aCharacter.charAt(0)) || tDelimiters.indexOf(aCharacter) != -1)
			return true;
		else
			return false;
	}

	private class SetDirectoryAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SetDirectoryAction() {
			super("Set directory");
		}

		public void actionPerformed(ActionEvent e) {
			
			JFileChooser tDirChooser = new JFileChooser();
			File tFile = null;
			if (iDirectory != null && iDirectory.length() > 0) {
				tFile = new File(iDirectory);
				tDirChooser.setCurrentDirectory(tFile);
			}
			
			tDirChooser.setDialogTitle("Set directory to be monitored");
			tDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			//AlgotFileFilter flt = new AlgotFileFilter("alg", "Algot file");
			//tDirChooser.setFileFilter(flt);
			if (tDirChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				if (FileInspector.this.iInspectionPerformer != null){
					FileInspector.this.iInspectionPerformer.setRunEnabled(false);					
				}
				String tDirectory = null;
				try {
					tDirectory = tDirChooser.getSelectedFile().getCanonicalPath().toString();
					//FileInspector.this.setDirectory(tDirectory);
					PersistenceManager.getInstance().getSettings().setCurrentFileDirectory(tDirectory);
					
					/*
					 * nulling out the monitoredfilelist -> a new file list will be
					 * created
					 */
					//iMonitoredFileList = null;
					FileInspector.this.startMonitor();
					//FileInspector.this.init(tDirectory);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				//				int response =
				//					JOptionPane.showConfirmDialog(
				//						null,
				//						s);
			}
		}
	}

	private class SetRegExpFilterAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SetRegExpFilterAction() {
			super("Set RegExp filter");
		}

		public void actionPerformed(ActionEvent e) {
			FileInspector.this.stopMonitor();
			FileInspector.this.setRegExpFilter();
			FileInspector.this.startMonitor();
		}
	}
	
	private class SetLineFilterRegExpAction extends AbstractAction {		
		private static final long serialVersionUID = 1L;

		public SetLineFilterRegExpAction() {
			super("Set Line Filter RegExp");
		}

		public void actionPerformed(ActionEvent e) {
			FileInspector.this.stopMonitor();
			FileInspector.this.setLineFilterRegExpr();
			FileInspector.this.startMonitor();
		}
	}
	
	
	
	private class SetNumberOfCharsAction extends AbstractAction {		
		private static final long serialVersionUID = 1L;

		public SetNumberOfCharsAction() {
			super("Set max number of chars");
		}

		public void actionPerformed(ActionEvent e) {
			FileInspector.this.setNumOfChars();
		}
	}

	private class ExitAction extends AbstractAction {
		private static final long serialVersionUID = -6741555456954838675L;

		public ExitAction() {
			super("Exit");
		}

		public void actionPerformed(ActionEvent e) {
			FileInspector.this.performClose();
		}

	}

	private void performClose(){
		int response = JOptionPane.showConfirmDialog(FileInspector.this, "Wish to close?");
		if (response == 0) {
			PersistenceManager.getInstance().persist();
			System.exit(0);
		}
	}
	
	private InspectionPerformer iInspectionPerformer;
	public void startMonitor() {
		iInspectionPerformer = new InspectionPerformer();
		iInspectionPerformer.setConsole(this);
		iInspectionPerformer.setFileMatchRegExp(iFileFilterRegExp);
		iInspectionPerformer.setLineFilterRegExp(iLineFilterRegExpString);
		iInspectionPerformer.setMonitorFrequency(iMonitorFrequency);
		iInspectionPerformer.init(iDirectory);		
		iInspectionPerformer.start();		
	}

	public void stopMonitor() {
		if (iInspectionPerformer != null){
			iInspectionPerformer.setRunEnabled(false);			
		}
	}




	public Sql getSql() {
		return iSql;
	}

	public void manageDocLength() {
		Document tDoc = iConsole.getDocument();
		int tExtraBuffSize = 50000;
		if (tDoc.getLength() > iMaxNumberOfCharsShown) {
			int tDocLen = tDoc.getLength();
			try {
				if (tDocLen > tExtraBuffSize) {
					tDoc.remove(0, tDocLen - (iMaxNumberOfCharsShown + tExtraBuffSize));
				} else {
					tDoc.remove(0, tDocLen - (iMaxNumberOfCharsShown));
				}
			} catch (BadLocationException e) {
				System.out.println("BadLocationException occured.");
			}
		}
	}

	public void print(String aString) {
		if (iPerformUpdate && Utils.isNotEmpty(aString)) {
			try {
				Document tDoc = iConsole.getDocument();
				tDoc.insertString(tDoc.getLength(), aString, null);
				iConsoleScrollBarTimer.start();
			} catch (BadLocationException e) {
				System.out.println("BadLocationException occured.");
			}
		}
	}

	public void println(String aMessage) {
		if (iPerformUpdate && Utils.containsSignificantInfo(aMessage)) {
			try {
				Document tDoc = iConsole.getDocument();
				tDoc.insertString(tDoc.getLength(), aMessage + '\n', null);				
				iConsoleScrollBarTimer.start();
			} catch (BadLocationException e) {
				System.out.println("BadLocationException occured.");
			}
		}
	}

	public void println(String aMessage, boolean aIsBold) {
		if (iPerformUpdate && Utils.containsSignificantInfo(aMessage)) {
			try {
				Document tDoc = iConsole.getDocument();
				if (aIsBold) {
					tDoc.insertString(tDoc.getLength(), aMessage + '\n', iConsole.getStyle("bold"));
				} else {
					tDoc.insertString(tDoc.getLength(), aMessage + '\n', null);
				}
				iConsoleScrollBarTimer.start();
			} catch (BadLocationException e) {
				System.out.println("BadLocationException occured.");
			}
		}
	}

	public void printlnDebug(String aMessage) {
		if (iPerformUpdate && Utils.containsSignificantInfo(aMessage)) {
			if (cDEBUG) {
				try {
					Document tDoc = iConsole.getDocument();
					tDoc.insertString(tDoc.getLength(), aMessage + '\n', iConsole.getStyle("debug"));
					iConsoleScrollBarTimer.start();
				} catch (BadLocationException e) {
					System.out.println("BadLocationException occured.");
				}
			}
		}
	}

	public void printlnVerbose(String aMessage) {
		if (iPerformUpdate && Utils.containsSignificantInfo(aMessage)) {
			if (iVerbose) {
				try {
					Document tDoc = iConsole.getDocument();
					tDoc.insertString(tDoc.getLength(), aMessage + '\n', iConsole.getStyle("verbose"));
					iConsoleScrollBarTimer.start();
				} catch (BadLocationException e) {
					System.out.println("BadLocationException occured.");
				}
			}
		}
	}

	private class MoveConsoleToLastRow implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			FileInspector.this.moveScrollBarToMax();
		}
	}

	
	public void moveScrollBarToMax() {
		if (iScrollToMax) {
			int tMax = iConsoleScrollPane.getVerticalScrollBar().getMaximum();
			//int tCurrentScrollMax = iConsoleScrollPane.getVerticalScrollBar().getValue();
			iConsoleScrollPane.getVerticalScrollBar().setValue(tMax);
			//		try {
			//			for (int i = tCurrentScrollMax; i < 100000; i++) {
			//				iConsoleScrollPane.getVerticalScrollBar().setValue(i);
			//			}
			//		} catch (Exception e) {
			//			
			//		}

			//		for (int i = tCurrentScrollMax; i < tMax; i++) {
			//			iConsoleScrollPane.getVerticalScrollBar().setValue(i);
			//			tMax = iConsoleScrollPane.getVerticalScrollBar().getMaximum();
			//			// try {
			//			// Thread.sleep(1);
			//			// } catch (InterruptedException e) {
			//			// // handle exception
			//			// }
			//		}

			if (iTimerVisitIndex > 2) {
				iTimerVisitIndex = 0;
				iConsoleScrollBarTimer.stop();
			} else {
				iTimerVisitIndex++;
			}

		}
	}

	//public void findText(JEditorPane aSender) {
	public void findText() {

		if (iDlgSearch == null) {
			iDlgSearch = new DlgSearch(this, "Search", true);
		}
		iDlgSearch.setVisible(true);
		iDlgSearch.focusSearchTextField();
		String tSearchText = "";
		if (iDlgSearch.getIsConfirmed()) {
			tSearchText = iDlgSearch.getReturnText();
		} else {
			return;
		}
		if (tSearchText.trim().length() >0){
			highlight(iConsole, tSearchText);			
		}

//		int tCaretPos = 0;
//		//			if (aSender == iConsole) {
//		//				//iSearchText = iInputEditor.getSelectedText().toLowerCase();
//		//				tCaretPos = iConsole.getCaretPosition();
//		//			} else {
//		if (iConsole.getSelectedText() != null) {
//			iSearchText = iConsole.getSelectedText().toLowerCase();
//			tCaretPos = iConsole.getCaretPosition();
//		} else {
//			tCaretPos = iConsole.getCaretPosition() + 1;
//		}
//
//		//			}
//		if (!iSearchText.equals("")) {
//			Document tDoc = iConsole.getDocument();
//			String tDocStr = null;
//			try {
//				tDocStr = tDoc.getText(0, tDoc.getLength()).toLowerCase();
//			} catch (BadLocationException e) {
//			}
//			if (tDocStr != null) {
//				int tPos = tDocStr.indexOf(iSearchText.toLowerCase(), tCaretPos);
//				if (tPos > -1) {
//					iConsole.setCaretPosition(tPos);
//					iConsole.requestFocus();
//				} else {
//					//tPos=-1, wrap around
//					tPos = tDocStr.indexOf(iSearchText.toLowerCase(), 0);
//					if (tPos > -1) {
//						iConsole.setCaretPosition(tPos);
//						iConsole.requestFocus();
//					}
//				}
//			}
//		}
	}

	// Highlight the occurrences of the word "public"
	//highlight(textComp, "public");

	// Creates highlights around all occurrences of pattern in textComp
	public void highlight(JTextComponent textComp, String pattern) {
	    // First remove all old highlights
	    removeHighlights(textComp);

	try {
	    Highlighter hilite = textComp.getHighlighter();
	    Document doc = textComp.getDocument();
	    String text = doc.getText(0, doc.getLength());
	    int pos = 0;

	    // Search for pattern
	    // see I have updated now its not case sensitive 
	    while ((pos = text.toUpperCase().indexOf(pattern.toUpperCase(), pos)) >= 0) {
	        // Create highlighter using private painter and apply around pattern
	        hilite.addHighlight(pos, pos+pattern.length(), myHighlightPainter);
	        pos += pattern.length();
	    }
		} catch (BadLocationException e) {
			//aldskfj
		}
	}
	
	
	
	
	private class ConsoleKeyListener implements KeyListener {

		public void keyPressed(KeyEvent aEvent) {
			//DBCommunicator.this.println(String.valueOf(aEvent.getKeyCode()));
			if (aEvent.getKeyCode() == KeyEvent.VK_INSERT) {
				String tSelectedText = new String(iConsole.getSelectedText().trim());
				try {
					FileInspector.this.iConsole.getDocument().insertString(iConsole.getCaretPosition(), tSelectedText, null);
					FileInspector.this.iConsole.requestFocus();
				} catch (BadLocationException e) {
					FileInspector.this.println("Bad position in the input editor");
				}
			}

			if (aEvent.isControlDown() && aEvent.getKeyCode() == 70) {
				//FileInspector.this.findText(FileInspector.this.iConsole);
				FileInspector.this.findText();
			}
			//
			//			if (aEvent.isControlDown() && aEvent.getKeyCode() == 77) {
			//				FileInspector.this.toggleMaxMinEditors(FileInspector.this.iInputEditorScrollPane);
			//				FileInspector.this.iInputEditor.requestFocus();
			//			}
			//			if (aEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
			//				FileInspector.this.iPopUpWindow.setVisible(false);
			//				FileInspector.this.iInputEditor.requestFocus();
			//			}
		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		public void keyReleased(KeyEvent e) {
			//

		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		public void keyTyped(KeyEvent e) {
			//

		}
	}

	/**
	 * S�tter Directory
	 * @param String aDirectory
	 */
	public void setDirectory(String aDirectory) {
		if (Utils.isNotEmpty(aDirectory)){
			iDirectory = aDirectory;			
		}
	}

	public static void main(String[] args) {
		
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        	//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {		
			e.printStackTrace();
		} catch (InstantiationException e) {		
			e.printStackTrace();
		} catch (IllegalAccessException e) {		
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {		
			e.printStackTrace();
		}

		FileInspector tFileInspector = new FileInspector("FileInspector");

		if (args.length < 1) {
			//System.out.println("Set the dir to be monitored at the command line");
			//System.exit(0);
		} else {
			//tFileInspector.init(args[0]);
			tFileInspector.setDirectory(args[0]);
			if (args.length > 1){
				tFileInspector.setFileFilterRegExpString(args[1]);
			}
			tFileInspector.startMonitor();
		}

	}

	/* (non-Javadoc)
	 * @see fileinspect.ui.resource.Console#printErasableMessage()
	 */
	public void printReversibleMessage(String aMessage) {
		//emtpy
	}
	
	/**
		 * @return
		 */
		public String getFileMatchRegExp() {
			return iFileFilterRegExp;
		}

		/**
		 * @param aString
		 */
		public String getLineFilterRegExpString() {
			return iLineFilterRegExpString;
		}

		// Removes only our private highlights
		public void removeHighlights(JTextComponent textComp) {
		    Highlighter hilite = textComp.getHighlighter();
		    Highlighter.Highlight[] hilites = hilite.getHighlights();
		    for (int i=0; i<hilites.length; i++) {
		    	if (hilites[i].getPainter() instanceof MyHighlightPainter) {
		    		hilite.removeHighlight(hilites[i]);
		    	}
		    }
		}

		// An instance of the private subclass of the default highlight painter
		Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.yellow);

		// A private subclass of the default highlight painter
		class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
		    public MyHighlightPainter(Color color) {
		        super(color);
		    }
		}

		@Override
		public void setLineFilterRegExpString(String aLineFilterRegExpString){
			iLineFilterRegExpString = aLineFilterRegExpString;
		}
		
		@Override
		public void setFileFilterRegExpString(String aLineFilterRegExpString){
			iFileFilterRegExp = aLineFilterRegExpString;
		}
		
		@Override
		public void setFileDirectory(String aFileDirectory){
			if (Utils.isNotEmpty(aFileDirectory)){
				iDirectory = aFileDirectory;				
				setConsoleTitle("FileInspector " + iDirectory);
			}
		}	
		
		private void setRegExpFilter(){
			DlgRegExpFilter tDlgRegExpFilter = new DlgRegExpFilter(this, "File Name Regular Expression Filter", true, String.valueOf(iFileFilterRegExp));
			String tCurrentText = FileInspector.this.getFileMatchRegExp();
			tDlgRegExpFilter.setExpression(tCurrentText);
			tDlgRegExpFilter.setVisible(true);
			tDlgRegExpFilter.focusInputTextField();
			if (tDlgRegExpFilter.getIsConfirmed()) {
				try {
					//FileInspector.this.setFileMatchRegExp(tDlgRegExpFilter.getReturnText());
					PersistenceManager.getInstance().getSettings().setCurrentFileNameFilterRegExp(tDlgRegExpFilter.getReturnText());
					iRegExpLabel.setText(CURRENT_REGEXP_FILTER + tDlgRegExpFilter.getReturnText());
				} catch (Exception e) {
					// just ignore ...
				}
			} else {
				return;
			}		
		}
		
		private void setLineFilterRegExpr(){
			DlgLineFilterRegExp tDlgLineFilterRegExp = new DlgLineFilterRegExp(this, "Line Regular Expression Filter", true);
			String tCurrentText = FileInspector.this.getLineFilterRegExpString();
			tDlgLineFilterRegExp.setExpression(tCurrentText);
			tDlgLineFilterRegExp.setVisible(true);
			tDlgLineFilterRegExp.focusInputTextField();
			if (tDlgLineFilterRegExp.getIsConfirmed()) {
				try {
					PersistenceManager.getInstance().getSettings().setCurrentLineFilterRegExp(tDlgLineFilterRegExp.getReturnText());
					//FileInspector.this.setLineFilterRegExpString(tDlgLineFilterRegExp.getReturnText());
					
					iRegExpLabel.setText(CURRENT_LINE_REGEXP_FILTER + tDlgLineFilterRegExp.getReturnText());
				} catch (Exception e) {
					// just ignore ...
				}
			} else {
				return;
			}		
		}				
}
