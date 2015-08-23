package net.pandoragames.far.ui.swing.component.listener;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JTable;

import net.pandoragames.far.ui.model.ReplaceForm;
import net.pandoragames.far.ui.model.Resetable;
import net.pandoragames.far.ui.model.TargetFile;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.dialog.FileView;

import org.apache.commons.logging.LogFactory;

public class ActionPreview extends AbstractAction implements TabListener, Resetable {

	private JFrame mainFrame;
	private SwingConfig config;
	private TargetFile selectedFile;
	private ReplaceForm replaceForm;
	private boolean replaceTabActive = false;

	public ActionPreview(ComponentRepository componentRepository, SwingConfig swingConfig) {
		super(swingConfig.getLocalizer().localize( "label.preview" ) );
		putValue( Action.MNEMONIC_KEY, swingConfig.getAccessKey("popup.label.preview") );
		config = swingConfig;
		mainFrame = componentRepository.getRootWindow();
		replaceForm = componentRepository.getReplaceForm();
		componentRepository.getTabPane().addTabListener(1, this);
		componentRepository.getResetDispatcher().addResetable( this );
		super.setEnabled( false );	}
	
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
		if( replaceTabActive ) {
			FileView fileView = new FileView( mainFrame, 
						  selectedFile,
						  replaceForm,
						  config,
						  true);				
			fileView.pack();
			fileView.setVisible( true );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void tabSelected(TabEvent event) {
		replaceTabActive = true;
		refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	public void tabUnselected(TabEvent event) {
		replaceTabActive = false;
		super.setEnabled(false);
	}

	/**
	 * Returns true if this Action is ready to show a preview.
	 * @return true if preview can be shown
	 */
	public boolean isReady() {
		return replaceTabActive 
				&& selectedFile != null 
				&& selectedFile.getFile().exists()
				&& replaceForm.getSearchStringContent().length() > 0;
	}
}
