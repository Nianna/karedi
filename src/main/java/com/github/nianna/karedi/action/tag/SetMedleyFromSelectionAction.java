package main.java.com.github.nianna.karedi.action.tag;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.tag.ChangeMedleyCommand;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;

class SetMedleyFromSelectionAction extends NewKarediAction {
    private boolean setStartBeat;
    private boolean setEndBeat;
    private KarediActions handledAction;

    private final CommandExecutor commandExecutor;
    private final DisplayContext displayContext;
    private final NoteSelection selection;

    SetMedleyFromSelectionAction(KarediActions handledAction, boolean setStartBeat, boolean setEndBeat, NoteSelection noteSelection, CommandExecutor commandExecutor, DisplayContext displayContext, NoteSelection selection) {
        this.handledAction = handledAction;
        this.commandExecutor = commandExecutor;
        this.displayContext = displayContext;
        this.selection = selection;
        setDisabledCondition(noteSelection.isEmptyProperty());
        this.setEndBeat = setEndBeat;
        this.setStartBeat = setStartBeat;
    }

    @Override
    protected void onAction(ActionEvent event) {
        Integer startBeat = setStartBeat ? selection.getSelectionBounds().getLowerXBound() : null;
        Integer endBeat = setEndBeat ? selection.getSelectionBounds().getUpperXBound() : null;
        commandExecutor.execute(new ChangeMedleyCommand(displayContext.getActiveSong(), startBeat, endBeat));
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}