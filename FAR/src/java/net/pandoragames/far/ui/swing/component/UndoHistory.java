package net.pandoragames.far.ui.swing.component;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import net.pandoragames.far.ui.model.Resetable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An alternative UndoManager. Groups undoes of similar type
 * together as to avoid "single character" undos. Slightly more handy
 * than <code>javax.swing.undo.CompoundEdit</code>. This mechanism is
 * based on the assumption that UndoableEdits are actually instances of
 * <code>javax.swing.text.AbstractDocument.DefaultDocumentEvent</code>.
 * <p>
 * To bind this UndoHistory to a text component, call the method 
 * {@link #registerUndoHistory(JTextComponent) registerUndoHistory()}. It will 
 * automatically be available through the standard shortcuts 
 * <code>ctrl + z</code> and <code>ctrl + y</code>, and through this
 * classes API.
 * </p><p>
 * Alternatively (or in parallel) you can register the text component using
 * the method {@link #registerSnapshotHistory(JTextComponent) registerSnapshotHistory()}.
 * A "snapshot" of the text will be created every time the method
 * {@link #reset() reset()} is called on this object. The snapshot history will
 * automatically be available through the shortcuts 
 * <code>alt + &larr;</code> and <code>ctrl + &rarr;</code> (arrows from cursor block), 
 * and through this classes API.
 * </p><p>
 * The difference between the two types of history is the following: While the undo history
 * is recorded automatically the snapshot history requires explicite recording through
 * the <code>reset()</code> method. The snapshot history is always retained and only grows
 * with more snapshots being added. The undo history on the other hand may grow shrink and
 * change. If text modifications are undone, then continued from an earlyer point in history,
 * the undone modifications are discarded causing the undo history to shrink.
 * </p><p>
 * Every text component must have its own instance of UndoHistory, that is only 
 * one text component may be registered per UndoHistory.
 * </p>
 *
 * @author Olivier Wehner at 25/11/2009
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
public class UndoHistory implements UndoableEditListener, Resetable {
	
	/** Key for Action in action map of text component */
	public static String ACTION_KEY_UNDO = "Undo";
	/** Key for Action in action map of text component */
	public static String ACTION_KEY_REDO = "Redo";
	/** Key for Action in action map of text component */
	public static String ACTION_KEY_PREVIOUS = "Previous";
	/** Key for Action in action map of text component */
	public static String ACTION_KEY_NEXT = "Next";
	
	// types of UndoableEdits (undos) for grouping
	private enum UNDOTYPE {CHANGE, INSERT, WHITESPACE, REMOVE, OTHER};
	// list of grouped undos
	private List<UndoRun> undoList = new ArrayList<UndoRun>();
	// the undo pointer
	private int index = 0;
	// type of last received undo to identify grouping borders
	private UNDOTYPE lastWrittenUndo = UNDOTYPE.OTHER;
	// the snapshot history object
	private SnapshotHistory snapshots;
	// marker to make the next undo comming in volatile
	private int transientRecords = 0;
	// logger for heavy debugging
	private Log logger = LogFactory.getLog( this.getClass() );

	//Actions
	private Action undoAction;
	private Action redoAction;
	private Action previousAction;
	private Action nextAction;
	
	/**
	 * Adds an UndoableEdit object (undo) to the list of undoable modifications. 
	 * @param anEdit to be added to the undo history
	 */
	public boolean addEdit(UndoableEdit anEdit) {
		cutBranch();	
		if( logger.isTraceEnabled() ) {
			logger.trace( "ADD:" + stringValue(anEdit) );
		}
		UNDOTYPE undoType = UNDOTYPE.OTHER;
		if( anEdit instanceof AbstractDocument.DefaultDocumentEvent) {
			AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) anEdit;
			if( event.getType() == DocumentEvent.EventType.REMOVE ) {
				undoType = UNDOTYPE.REMOVE;
			} else if( event.getType() == DocumentEvent.EventType.INSERT ) {
				String change = stringValue(anEdit);
				if((change.length() == 1) && (Character.isWhitespace( change.charAt(0) ))) {
					undoType = UNDOTYPE.WHITESPACE;
				} else {
					undoType = UNDOTYPE.INSERT;									
				}
			} else if( event.getType() == DocumentEvent.EventType.CHANGE ) {
				undoType = UNDOTYPE.CHANGE;
			}
		} 
		if(UNDOTYPE.OTHER.equals( undoType )) {
			addIt( anEdit, undoType );
		} else if (lastWrittenUndo.equals( undoType )){
			// an UndoRun has already been created
			undoList.get( index - 1 ).addEdit( anEdit );
		} else {
			addIt( anEdit, undoType );
		}
		lastWrittenUndo = undoType;
		transientRecords = 0;
		validateDOActions();
		validateShotActions();
		return false;
	}

	/**
	 * Returns true if there is a tex modification that can be "redone".
	 * @return true if a "redo" is possible.
	 */
	public boolean canRedo() {
		return index < undoList.size() 
				&& index >= 0
				&& undoList.get(index).canRedo();
	}

	/**
	 * Returns true if there is a tex modification that can be "undone".
	 * @return true if a "undo" is possible.
	 */
	public boolean canUndo() {
		return index > 0 && undoList.get(index - 1).canUndo();
	}

	/**
	 * "Redoes" the last undone text modification if any is available.
	 * Does not throw any "CannotRedoException".
	 */
	public void redo() {
		if(canRedo()) {
			if( logger.isDebugEnabled() ) {
				logger.debug( "redo#" + index + " (" + undoList.get( index ).canRedo() 
							+"):" + stringValue(undoList.get( index )));
			}
			try{
				undoList.get(index).redo();
				index++;
				validateDOActions();
           } catch (CannotRedoException e) {
            	// not possible unless interfered by concurrent thread
            	logger.error("Redo not possible", e);
            }
		} else {
			validateDOActions();
			logger.debug("REDO called but can not be executed");
		}
	}

	/**
	 * "Undoes" the last registered text modification if any is available.
	 * Does not throw any "CannotUndoException".
	 */
	public void undo() {
		if(canUndo()) {
			index--;
			if( logger.isDebugEnabled() ) {
				logger.debug( "undo#" + index + " (" + undoList.get( index ).canUndo() 
							+"):" + stringValue(undoList.get( index )));
			}
			try {
				undoList.get(index).undo();
				lastWrittenUndo = UNDOTYPE.OTHER;
				validateDOActions();
			} catch (CannotUndoException e) {
            	// not possible unless interfered by concurrent thread
            	logger.error("Undo not possible", e);
            }
		} else {
			validateDOActions();
			logger.debug("UNDO called but can not be executed");
		}
	}
	
	/**
	 * Sets the text component to the previous snapshot in the
	 * snapshot history if there is any.
	 */
	public void previous() {
		if( snapshots != null ) {
			snapshots.previous();
			validateShotActions();
		}
	}
	
	/**
	 * Sets the text component to the next (newer) snapshot in the
	 * snapshot history if there is any.
	 */
	public void next() {
		if( snapshots != null ) {
			snapshots.next();
			validateShotActions();
		}		
	}

	/**
	 * Returns true if there is an older snapshot in the history
	 * than the one currently displayed.
	 * @return true if calling <code>previous()</code> would have any effect.
	 */
	public boolean hasPrevious() {
		if( snapshots != null ) {
			return snapshots.hasPrevious();
		} else {
			return false;
		}
	}
	
	/**
	 * Returns true if there is a newer snapshot in the history
	 * than the one currently displayed.
	 * @return true if calling <code>next()</code> would have any effect.
	 */
	public boolean hasNext() {
		if( snapshots != null ) {
			return snapshots.hasNext();
		} else {
			return false;
		}
	}

	/**
	 * Method from interface <code>UndoableEditListener</code>.
	 * Interfaces with the text component (pecisely: its Document object)
	 * to record text modifications.
	 * @param event containing an UndoableEdit obejct
	 */
	public void undoableEditHappened(UndoableEditEvent event) {
		this.addEdit(event.getEdit());
	}

	/**
	 * Forces the start of a new run of undos, and discards 
	 * any open redoes. Takes a snapshot of the state of the 
	 * text component.
	 */
	public void reset() {
		lastWrittenUndo = UNDOTYPE.OTHER;
		for(int i = undoList.size() - 1; i >= index; i--) {
			undoList.remove(i);
		}
		if( snapshots != null ) {
			snapshots.takeSnapShot();
			validateShotActions();
		}
	}
	
	/**
	 * Registers the specified text component for the undo history.
	 * This enables the <code>ctrl + z</code> and <code>ctrl + y</code> shortcuts.
	 * @param component to be registered for undos.
	 */
	public void registerUndoHistory(JTextComponent component) {
	    // Create an undo action and add it to the text component
		undoAction = new AbstractAction("Undo") {
            public void actionPerformed(ActionEvent evt) {
                if (UndoHistory.this.canUndo()) {
                	UndoHistory.this.undo();
                }
	         }
	    };
	    undoAction.setEnabled( false );
		component.getActionMap().put(ACTION_KEY_UNDO, undoAction );
	    
	    // Create a redo action and add it to the text component
		redoAction = new AbstractAction("Redo") {
            public void actionPerformed(ActionEvent evt) {
                if (UndoHistory.this.canRedo()) {
                	UndoHistory.this.redo();
                }
	        }
	    };
	    redoAction.setEnabled( false );
		component.getActionMap().put(ACTION_KEY_REDO, redoAction );
	    
	    // Bind the actions to ctl-Z and ctl-Y
		component.getInputMap().put(KeyStroke.getKeyStroke("control Z"), ACTION_KEY_UNDO);
		undoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Z"));
		component.getInputMap().put(KeyStroke.getKeyStroke("control Y"), ACTION_KEY_REDO);
		redoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Y"));
		
		// registers this UndoHistory as an UndoableEditListener
		component.getDocument().addUndoableEditListener( this );
	}
	
	/**
	 * Registers the specified text component for the snapshot history.
	 * This enables the <code>alt + &larr;</code> and <code>ctrl + &rarr;</code> shortcuts.
	 * @param component to be registered for snapshots.
	 */
	public void registerSnapshotHistory(JTextComponent component) {
		snapshots = new SnapshotHistory( component );
		
	    // Create a previous action and add it to the text component
		previousAction = new AbstractAction("Previous") {
            public void actionPerformed(ActionEvent evt) {
            	UndoHistory.this.previous();
            }
		};
		previousAction.setEnabled( false );
		component.getActionMap().put( ACTION_KEY_PREVIOUS, previousAction );
		    
		    // Create a next action and add it to the text component
		nextAction = new AbstractAction("Next") {
            public void actionPerformed(ActionEvent evt) {
            	UndoHistory.this.next();
            }
        };
        nextAction.setEnabled( false );
		component.getActionMap().put( ACTION_KEY_NEXT, nextAction );
		    
	    // Bind the actions to alt-left-arrow and alt-right arrow
		previousAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt LEFT"));
		component.getInputMap().put(KeyStroke.getKeyStroke("alt LEFT"), ACTION_KEY_PREVIOUS);
		component.getInputMap().put(KeyStroke.getKeyStroke("alt KP_LEFT"), ACTION_KEY_PREVIOUS);
		nextAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt RIGHT"));
		component.getInputMap().put(KeyStroke.getKeyStroke("alt RIGHT"), ACTION_KEY_NEXT);
		component.getInputMap().put(KeyStroke.getKeyStroke("alt KP_RIGHT"), ACTION_KEY_NEXT);
	}
	
// -- private methods -----------------------------------------------------------------	
	
	/*
	 * Discards all used undos at the end of the list
	 */
	private void cutBranch() {
		for(int i = undoList.size() - 1; i >= index; i--) {
			undoList.get(i).die();
			undoList.remove(i);
		}
	}
	
	private void addIt(UndoableEdit anEdit, UNDOTYPE undoType ) {
		UndoRun undoRun = new UndoRun( undoType ); 
		undoRun.addEdit( anEdit );
		undoList.add( undoRun );
		index++;
	}
	
	private void validateDOActions() {
		if( redoAction != null ) {
			redoAction.setEnabled( canRedo() );
		}
		if( undoAction != null ) {
			undoAction.setEnabled( canUndo() );
		}
	}

	private void validateShotActions() {
		if( previousAction != null ) {
			previousAction.setEnabled( hasPrevious() );
		}
		if( nextAction != null ) {
			nextAction.setEnabled( hasNext() );
		}
	}

	/*
	 * Returns a string representation of the specified UndoableEdit for logging
	 */
	private String stringValue(UndoableEdit edit) {
		if( edit instanceof AbstractDocument.DefaultDocumentEvent) {
			AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent) edit;
			
			String change = null;
			if( event.getType() == DocumentEvent.EventType.REMOVE) {
				change = "DEL";
			} else  {
				try {
					change = event.getDocument().getText(event.getOffset(), event.getLength());
					if( event.getType() == DocumentEvent.EventType.CHANGE ) change = change + " (c)";
				} catch(BadLocationException blx) {
					change = "EXC: " + blx.getMessage();
				}
			}
			return change;
		} else {
			return edit.getPresentationName();
		}
	}
	
	/**
	 * Groups a series of undos together and executes them all at once.
	 * Like CompoundEdit but with no additional protocol syntax or API.
	 */
	class UndoRun implements UndoableEdit {
		
		private List<UndoableEdit> undoRun = new ArrayList<UndoableEdit>();
		private StringBuffer stringValue = new StringBuffer();
		private UNDOTYPE type;
		private boolean stateDone = true;
		
		public UndoRun(UNDOTYPE undotype) {
			type = undotype;
		}

		public UNDOTYPE getType() {
			return type;
		}
		
		public boolean addEdit(UndoableEdit anEdit) {
			undoRun.add(anEdit);
			stringValue.append( stringValue(anEdit) );			
			if(! anEdit.canUndo() ) {
				logger.warn("UndoableEdit " + anEdit.getClass().getName()
						+ "(" + stringValue(anEdit) + ") not undoable!");
			}
			return false;
		}

		public boolean canRedo() {
			return undoRun.size() > 0 && ! stateDone;
		}

		public boolean canUndo() {
			return undoRun.size() > 0 && stateDone;
		}

		public void die() {
			for(UndoableEdit edit : undoRun) {
				edit.die();
			}
		}

		public String getPresentationName() {
			return stringValue.toString();
		}

		public String getRedoPresentationName() {
			return getPresentationName();
		}

		public String getUndoPresentationName() {
			return getPresentationName();
		}

		public boolean isSignificant() {
			return true;
		}

		public void redo() throws CannotRedoException {
			if(canRedo()) {
				for(int i = 0; i < undoRun.size(); i++) {
					if(logger.isDebugEnabled()) {
						logger.debug( "redo-internal#" + i + " (" + undoRun.get( i ).canRedo() 
									+"):" + stringValue(undoRun.get( i )));
					}
					undoRun.get( i ).redo();					
				}
				stateDone = true;
			} else {
				throw new CannotRedoException();
			}
		}

		public boolean replaceEdit(UndoableEdit anEdit) {
			throw new UnsupportedOperationException("Not implemented here");
		}

		public void undo() throws CannotUndoException {
			if(canUndo()) {
				for(int i = undoRun.size() - 1; i >= 0; i--) {
					if(logger.isDebugEnabled()) {
						logger.debug( "undo-internal#" + i + " (" + undoRun.get( i ).canUndo() 
									+"):" + stringValue(undoRun.get( i )));
					}
					undoRun.get( i ).undo();					
				}
				stateDone = false;
			} else {
				throw new CannotUndoException();
			}
		}
	}

	/**
	 * Manages the snapshot history. Thightly cooperates with the undo history,
	 * to prevent the snapshots piling up in the undo history.
	 */
	class SnapshotHistory {
		
		private JTextComponent textComponent;
		private List<String> snapshots = new ArrayList<String>();
		private int sindex = 0;
		
		SnapshotHistory(JTextComponent component) {
			textComponent = component;
		}
		
		public void takeSnapShot() {
			snapshots.add( textComponent.getText() );
			sindex = snapshots.size();
		}
		
		public boolean hasPrevious() {
 			if (snapshots.size() == 1) {
				return sindex > 0 && ! snapshots.get(0).equals( textComponent.getText() );
			} else {
				return sindex > 0;
			}
		}
		
		public boolean hasNext() {
			return sindex < snapshots.size();
		}
		
		public void previous() {
			if( hasPrevious() ) {
				if( transientRecords > 0 ) {
					for(int i = 0; i < transientRecords; i++) {
						undo();
					}
					sindex--;
				} else {
					sindex = snapshots.size() - 1;
					//	last entry in history equals current text
					if( snapshots.get(sindex).equals( textComponent.getText() ) && hasPrevious() ) {
						sindex--;
					}
				}
				logger.debug("History <-- " + sindex + (transientRecords > 0 ? "T" : "N"));
				lastWrittenUndo = UNDOTYPE.OTHER;
				cutBranch(); // cut before taking size	
				int records = undoList.size();
				textComponent.setText( snapshots.get(sindex) );
				transientRecords = undoList.size() - records;
				logger.debug("TR=" + transientRecords);
			} 
		}
		
		public void next() {
			if( hasNext() && (transientRecords > 0) ) {
				for(int i = 0; i < transientRecords; i++) {
					undo();
				}
				sindex++;
				// last entry in history equals current text
				if((sindex == snapshots.size() - 1)
					&& (snapshots.get(sindex).equals( textComponent.getText() ))) {
					sindex++;
					transientRecords = 0;
				}
				logger.debug("History --> " + sindex + " (" + snapshots.size() + ")");
				if( sindex < snapshots.size() ) {
					lastWrittenUndo = UNDOTYPE.OTHER;
					cutBranch(); // cut before taking size
					int records = undoList.size();
					textComponent.setText( snapshots.get(sindex) );
					transientRecords = undoList.size() - records;		
					logger.debug("TR=" + transientRecords);
				}
			} 
		}		
	}
}
