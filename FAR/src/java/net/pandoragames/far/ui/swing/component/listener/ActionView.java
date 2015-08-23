package net.pandoragames.far.ui.swing.component.listener;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JTable;

import net.pandoragames.far.ui.model.FARForm;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.model.Resetable;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.OperationTabPane;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.dialog.FileView;

import org.apache.commons.logging.LogFactory;

/**
 * Opens the "viewer" that visualises the matches of a (regex) search pattern.
 * The pattern is taken from the currently active form, FindForm or ReplaceForm.
 *
 * @author Olivier Wehner
 * <!--
 *  FAR - Find And Replace
 *  Copyright (C) 2009,  Olivier Wehner

 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  -->
 */
public class ActionView extends AbstractAction implements TabListener, Resetable { // MouseListener

	private JFrame mainFrame;
	private SwingConfig config;
	private TargetFile selectedFile;
	private FARForm form;
	private ReplaceForm replaceForm;
	private FindForm findForm;


	public ActionView(ComponentRepository componentRepository, SwingConfig swingConfig) {
		super(swingConfig.getLocalizer().localize( "label.view" ) );
		putValue( Action.MNEMONIC_KEY, swingConfig.getAccessKey("popup.label.view") );
		config = swingConfig;
		mainFrame = componentRepository.getRootWindow();
		findForm = componentRepository.getFindForm();
		replaceForm = componentRepository.getReplaceForm();
		form = findForm;
		componentRepository.getTabPane().addTabListener(TabListener.ANYTAB, this);
		componentRepository.getResetDispatcher().addResetable( this );
		super.setEnabled( false );
	}
	
	/**
	 * Overwrites the super implementation to keep the component state 
	 * aligned with the state of the environment. This method may be called
	 * to refresh the component state.
	 */
	public void setEnabled( boolean enabled ) {
		refresh();
	}
	
	/**
	 * Sets the File that should be displayd by this Action.
	 * @param file to be displayed or null for none
	 */
	public void setSelectedFile(TargetFile file ) {
		selectedFile = file;
		refresh();
	}
	
	/**
	 * Refreshes the enabled/disabled status.
	 */
	public void refresh() {
		super.setEnabled( isReady() );
	}
	
	/**
	 * Disables this action. Called when the application is reset to
	 * its original state.
	 */
	public void reset() {
		super.setEnabled( false );
		selectedFile = null;
	}


	/**
	 * Opens a {@link net.pandoragames.far.ui.swing.dialog.FileView FileView}
	 * dialog if the current form is not null.
	 */
	public void actionPerformed(ActionEvent e) {
		if( form != null ) {
			FileView fileView = null;
			if( form instanceof ReplaceForm ) {
				fileView = new FileView( mainFrame, 
						  selectedFile,
						  (ReplaceForm) form,
						  config,
						  false);				
			} else {
				fileView = new FileView( mainFrame, 
						  selectedFile,
						  (FindForm) form,
						  config);								
			}
			fileView.pack();
			fileView.setVisible( true );
		}
	}
	
	/**
	 * Returns true if this Action is ready to show a file with findings.
	 * @return true if findings can be shown
	 */
	public boolean isReady() {
		return form != null 
				&& selectedFile != null 
				&& selectedFile.getFile().exists()
				&& form.getSearchStringContent().length() > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public void tabSelected(TabEvent event) {
		if( event.getTabIndex() == 0 ) {
			form = findForm;
		} else if( event.getTabIndex() == 1 ) {
			form = replaceForm;
		}
		refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	public void tabUnselected(TabEvent event) {
		form = null;
		super.setEnabled(false);
	}
}
