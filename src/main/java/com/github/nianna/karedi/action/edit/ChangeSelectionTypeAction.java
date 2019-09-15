package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.MarkAsTypeCommand;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.song.Note;

import java.util.ArrayList;

class ChangeSelectionTypeAction extends NewKarediAction {
    private final KarediActions handledAction;
    private final Note.Type type;
    private final NoteSelection selection;
    private final CommandExecutor commandExecutor;

    ChangeSelectionTypeAction(KarediActions handledAction, Note.Type type, NoteSelection selection, CommandExecutor commandExecutor) {
        this.handledAction = handledAction;
        this.type = type;
        this.selection = selection;
        this.commandExecutor = commandExecutor;
        setDisabledCondition(this.selection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        commandExecutor.execute(new MarkAsTypeCommand(new ArrayList<>(selection.get()), type));
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}