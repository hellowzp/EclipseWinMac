package net.pandoragames.far.ui.swing.dialog.update;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;
import net.pandoragames.far.ui.swing.dialog.SubWindow;

/**
 * Wizzard that is shown uppon software updates if user interaction is required.
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
public class UpdateDialog extends SubWindow {
	
	private List<UpdateWizzardPanel> wizzardList = new ArrayList<UpdateWizzardPanel>();
	
	private int index = 0;
	
	private JButton previousButton;
	private JButton nextButton;
	
	public UpdateDialog(JFrame owner, SwingConfig config, ComponentRepository repository) {
		super(owner, config.getLocalizer().localize( "message.welcome", config.getClass().getPackage().getSpecificationVersion()),true);
		init( config, repository);
	}

	private void init(SwingConfig config, ComponentRepository repository) {
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		setMinimumSize(new Dimension(450,250));
		// setPreferedSize();
		
		getContentPane().setLayout( new BorderLayout() );

		wizzardList.add( new FrontPage(config, repository) );
		wizzardList.add( new MimeUpdatePanel(config, repository) );
		
		getContentPane().add( wizzardList.get(0), BorderLayout.CENTER );
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		previousButton = new JButton( config.getLocalizer().localize( "button.previous"));
		previousButton.setEnabled( false );
		previousButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				UpdateDialog.this.getContentPane().remove( wizzardList.get(index) );
				index--;
				UpdateDialog.this.getContentPane().add( wizzardList.get(index), BorderLayout.CENTER );
				if( index == 0 ) previousButton.setEnabled( false );
				nextButton.setEnabled( true );				
				UpdateDialog.this.revalidate();
				wizzardList.get(index).repaint();
			}
		});
		buttonPanel.add( previousButton );
		nextButton = new JButton( config.getLocalizer().localize( "button.next"));
		nextButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				UpdateDialog.this.getContentPane().remove( wizzardList.get(index) );
				index++;
				UpdateDialog.this.getContentPane().add( wizzardList.get(index), BorderLayout.CENTER );
				if( index == wizzardList.size() - 1) nextButton.setEnabled( false );
				previousButton.setEnabled( true );
				UpdateDialog.this.revalidate();
				wizzardList.get(index).repaint();
			}
		});
		buttonPanel.add( nextButton );
		JButton finishButton = new JButton( config.getLocalizer().localize( "button.finish"));
		finishButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent eve) {
				for(UpdateWizzardPanel panel : wizzardList) {
					panel.finish();
				}
				UpdateDialog.this.dispose();
			}
		});
		buttonPanel.add( finishButton );
		getContentPane().add( buttonPanel, BorderLayout.SOUTH);
		
		placeOnScreen( config.getScreenCenter() );
	}
	
	/**
	 * Returns true if the update makes some user interaction necessary, i.e. if this dialog should be displayed.
	 * @return true if this dialog should be displayed.
	 */
	public boolean isUserInteractionRequired() {
		for(UpdateWizzardPanel panel : wizzardList) {
			if( panel.isUserInteractionRequested() ) return true;
		}
		return false;
	}
}

class FrontPage extends UpdateWizzardPanel {
	
	public FrontPage(SwingConfig config, ComponentRepository repository) {
		super( config, repository);
	}
	@Override
	public void init(SwingConfig config, ComponentRepository repository) {
		setBorder( BorderFactory.createEmptyBorder( SwingConfig.PADDING*2, 
				SwingConfig.PADDING*2, 
				SwingConfig.PADDING*2, 
				SwingConfig.PADDING*2 ) );
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		add( Box.createVerticalGlue() );
		JLabel label = new JLabel( config.getLocalizer().localize( "message.welcome", config.getClass().getPackage().getSpecificationVersion()) );
		label.setAlignmentX( Component.LEFT_ALIGNMENT );
		add( label );
		add( Box.createVerticalStrut(10) );
		JTextArea textField = new JTextArea(config.getLocalizer().localize( "message.update-requires-action"));
		textField.setBackground( label.getBackground() );
		textField.setAlignmentX( Component.LEFT_ALIGNMENT );
		add( textField );
		add( Box.createVerticalGlue() );
	}
	@Override
	public void finish() {	}
	@Override
	public boolean isUserInteractionRequested() {
		return false;
	}
	
}