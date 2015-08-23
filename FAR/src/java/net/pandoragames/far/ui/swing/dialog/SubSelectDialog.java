package net.pandoragames.far.ui.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.pandoragames.far.ui.UIBean;
import net.pandoragames.far.ui.UIFace;
import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.OperationType;
import net.pandoragames.far.ui.model.ProgressListener;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.ContentSearchPanel;
import net.pandoragames.far.ui.swing.component.FileSetTableModel;
import net.pandoragames.far.ui.swing.component.listener.ProgressBarUpdater;

public class SubSelectDialog extends SubWindow implements ProgressListener {

	private FileSetTableModel fileList;
	private FindForm findForm;
	private UIFace fileFilter;
	
	public SubSelectDialog(JFrame owner, FileSetTableModel model, ComponentRepository componentRepository, SwingConfig config) {
		super(owner, config.getLocalizer().localize("label.select-subset"), true );
		fileList = model;
		findForm = new FindForm();
		fileFilter = new UIBean( config, componentRepository.getMessageBox() );
		fileFilter.addProgressListener( this );
		init( componentRepository, config );
	}
	
	private void init(ComponentRepository componentRepository, SwingConfig config) {
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setPreferredSize( new Dimension(475, 294));
		setMinimumSize( new Dimension(475, 150) );
		getContentPane().setLayout( new BorderLayout() );
		
		ContentSearchPanel searchPanel = new ContentSearchPanel( config.getLocalizer().localize("label.content-pattern"), 
													findForm,
													config,
													componentRepository);
		JCheckBox inversionFlag = new JCheckBox( config.getLocalizer().localize("label.exclude-matches"));
		inversionFlag.setAlignmentX( Component.LEFT_ALIGNMENT );
		inversionFlag.setSelected( findForm.isInvertContentFilter() );
		inversionFlag.addItemListener( new ItemListener() {	
				public void itemStateChanged(ItemEvent event) {
					findForm.setInvertContentFilter( (ItemEvent.SELECTED == event.getStateChange()) );
				}
			});
		searchPanel.addFlag( inversionFlag );
		searchPanel.setBorder( BorderFactory.createEmptyBorder(0, SwingConfig.PADDING, 0, SwingConfig.PADDING));
 		getContentPane().add( searchPanel, BorderLayout.CENTER );
 		
 		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
 		buttonPanel.setBorder( BorderFactory.createEmptyBorder(0, SwingConfig.PADDING, SwingConfig.PADDING, SwingConfig.PADDING));
		JProgressBar progressBar = new JProgressBar();
		progressBar.setEnabled( false );
		progressBar.setMaximumSize( new Dimension( 100, 20 ));
		progressBar.setBorder( BorderFactory.createEmptyBorder(1, SwingConfig.PADDING, 2, SwingConfig.PADDING));
		fileFilter.addProgressListener( new ProgressBarUpdater( progressBar ) );		
		JPanel progressBarPanel = new JPanel();
		progressBarPanel.add( progressBar );
		buttonPanel.add( progressBarPanel );
		buttonPanel.add( Box.createHorizontalStrut(100));

 		// select button
 		JButton selectButton = new JButton( config.getLocalizer().localize("button.filter") );
 		selectButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// fileFilter.filter(fileList.listRows(), findForm, false);
				FilterCommand cmd = new FilterCommand();
				cmd.setDaemon( true );
				cmd.start();
			}						
		});
 		buttonPanel.add( selectButton );
 		
 		// close button
 		JButton closeButton = new JButton( config.getLocalizer().localize("button.cancel") );
 		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				fileFilter.abort();
				SubSelectDialog.this.dispose();
			}						
		});
 		buttonPanel.add( closeButton );
 		//    buttonPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
 		getContentPane().add( buttonPanel, BorderLayout.SOUTH );
 		
 		placeOnScreen( config.getScreenCenter() );
	}

	public void operationStarted( OperationType type ) {}
	
	public void operationProgressed( int count, int total,  OperationType type  ) {}
	
	/**
	 * Notified when the operation has terminated.
	 * @param type type of operation that has terminated
	 */
	public void operationTerminated( OperationType type ) {
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				fileList.notifyUpdate();				
			}
		});
		SubSelectDialog.this.dispose();
	}
	
	/**
	 * Notified when the operation has been aborted.
	 * @param type type of operation that has been aborted
	 */
	public void operationAborted( OperationType type ) {
		operationTerminated( type );
	}
	
	class FilterCommand extends Thread {
		
		public void run() {
			fileFilter.filter(fileList.listRows(), findForm, false);
		}
	}

}


