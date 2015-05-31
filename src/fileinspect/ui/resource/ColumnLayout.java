package fileinspect.ui.resource;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

public class ColumnLayout {
	private GridBagConstraints c_;
	private JPanel out_panel;
	public ColumnLayout() {
		setDefaultConstraints();
	}

	public void resetDefaultConstraints() {
		setDefaultConstraints();
	}
	
	private void setDefaultConstraints() {

		c_ = new GridBagConstraints();
		/*left alignment*/
		c_.anchor = GridBagConstraints.WEST;
		/*column number */
		c_.gridx = 0; //first column
		/* row number */
		c_.gridy = 0; //first row			
		/* exterior padding, negative values->larger than container 
		 *Insets(int top, int left, int bottom, int right) */
		c_.insets = new Insets(2, 5, 0, 0);
		/*interior padding */
		c_.ipadx = 0; //increases component width by value*2 pixels
		c_.ipady = 0; //increases component height by value*2 pixels

		/* treatment of extra space */
		/* fractions of extra horizontal space to occupy*/
		c_.weightx = 0.5;
		/* fractions of extra vertical space to occupy*/
		c_.weighty = 0.5;

		/*components that span over multiple cells*/
		c_.gridwidth = 1; //span across 1 column;
		c_.gridheight = 1; //span across 1 row;

		c_.fill = GridBagConstraints.BOTH;  
	}

	public void setColumnSpanNumber(int columns) {
		c_.gridwidth = columns;
	}

	public void setRowSpanNumber(int rows) {
		c_.gridheight = rows;
	}

	public void addComponent(Component comp, int row, int column) {
		if (out_panel == null) {
			out_panel = new JPanel();
			out_panel.setLayout(new GridBagLayout());
		}
		c_.gridy = row;
		c_.gridx = column;
		out_panel.add(comp, c_);
	}
	public JPanel getPanel() {
		return out_panel;
	}
	/**
	 * Can be used for further customizing
	 * @return GridBagConstraints
	 */
	public GridBagConstraints getGridConstraints() {
		if (c_ == null) {
			c_ = new GridBagConstraints();
		}
		return c_;
	}

} //ColumnLayout
