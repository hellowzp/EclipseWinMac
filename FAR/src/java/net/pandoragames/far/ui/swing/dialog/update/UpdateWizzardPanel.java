package net.pandoragames.far.ui.swing.dialog.update;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import net.pandoragames.far.ui.swing.ComponentRepository;
import net.pandoragames.far.ui.swing.SwingConfig;

public abstract class UpdateWizzardPanel extends JPanel {

	public UpdateWizzardPanel(SwingConfig config, ComponentRepository repository) {
		init(config, repository);
	}

	public abstract void init(SwingConfig config, ComponentRepository repository);
	
	public abstract void finish();
	
	public abstract boolean isUserInteractionRequested();

}
