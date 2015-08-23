package net.pandoragames.far.ui.swing.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.font.TextAttribute;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import net.pandoragames.far.ui.model.FindForm;
import net.pandoragames.far.ui.model.MessageBox;
import net.pandoragames.far.ui.model.Resetable;
import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.component.listener.SearchBaseHasChangedListener;
import net.pandoragames.far.ui.swing.dialog.InfoView;
import net.pandoragames.far.ui.swing.dialog.SubWindow;
import net.pandoragames.util.i18n.Localizer;

/**
 * Panel with checkbox for selection of Date boundaries.
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
public class DateRestrictionPanel extends JPanel implements Resetable {

	private FindForm form;
	private JButton link;
	private Localizer localizer;
	private Calendar startOfDay;
	private JFrame baseWindow;
	private SwingConfig swingConfig;
	private SearchBaseHasChangedListener searchBaseListener;
	
	// Mind, this is not fit for concurrent use!
	private SimpleDateFormat dateTimeFormat = new SimpleDateFormat( SwingConfig.STANDARD_DATE_TIME_FORMAT );
	private SimpleDateFormat timeFormat = new SimpleDateFormat( SwingConfig.STANDARD_TIME_FORMAT );
	private SimpleDateFormat dateFormat = new SimpleDateFormat( SwingConfig.STANDARD_DATE_FORMAT );
	
	public DateRestrictionPanel(FindForm data, ComponentRepository componentRepository, SwingConfig swing) {
		localizer = componentRepository.getLocalizer();
		baseWindow = componentRepository.getRootWindow();
		form = data;
		swingConfig = swing;
		startOfDay = Calendar.getInstance();
		startOfDay.set(Calendar.HOUR_OF_DAY, 0);
		startOfDay.set(Calendar.MINUTE, 0);
		startOfDay.set(Calendar.SECOND, 0);
		searchBaseListener = componentRepository.getSearchBaseListener();
		this.setLayout( new FlowLayout( FlowLayout.LEFT ) );
		link = new LinkButton( localizer.localize("button.restrict-modified-date"));
		link.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				TimeDialog dialog = new TimeDialog( link.getLocationOnScreen() );
				dialog.pack();
				dialog.setVisible(true);
			}
		});
		this.add( link );
		componentRepository.getResetDispatcher().addResetable( this );
	}
	
	public void update(FindForm data) {
		form = data;
		refreshLabel();
	}
	
	public void reset() {
		form.setAfter(null);
		form.setBefore(null);
		link.setText(localizer.localize("button.restrict-modified-date"));
	}
	
	private void refreshLabel() {
		if( form.getAfter() == null && form.getBefore() == null ) {
			link.setText(localizer.localize("button.restrict-modified-date"));			
		} else if (form.getAfter() == null) {
			String lastModified = formatSingleDate( form.getBefore() );
			link.setText(localizer.localize("label.before-y", lastModified));			
		} else if (form.getBefore() == null) {
			String lastModified = formatSingleDate( form.getAfter() );
			link.setText(localizer.localize("label.after-x", lastModified));			
		} else {	
			String start = formatSingleDate( form.getAfter() );
			String end = formatSingleDate( form.getBefore() );
			link.setText(localizer.localize("label.between-x-and-y", new Object[]{start, end}));
		}		
	}
	
	private String formatSingleDate(Date date) {
		if( startOfDay.getTimeInMillis() < date.getTime() ) {
			return timeFormat.format(date);
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			if( calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0) {
				return dateFormat.format(date);
			} else {
				return dateTimeFormat.format(date);
			}
		}
	}
	
	class TimeDialog extends SubWindow {
		private MessageLabel errorText;
		private DateDefinitionPanel afterPanel;
		private DateDefinitionPanel beforePanel;
		public TimeDialog(Point corner) {
			super(baseWindow, localizer.localize("label.restrict-modified-date"), true);
			
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout( new BorderLayout() );
			
			JPanel basePanel = new JPanel();
			basePanel.setLayout( new BoxLayout( basePanel, BoxLayout.Y_AXIS ) );
			basePanel.setBorder( BorderFactory.createEmptyBorder( 0, 
					SwingConfig.PADDING, 
					SwingConfig.PADDING, 
					SwingConfig.PADDING ) );
			registerCloseWindowKeyListener( basePanel );
			
			// some headspace
			basePanel.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
			
			// error text
			errorText = new MessageLabel();
			TwoComponentsPanel errorPanel = new TwoComponentsPanel(errorText, new JLabel(" "));
			errorPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
			basePanel.add(errorPanel);
			
			// after
			afterPanel = new DateDefinitionPanel( localizer.localize("label.younger-than"), form.getAfter(), errorText);
			afterPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
			basePanel.add( afterPanel );
			
			// before
			beforePanel = new DateDefinitionPanel( localizer.localize("label.older-than"), form.getBefore(), errorText);
			beforePanel.setAlignmentX( Component.LEFT_ALIGNMENT );
			basePanel.add( beforePanel );
			
			getContentPane().add( basePanel, BorderLayout.CENTER );
			
	 		// close button
	 		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
	 		JButton saveButton = new JButton( localizer.localize("button.apply") );
	 		saveButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if( ! afterPanel.isOnError() && ! beforePanel.isOnError()) {
						form.setAfter( afterPanel.getDate() );
						form.setBefore( beforePanel.getDate() );
						searchBaseListener.actionPerformed(event);
						refreshLabel();
						TimeDialog.this.dispose();
					}
				}						
			});
	 		buttonPanel.add( saveButton );
	 		JButton closeButton = new JButton( localizer.localize("button.cancel") );
	 		closeButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					TimeDialog.this.dispose();
				}						
			});
	 		buttonPanel.add( closeButton );
	 		getContentPane().add( buttonPanel, BorderLayout.SOUTH );
	 		
	 		placeOnScreen( corner );
		}
	}
	
	class DateDefinitionPanel extends JPanel {
		private JTextField dateField;
		private JComboBox<ListItem<Long>> stepSelection;
		private ButtonGroup radioGroup = new ButtonGroup();
		private MessageBox errorSink;
		private boolean onError = false;
		private JRadioButton listButton;
		private JRadioButton freeButton;
		
		public DateDefinitionPanel(String title, Date datum, MessageBox messageBox) {
			Date date = datum;
			errorSink = messageBox;
			this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
			this.setBorder( BorderFactory.createTitledBorder(title) );
			
			this.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));
			
			JRadioButton nothingButton = new JRadioButton(localizer.localize("label.unrestricted"));
			nothingButton.setActionCommand("0");
			nothingButton.setSelected( date == null );
			nothingButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if( onError ) {
						errorSink.clear();
						onError = false;
					}
				}						
			});
			radioGroup.add( nothingButton );
			nothingButton.setAlignmentX( Component.LEFT_ALIGNMENT );
			this.add( nothingButton );

			this.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));

			listButton = new JRadioButton( "" );
			listButton.setActionCommand("L");
			listButton.setSelected( false );
			listButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if( onError ) {
						errorSink.clear();
						onError = false;
					}
				}						
			});
			radioGroup.add( listButton );
			stepSelection = new JComboBox<ListItem<Long>>();
			stepSelection.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, 
					swingConfig.getStandardComponentHight()) );
			stepSelection.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 
					swingConfig.getStandardComponentHight()) );
			final long MINMS = 60000L;
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-minutes", 5), MINMS*5));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-minutes", 15), MINMS*15));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-minutes", 30), MINMS*30));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-hours", 1), MINMS*60));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-hours", 2), MINMS*120));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-hours", 4), MINMS*240));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-hours", 8), MINMS*480));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-hours", 12), MINMS*720));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-days", 1), MINMS*60*24));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-days", 2), MINMS*60*24*2));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-days", 5), MINMS*60*24*5));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-days", 7), MINMS*60*24*7));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-days", 14), MINMS*60*24*14));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-months", 1), MINMS*60*24*30));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-months", 2), MINMS*60*24*60));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-months", 3), MINMS*60*24*90));
			stepSelection.addItem( new ListItem<Long>(localizer.localize("label.n-months", 6), MINMS*60*24*180));
			stepSelection.setSelectedIndex(0);
			stepSelection.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					radioGroup.setSelected(listButton.getModel(), true);
					if( onError ) {
						errorSink.clear();
						onError = false;
					}
				}				
			});
			TwoComponentsPanel listPanel = new TwoComponentsPanel(listButton, stepSelection);
			listPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
			this.add( listPanel );
			
			this.add( Box.createRigidArea( new Dimension(1, SwingConfig.PADDING )));

			freeButton = new JRadioButton( SwingConfig.STANDARD_DATE_FORMAT + " [" + SwingConfig.STANDARD_TIME_FORMAT+ "]" );
			freeButton.setActionCommand("F");
			freeButton.setSelected( date != null );
			freeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
				}						
			});
			radioGroup.add( freeButton );
			dateField = new JTextField();
			dateField.setPreferredSize( new Dimension(  SwingConfig.COMPONENT_WIDTH, 
					swingConfig.getStandardComponentHight()) );
			dateField.setMaximumSize( new Dimension(  SwingConfig.COMPONENT_WIDTH_MAX, 
					swingConfig.getStandardComponentHight()) );
			dateField.addFocusListener( new FocusListener() {
				public void focusLost(FocusEvent e) {
					getDate();
				}
				public void focusGained(FocusEvent e) {
					radioGroup.setSelected(freeButton.getModel(), true);
				}
			});
			if( date != null ) dateField.setText( dateTimeFormat.format(date ) );
			TwoComponentsPanel freeDatePanel = new TwoComponentsPanel(freeButton, dateField);
			freeDatePanel.setAlignmentX( Component.LEFT_ALIGNMENT );
			this.add( freeDatePanel );
		}		
		public Date getDate() {
			errorSink.clear();
			String buttonCode = radioGroup.getSelection().getActionCommand();
			if( "0".equals(buttonCode)) {
				return null;
			} else if( "L".equals(buttonCode)) {
				@SuppressWarnings("unchecked")
				ListItem<Long> selection = (ListItem<Long>) stepSelection.getSelectedItem();
				Long value = (Long) selection.getValue();
				return new Date( System.currentTimeMillis() - value.longValue());
			} else if( "F".equals(buttonCode)) {
				try {
					return dateTimeFormat.parse( dateField.getText().trim() );
				} catch(ParseException px) {
					try {
						return dateFormat.parse( dateField.getText().trim() );
					} catch(ParseException pax) {
						errorSink.error( localizer.localize("message.invalid-date-format", dateField.getText().trim()));
						onError = true;
						return null;
					}					
				}
			} else {
				throw new IllegalStateException("Undefined Code '" + buttonCode + "'");
			}
		}
		public boolean isOnError() {
			return onError;
		}
	}
}
