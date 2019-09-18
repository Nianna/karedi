package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.Note;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.INCREASE_SELECTION;

@Component
public class SelectMoreAction extends NewKarediAction {

    private final DisplayContext displayContext;

    private final NoteSelection selection;

    private final SongPlayer songPlayer;

    private SelectMoreAction(DisplayContext displayContext, NoteSelection selection, SongPlayer songPlayer) {
        this.displayContext = displayContext;
        this.selection = selection;
        this.songPlayer = songPlayer;
        setDisabledCondition(this.displayContext.activeTrackIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        selection.makeSelectionConsecutive();
        Optional<Note> lastNote = selection.getLast();
        if (lastNote.isPresent()) {
            lastNote.flatMap(Note::getNext).ifPresent(nextNote -> {
                selection.select(nextNote);
                correctVisibleArea(lastNote.get(), nextNote);
            });
        } else {
            displayContext.getActiveTrack().noteAtOrLater(songPlayer.getMarkerBeat()).ifPresent(selection::select);
        }
    }

    private void correctVisibleArea(Note lastNote, Note nextNote) {
        IntBounded visibleAreaBounds = displayContext.getVisibleAreaBounds();
        int lowerXBound = visibleAreaBounds.getLowerXBound();
        if (lastNote.getLine() != nextNote.getLine()) {
            int nextLineUpperBound = nextNote.getLine().getLast().getStart() + 1;
            if (!visibleAreaBounds.inBoundsX(nextLineUpperBound)) {
                displayContext.setVisibleAreaXBounds(lowerXBound, nextLineUpperBound);
                List<Note> visibleNotes = displayContext.getActiveTrack().getNotes(lowerXBound, nextLineUpperBound);
                if (visibleNotes.size() > 0) {
                    displayContext.assertBorderlessBoundsVisible(new BoundingBox<>(visibleNotes));
                }
            }
            displayContext.setActiveLine(null);
        }
    }

    @Override
    public KarediActions handles() {
        return INCREASE_SELECTION;
    }
}
