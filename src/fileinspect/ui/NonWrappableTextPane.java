/*
 * Created on 2005-apr-18
 *
 */
package fileinspect.ui;

import java.awt.Component;

import javax.swing.JTextPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.StyledDocument;

/**
 * Sub class of JTextPane that does not wrap the text.
 */
public class NonWrappableTextPane extends JTextPane {
	private static final long serialVersionUID = 1L;


/**
   * 
   */
  public NonWrappableTextPane() {
    super();
  }

  /**
   * @param arg0
   */
  public NonWrappableTextPane(StyledDocument arg0) {
    super(arg0);
  }
  

  public boolean getScrollableTracksViewportWidth() {
    Component parent = getParent();
    ComponentUI ui = getUI();
    return parent != null ? (ui.getPreferredSize(this).width <= parent
        .getSize().width) : true;

  }


  public static void main(String[] args) {
	  // empty
  }
}
