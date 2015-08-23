package net.pandoragames.far.ui.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.dialog.SubSelectByNameDialog;

public class SelectByNameListener implements ActionListener {
	private SwingConfig config;
	private JFrame jframe;
	private FileSetTableModel tableModel;
	
	public SelectByNameListener(JFrame owner, FileSetTableModel model, SwingConfig swc) {
		config = swc;
		jframe = owner;
		tableModel = model;
	}
	public void actionPerformed(ActionEvent e) {
		SubSelectByNameDialog dialog = new SubSelectByNameDialog(jframe, tableModel, config);
		dialog.pack();
		dialog.show();
	}

}
