package net.pandoragames.far.ui.swing.component;

import java.util.ArrayList;
import java.util.List;

import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.pandoragames.far.ui.model.FileNamePattern;

public class FileNamePatternComboboxModel implements MutableComboBoxModel<FileNamePattern> {

	private List<FileNamePattern> patternList;
	private List<ListDataListener> listenerList = new ArrayList<ListDataListener>();
	
	private FileNamePattern selectedItem;
	
	public FileNamePatternComboboxModel(List<FileNamePattern> list) {
		patternList = list;
		if( patternList.size() > 0) {
			selectedItem = patternList.get(0);
		}
	}

	public void addListDataListener(ListDataListener listener) {
		listenerList.add(listener);
	}

	public FileNamePattern getElementAt(int index) {
		return patternList.get(index);
	}

	public int getSize() {
		return patternList.size();
	}

	public void removeListDataListener(ListDataListener listener) {
		listenerList.remove(listener);
	}

	public FileNamePattern getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Object item) {
		if( item == null || item instanceof FileNamePattern) {
			selectedItem = (FileNamePattern)item;
			int index = patternList.indexOf(selectedItem);
			ListDataEvent eve = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index);
			for(ListDataListener listener : listenerList ) {
				listener.contentsChanged(eve);
			}			
		}
	}

	public void addElement(FileNamePattern pattern) {
		if( pattern != null ) {
			patternList.add(pattern);
			ListDataEvent eve = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, patternList.size() -1, patternList.size() -1);
			for(ListDataListener listener : listenerList ) {
				listener.intervalAdded(eve);
			}		
		}
	}

	public void insertElementAt(FileNamePattern pattern, int index) {
		if( pattern != null ) {
			patternList.add(index, pattern);
			ListDataEvent eve = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index);
			for(ListDataListener listener : listenerList ) {
				listener.intervalAdded(eve);
			}		
		}
	}
	

	public boolean remove(FileNamePattern pattern) {
		if( pattern != null ) {
			int index = patternList.indexOf(pattern);
			if( patternList.remove(pattern) ) {
				if(pattern.equals(selectedItem)) {
					selectedItem = patternList.size() > 0 ? patternList.get(0) : null;
				}
				ListDataEvent eve = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index);
				for(ListDataListener listener : listenerList ) {
					listener.intervalRemoved(eve);
				}
				return true;
			}
		}
		return false;
	}
	
	public void removeElement(Object pattern) {
		if( pattern != null && pattern instanceof FileNamePattern) {
			remove((FileNamePattern)pattern);
		}
	}
	
	public void removeElementAt(int index) {
		FileNamePattern pattern = patternList.get(index);
		remove(pattern);
	}
	
	public List<FileNamePattern>  asList() {
		return patternList;
	}
}
