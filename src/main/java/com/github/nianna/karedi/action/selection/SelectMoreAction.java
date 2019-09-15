package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.song.Note;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.INCREASE_SELECTION;

@Component
public class SelectMoreAction extends NewKarediAction {

    private final SongContext songContext;

    private final NoteSelection selection;

    private final SongPlayer songPlayer;

    private final VisibleArea visibleArea;

    private SelectMoreAction(SongContext songContext, NoteSelection selection, SongPlayer songPlayer, VisibleArea visibleArea) {
        this.songContext = songContext;
        this.selection = selection;
        this.songPlayer = songPlayer;
        this.visibleArea = visibleArea;
        setDisabledCondition(this.songContext.activeTrackIsNullProperty());
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
            songContext.getActiveTrack().noteAtOrLater(songPlayer.getMarkerBeat()).ifPresent(selection::select);
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
                List<Note> visibleNotes = songContext.getActiveTrack().getNotes(lowerXBound, upperXBound);
                if (visibleNotes.size() > 0) {
                    visibleArea.assertBorderlessBoundsVisible(new BoundingBox<>(visibleNotes));
                }
            }
            songContext.setActiveLine(null);
        }
    }

    private void setVisibleAreaXBounds(int lowerXBound, int upperXBound) {
        if (visibleArea.setXBounds(lowerXBound, upperXBound)) {
            songContext.setActiveLine(null);
        }
    }

    @Override
    public KarediActions handles() {
        return INCREASE_SELECTION;
    }
}
