package main.java.com.github.nianna.karedi.command;

import main.java.com.github.nianna.karedi.command.StateManager.StateSnapshot;

public class BackupStateCommandDecorator extends CommandDecorator {

	private final StateManager stateManager;

	private StateSnapshot stateSnapshot;

	public BackupStateCommandDecorator(Command command, StateManager stateManager) {
		super(command);
		this.stateManager = stateManager;
	}

	@Override
	public boolean execute() {
		if (stateSnapshot == null) {
			stateSnapshot = stateManager.createSnapshot();
		} else {
			restoreState();
		}
		return super.execute();
	}

	private void restoreState() {
		stateManager.restore(stateSnapshot);
	}

	@Override
	public void undo() {
		super.undo();
		restoreState();
	}
}
