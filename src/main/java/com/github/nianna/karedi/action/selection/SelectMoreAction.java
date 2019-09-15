package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.song.Note;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.INCREASE_SELECTION;

@Component
public class SelectMoreAction extends NewKarediAction {

    private final SongState songState;

    private final NoteSelection selection;

    private final SongPlayer songPlayer;

    private final VisibleArea visibleArea;

    private SelectMoreAction(SongState songState, NoteSelection selection, SongPlayer songPlayer, VisibleArea visibleArea) {
        this.songState = songState;
        this.selection = selection;
        this.songPlayer = songPlayer;
        this.visibleArea = visibleArea;
        setDisabledCondition(this.songState.activeTrackIsNullProperty());
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
            songState.getActiveTrack().noteAtOrLater(songPlayer.getMarkerBeat()).ifPresent(selection::select);
        }
    }

    private void correctVisibleArea(Note lastNote, Note nextNote) {
        int lowerXBound = visibleArea.getLowerXBound();
        int upperXBound = visibleArea.getUpperXBound();
        if (lastNote.getLine() != nextNote.getLine()) {
            int nextLineUpperBound = nextNote.getLine().getLast().getStart() + 1;
            if (!visibleArea.inBoundsX(nextLineUpperBound)) {
                upperXBound = nextLineUpperBound;
                setVisibleAreaXBounds(lowerXBound, upperXBound);
                List<Note> visibleNotes = songState.getActiveTrack().getNotes(lowerXBound, upperXBound);
                if (visibleNotes.size() > 0) {
                    visibleArea.assertBorderlessBoundsVisible(new BoundingBox<>(visibleNotes));
                }
            }
            songState.setActiveLine(null);
        }
    }

    private void setVisibleAreaXBounds(int lowerXBound, int upperXBound) {
        if (visibleArea.setXBounds(lowerXBound, upperXBound)) {
            songState.setActiveLine(null);
        }
    }

    @Override
    public KarediActions handles() {
        return INCREASE_SELECTION;
    }
}
