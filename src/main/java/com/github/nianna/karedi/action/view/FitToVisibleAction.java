package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.Note;

import java.util.List;

class FitToVisibleAction extends NewKarediAction {
    private final KarediActions handledAction;
    private final boolean vertically;
    private final boolean horizontally;
    private final DisplayContext displayContext;

    FitToVisibleAction(KarediActions handledAction, boolean vertically, boolean horizontally, DisplayContext displayContext) {
        this.handledAction = handledAction;
        this.vertically = vertically;
        this.horizontally = horizontally;
        this.displayContext = displayContext;
        setDisabledCondition(this.displayContext.activeSongIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        IntBounded visibleAreaBounds = displayContext.getVisibleAreaBounds();
        List<Note> visibleNotes = displayContext.getActiveSong().getVisibleNotes(visibleAreaBounds.getLowerXBound(), visibleAreaBounds.getUpperXBound());
        if (visibleNotes.size() > 0) {
            IntBounded bounds = displayContext.addMargins(new BoundingBox<>(visibleNotes));
            if (horizontally) {
                displayContext.setVisibleAreaXBounds(bounds.getLowerXBound(), bounds.getUpperXBound());
            }
            if (vertically) {
                displayContext.setVisibleAreaYBounds(bounds.getLowerYBound(), bounds.getUpperYBound());
            }
        }
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}