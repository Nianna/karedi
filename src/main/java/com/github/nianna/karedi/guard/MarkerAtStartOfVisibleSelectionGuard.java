package main.java.com.github.nianna.karedi.guard;

import javafx.beans.Observable;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.region.IntBounded;
import org.springframework.stereotype.Component;

@Component
class MarkerAtStartOfVisibleSelectionGuard implements Guard {

    private final DisplayContext displayContext;
    private final NoteSelection selection;
    private final SongPlayer player;

    MarkerAtStartOfVisibleSelectionGuard(DisplayContext displayContext, NoteSelection selection, SongPlayer player) {
        this.displayContext = displayContext;
        this.selection = selection;
        this.player = player;
    }

    @Override
    public void enable() {
        selection.getSelectionBounds().addListener(this::onSelectionBoundsInvalidated);
    }

    @Override
    public void disable() {
        selection.getSelectionBounds().removeListener(this::onSelectionBoundsInvalidated);
    }

    private void onSelectionBoundsInvalidated(Observable observable) {
        IntBounded selectionBounds = selection.getSelectionBounds();
        if (selection.size() > 0 && selectionBounds.isValid()) {
            player.setMarkerBeat(selectionBounds.getLowerXBound());
            if (displayContext.assertBorderlessBoundsVisible(selectionBounds)) {
                displayContext.setActiveLine(null);
                displayContext.assertAllNeededTonesVisible();
            }
        }
    }
}
