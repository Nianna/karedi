package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.DECREASE_SELECTION;

@Component
public class SelectLessAction extends KarediAction {

    private final NoteSelection selection;

    private SelectLessAction(NoteSelection selection) {
        this.selection = selection;
        setDisabledCondition(this.selection.sizeProperty().lessThanOrEqualTo(1));
    }

    @Override
    protected void onAction(ActionEvent event) {
        selection.makeSelectionConsecutive();
        selection.getLast().ifPresent(selection::deselect);
    }

    @Override
    public KarediActions handles() {
        return DECREASE_SELECTION;
    }
}
