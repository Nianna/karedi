package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.CLEAR_SELECTION;

@Component
public class SelectNoneAction extends NewKarediAction {

    private final NoteSelection noteSelection;

    private SelectNoneAction(NoteSelection noteSelection) {
        this.noteSelection = noteSelection;
        setDisabledCondition(noteSelection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        noteSelection.clear();
    }

	@Override
	public KarediActions handles() {
		return CLEAR_SELECTION;
	}
}