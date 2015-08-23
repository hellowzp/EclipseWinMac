package net.pandoragames.far.ui.swing.dialog.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.TwoComponentsPanel;

public class CharsetConfigPanel extends ConfigPanel {

	private DefaultListModel charsetList;
	private Charset newDefaultCS;
	private JList supplyList;
	private JList availableList;
		
	public CharsetConfigPanel(SwingConfig swingConfig, ComponentRepository repository, MessageBox messageBox) {
		super(messageBox, repository, swingConfig);
		CharsetLoader csloader = new CharsetLoader();
		Thread loadingThread = new Thread( csloader );
		loadingThread.start();
	}

	@Override
	protected void init(ComponentRepository repositor, SwingConfig swingConfig) {
		setLayout(new BorderLayout());
		
		charsetList = new DefaultListModel();
		for(Charset charset : swingConfig.getCharsetList()) {
			charsetList.addElement(charset);
		}
		
		// default character set
		JPanel defaultCSPanel = new JPanel();
		// defaultCSPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		defaultCSPanel.setLayout( new BoxLayout( defaultCSPanel, BoxLayout.Y_AXIS ) );
		
		JLabel labelCharset = new JLabel( swingConfig.getLocalizer().localize("label.default-characterset") );
		labelCharset.setAlignmentX( Component.LEFT_ALIGNMENT );
		defaultCSPanel.add( labelCharset );
		
		JComboBox listCharset = new JComboBox( new CharsetListModelComboBoxWrapper(charsetList) );
		listCharset.setAlignmentX( Component.LEFT_ALIGNMENT );
		listCharset.setSelectedItem( swingConfig.getDefaultCharset() );
		listCharset.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, 
				swingConfig.getStandardComponentHight()) );
		listCharset.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 
										swingConfig.getStandardComponentHight()) );
		newDefaultCS = swingConfig.getDefaultCharset();
		listCharset.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
		        JComboBox cbox = (JComboBox) event.getSource();
		        newDefaultCS = (Charset) cbox.getSelectedItem();
		        availableList.repaint();
			}
		});
		defaultCSPanel.add( listCharset );

		add( defaultCSPanel, BorderLayout.NORTH );
		
		// list
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout( new BoxLayout( centerPanel, BoxLayout.Y_AXIS ) );

		JPanel supplyPanel = new JPanel();
		supplyPanel.setLayout( new BoxLayout( supplyPanel, BoxLayout.Y_AXIS ) );
		JLabel supplyLabel = new JLabel( localizer.localize( "label.charset-supported"));
		supplyLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		supplyPanel.add(supplyLabel);
		
		supplyList = setUpCSList(new DefaultListModel());
		supplyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		supplyList.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent eve) {
				JList list = (JList) eve.getSource();
				Charset selection = (Charset) list.getSelectedValue();
				if( selection != null ) {
					((DefaultListModel) availableList.getModel()).addElement(selection);
					int index = list.getSelectedIndex();
					list.getSelectionModel().clearSelection();
					((DefaultListModel) list.getModel()).remove(index);
				}
			}
		});
		supplyList.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR ));
		supplyList.setToolTipText( swingConfig.getLocalizer().localize("message.loading") );
		supplyList.setAlignmentX( Component.LEFT_ALIGNMENT );
		supplyPanel.add(addToScrollPane(supplyList));
		
		
		JPanel availablePanel = new JPanel();
		availablePanel.setLayout( new BoxLayout( availablePanel, BoxLayout.Y_AXIS ) );
		JLabel availableLabel = new JLabel( localizer.localize( "label.charset-available"));
		availableLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		availablePanel.add(availableLabel);

		availableList = setUpCSList(charsetList);
		availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availableList.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent eve) {
				JList list = (JList) eve.getSource();
				Charset selection = (Charset) list.getSelectedValue();
				if( selection != null ) {
					int index = list.getSelectedIndex();
					list.getSelectionModel().clearSelection();
					if( ! isCharsetDisabled(selection)) {
						((DefaultListModel) supplyList.getModel()).addElement(selection);
						((DefaultListModel) list.getModel()).remove(index);						
					}
				}
			}
		});
		availableList.setAlignmentX( Component.LEFT_ALIGNMENT );
		availableList.setCellRenderer( new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
				Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				Charset charset = (Charset) value;
				if( isCharsetDisabled(charset)) {
					comp.setForeground(Color.GRAY);
				} else {
					comp.setForeground(Color.BLACK);
				}
				return comp;
			}
		});
		availablePanel.add(addToScrollPane(availableList));

		JPanel leftright = new JPanel();
		leftright.setLayout(new FlowLayout(FlowLayout.CENTER));
		leftright.add(supplyPanel);
		leftright.add(Box.createHorizontalGlue());
		leftright.add(availablePanel);
		leftright.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		centerPanel.add( leftright );
		
		add( centerPanel, BorderLayout.CENTER);
	}
	
	private boolean isCharsetDisabled(Charset charset) {
		return ( "UTF-8".equalsIgnoreCase(charset.name()) 
				|| "US-ASCII".equalsIgnoreCase(charset.name()) 
				|| "X-CP437".equalsIgnoreCase(charset.name())
				|| newDefaultCS.name().equalsIgnoreCase(charset.name()));
	}
	
	private JList setUpCSList(DefaultListModel model) {
		JList cslist = new JList( model);
		cslist.setBackground(Color.WHITE);
		cslist.setBorder( BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		return cslist;
	}
	
	private JScrollPane addToScrollPane(JList list) {
		JScrollPane scroller = new JScrollPane(list);
		scroller.setMinimumSize(new Dimension(100,100));
		scroller.setPreferredSize(new Dimension(200,200));
		scroller.setMaximumSize(new Dimension(SwingConfig.COMPONENT_WIDTH_MAX,1000));
		return scroller;
	}

	@Override
	public void save(SwingConfig swingConfig) {
		swingConfig.setDefaultCharset( newDefaultCS );
		Set<Charset> fullSet = new HashSet<Charset>();
		for(int i = 0; i < availableList.getModel().getSize(); i++ ) {
			Charset charset = (Charset) availableList.getModel().getElementAt(i);
			if( ! swingConfig.getCharsetList().contains(charset)) {
				swingConfig.getCharsetList().add(charset);
			}
			fullSet.add(charset);
		}
		Set<Charset> toBeRemoved = new HashSet<Charset>();
		for(Charset charset : swingConfig.getCharsetList()) {
			if( ! fullSet.contains( charset )) toBeRemoved.add(charset);
		}
		if( toBeRemoved.size() > 0 ) swingConfig.getCharsetList().removeAll(toBeRemoved);
	}
	

	class CharsetLoader implements Runnable {
		private Collection<Charset> charsets;
		public void run() {
			charsets = Charset.availableCharsets().values();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					for(Charset charset : charsets) {
						if( ! ((DefaultListModel) availableList.getModel()).contains(charset)) {
							((DefaultListModel) supplyList.getModel()).addElement(charset);
						}
					}
					supplyList.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR ));
					supplyList.setToolTipText( null );
				}
			});
		}
	}
}

class CharsetListModelComboBoxWrapper implements ComboBoxModel {
	private DefaultListModel model;
	private Charset selection;
	public CharsetListModelComboBoxWrapper(DefaultListModel model) {
		this.model = model;
	}
	public void addListDataListener(ListDataListener arg0) {
		model.addListDataListener(arg0);
	}
	public Object getElementAt(int index) {
		return model.getElementAt(index);
	}
	public int getSize() {
		return model.getSize();
	}
	public void removeListDataListener(ListDataListener arg0) {
		model.removeListDataListener(arg0);
	}
	public Object getSelectedItem() {
		return selection;
	}
	public void setSelectedItem(Object item) {
		selection = (Charset) item;
	}
}
