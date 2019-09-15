package main.java.com.github.nianna.karedi.action.tag;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.tag.ChangeMedleyCommand;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongContext;

class SetMedleyFromSelectionAction extends NewKarediAction {
    private boolean setStartBeat;
    private boolean setEndBeat;
    private KarediActions handledAction;

    private final CommandExecutor commandExecutor;
    private final SongContext songContext;
    private final AppContext appContext; //TODO

    SetMedleyFromSelectionAction(KarediActions handledAction, boolean setStartBeat, boolean setEndBeat, NoteSelection noteSelection, CommandExecutor commandExecutor, SongContext songContext, AppContext appContext) {
        this.handledAction = handledAction;
        this.commandExecutor = commandExecutor;
        this.songContext = songContext;
        setDisabledCondition(noteSelection.isEmptyProperty());
        this.setEndBeat = setEndBeat;
        this.setStartBeat = setStartBeat;
        this.appContext = appContext;
    }

    @Override
    protected void onAction(ActionEvent event) {
        Integer startBeat = setStartBeat ? appContext.getSelectionBounds().getLowerXBound() : null;
        Integer endBeat = setEndBeat ? appContext.getSelectionBounds().getUpperXBound() : null;
        commandExecutor.execute(new ChangeMedleyCommand(songContext.getActiveSong(), startBeat, endBeat));
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}