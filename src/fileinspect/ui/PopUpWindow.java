package fileinspect.ui;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;

public class PopUpWindow extends JWindow {
	private static final long serialVersionUID = 1L;
	private JList iList;
	//private Console iConsole;
	private String iReturnValue;
	private String[] iData;
	private JFrame iOwner;

	public PopUpWindow(JFrame aOwner) {
		super(aOwner);
		iOwner = aOwner;
		//setLocationRelativeTo(((DBCommunicator)aOwner).iInputEditor);
		iList = new JList();
		iList.setBackground(new Color(228, 237, 167));
		//iList.setFont(FileInspectorFont.getInstance().getPopUpWindowFont());
		iList.setOpaque(false);
		iList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane tPane = new JScrollPane();
		tPane.getViewport().add(iList);
		getContentPane().add(tPane);
		iList.addKeyListener(new PopUpKeyListener());
		iList.addMouseListener(new PopUpMouseListener());
		iList.requestFocus();
	}

	public void setData(String[] aData) {
		iList.setListData(aData);
		pack();
	}
	private class PopUpMouseListener extends MouseAdapter {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				PopUpWindow.this.iReturnValue = String.valueOf(PopUpWindow.this.iList.getSelectedValue());
				//FileInspector tOwner = (FileInspector) PopUpWindow.this.iOwner;
				//        Document tDoc = tOwner.iInputEditor.getDocument();
				//        int tCurPos = tOwner.iInputEditor.getCaretPosition();
				//        try {
				//           tOwner.iInputEditor
				//              .getDocument().insertString(tCurPos,
				//                  PopUpWindow.this.iReturnValue, null);
				//        } catch (BadLocationException exc) {
				//        }
			}
			//tOwner.iInputEditor.requestFocus();
		}

	}
	private class PopUpKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent aEvent) {

			if (aEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
				PopUpWindow.this.setVisible(false);
			}

			if (aEvent.getKeyCode() == 10) {
				//PopUpWindow.this.iReturnValue =
				// String.valueOf(PopUpWindow.this.iList.getSelectedValue());
				Object[] tValues = PopUpWindow.this.iList.getSelectedValues();
				StringBuffer tStringValue = new StringBuffer();

				for (int i = 0; i < tValues.length; i++) {
					if (i == 0) {
						//tStringValue.append('\n');
					}
					tStringValue.append(String.valueOf(tValues[i]));
					if (i < tValues.length - 1) {
						tStringValue.append(", ");
						//tStringValue.append('\n');
					}
				}
				PopUpWindow.this.iReturnValue = tStringValue.toString();
				PopUpWindow.this.setVisible(false);
				//FileInspector tOwner = (FileInspector) PopUpWindow.this.iOwner;
				//        Document tDoc = tOwner.iInputEditor.getDocument();
				//        int tCurPos = tOwner.iInputEditor.getCaretPosition();
				//        try {
				//          tOwner.iInputEditor
				//              .getDocument().insertString(tCurPos,
				//                  PopUpWindow.this.iReturnValue, null);
				//        } catch (BadLocationException e) {
				//        }
				//        tOwner.iInputEditor.requestFocus();
			}

			if (aEvent.isControlDown() && aEvent.getKeyCode() == 66) {
				// empty
			}

			if (aEvent.isControlDown() && aEvent.getKeyCode() == KeyEvent.VK_DELETE) {
				//empty
			}
		}
	}

}