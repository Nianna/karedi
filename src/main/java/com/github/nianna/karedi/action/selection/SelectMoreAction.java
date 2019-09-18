package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongContext;
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

    private final SongContext songContext;

    private final NoteSelection selection;

    private final SongPlayer songPlayer;

    private SelectMoreAction(SongContext songContext, NoteSelection selection, SongPlayer songPlayer) {
        this.songContext = songContext;
        this.selection = selection;
        this.songPlayer = songPlayer;
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
        IntBounded visibleAreaBounds = songContext.getVisibleAreaBounds();
        int lowerXBound = visibleAreaBounds.getLowerXBound();
        if (lastNote.getLine() != nextNote.getLine()) {
            int nextLineUpperBound = nextNote.getLine().getLast().getStart() + 1;
            if (!visibleAreaBounds.inBoundsX(nextLineUpperBound)) {
                songContext.setVisibleAreaXBounds(lowerXBound, nextLineUpperBound);
                List<Note> visibleNotes = songContext.getActiveTrack().getNotes(lowerXBound, nextLineUpperBound);
                if (visibleNotes.size() > 0) {
                    songContext.assertBorderlessBoundsVisible(new BoundingBox<>(visibleNotes));
                }
            }
            songContext.setActiveLine(null);
        }
    }

    @Override
    public KarediActions handles() {
        return INCREASE_SELECTION;
    }
}
