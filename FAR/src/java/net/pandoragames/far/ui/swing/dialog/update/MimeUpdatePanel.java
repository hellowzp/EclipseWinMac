package net.pandoragames.far.ui.swing.dialog.update;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.pandoragames.far.ui.model.FileNamePattern;
import net.pandoragames.far.ui.model.MimeType;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.TargetFileListTableCellRenderer;
import net.pandoragames.far.ui.swing.component.listener.SaveFileNamePatternListener;

public class MimeUpdatePanel extends UpdateWizzardPanel {

	private Object[][] extension_mimeType_list;
	
	private Log logger = LogFactory.getLog(this.getClass());
	
	public MimeUpdatePanel(SwingConfig config, ComponentRepository repository) {
		super(config, repository);
	}

	@Override
	public void init(SwingConfig config, ComponentRepository repository) {

		makeTableContent( config );
		
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		setBorder( BorderFactory.createEmptyBorder( SwingConfig.PADDING, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING, 
				SwingConfig.PADDING ) );
		
		// some headspace
		add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
		
		// file name
		JLabel titleLabel = new JLabel( config.getLocalizer().localize("message.please-define-mime-type"));
		titleLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		add( titleLabel );

		MimeType[] mimmi = (MimeType[]) MimeType.MimeRegistry.listAll().toArray(new MimeType[0]);
		JComboBox mimeList = new JComboBox( mimmi );
		mimeList.setMinimumSize( new Dimension( SwingConfig.COMPONENT_WIDTH, config.getStandardComponentHight() ));
		MimeListCellRenderer renderer = new MimeListCellRenderer( mimmi );
		renderer.setMinimumSize( new Dimension( SwingConfig.COMPONENT_WIDTH, config.getStandardComponentHight() ));
		
		JTable extensionTable = new JTable( tableContent(), new String[]{ config.getLocalizer().localize("label.file-extension"), 
																									config.getLocalizer().localize("label.mime-type")});
		extensionTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		extensionTable.setColumnSelectionAllowed( false );
		extensionTable.getColumnModel().getColumn( 0 ).setPreferredWidth( 100 );
		extensionTable.getColumnModel().getColumn( 0 ).setMaxWidth( 150 );
		extensionTable.getColumnModel().getColumn( 1 ).setCellRenderer( renderer );
		extensionTable.getColumnModel().getColumn( 1 ).setCellEditor( new DefaultCellEditor( mimeList ) );
		
		extensionTable.setRowHeight(config.getStandardComponentHight() + extensionTable.getRowMargin());

	    JScrollPane scrollPane = new JScrollPane( extensionTable );
	    scrollPane.setAlignmentX( Component.LEFT_ALIGNMENT );
		add( scrollPane );
	}

	@Override
	public void finish() {
		for(Object[] row : tableContent() ) {
			logger.info( "Adding " + row[0] + " => " + row[1]);
			((MimeType) row[1]).addExtension( (String) row[0] );
		}
	}

	@Override
	public boolean isUserInteractionRequested() {
		return tableContent().length > 0;
	}
	
	private Object[][] tableContent() {
		return extension_mimeType_list;
	}
	
	private void makeTableContent(SwingConfig config) 
	{
		List<String> extensionList = new ArrayList<String>();
		for(FileNamePattern pattern : config.getFileNamePatternListModel().asList()) {
			if( ! pattern.isRegex() && pattern.getPattern().matches( SaveFileNamePatternListener.FNP_EXTENSIONLIST_PATTERN )) 
			{
				String[] extlist = pattern.getPattern().split(",");
				for(String ext : extlist) {
					String extension = ext.trim().substring(1);
					if( MimeType.MimeRegistry.findMimeType(extension) == null && ! extensionList.contains( extension ))
					{
						extensionList.add( extension );
					}
				}
			}
		}
		if( extensionList.size() == 0 ) {
			extension_mimeType_list = new Object[0][0];
		} else {
			extension_mimeType_list = new Object[extensionList.size()][2];
			MimeType defaultType = (MimeType) MimeType.getType("text/plain");
			for(int i = 0; i < extensionList.size(); i++ ) {
				extension_mimeType_list[i][0] = extensionList.get(i);
				extension_mimeType_list[i][1] = defaultType;
			}
		}
	}

class MimeListCellRenderer  extends JComboBox implements TableCellRenderer 
{
	public MimeListCellRenderer(MimeType[] values) {
		super( values );
	}
	/**
	 * {@inheritDoc}
	 */
	public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) 
	{
		setSelectedItem( value );
		return this;
	}
}

}
