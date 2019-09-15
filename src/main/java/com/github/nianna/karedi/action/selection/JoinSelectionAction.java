package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.JoinNotesCommand;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.song.Note;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.JOIN_SELECTION;

@Component
public class JoinSelectionAction extends NewKarediAction {

    private final NoteSelection selection;

    private final CommandExecutor commandExecutor;

    private JoinSelectionAction(NoteSelection selection, CommandExecutor commandExecutor) {
        this.selection = selection;
        this.commandExecutor = commandExecutor;
        setDisabledCondition(selection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Note outcome = selection.getFirst().get();
        if (selection.size() == 1) {
            selection.getLast().flatMap(Note::getNext).ifPresent(selection::select);
        }
        commandExecutor.execute(new JoinNotesCommand(selection.get()));
        selection.selectOnly(outcome);
    }

    @Override
    public KarediActions handles() {
        return JOIN_SELECTION;
    }
}