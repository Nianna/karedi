package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SelectNextAction extends KarediAction {

    private final DisplayContext displayContext;

    private final NoteSelection noteSelection;

    private final SongPlayer songPlayer;

    private SelectNextAction(DisplayContext displayContext, NoteSelection noteSelection, SongPlayer songPlayer) {
        this.displayContext = displayContext;
        this.noteSelection = noteSelection;
        this.songPlayer = songPlayer;
        setDisabledCondition(this.displayContext.activeTrackIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Optional<Note> nextNote = noteSelection.getLast().flatMap(Note::getNext)
                .filter(this::isInVisibleBeatRange);
        SongTrack activeTrack = displayContext.getActiveTrack();
        SongLine activeLine = displayContext.getActiveLine();

        if (!nextNote.isPresent() && noteSelection.size() == 0) {
            int markerBeat = songPlayer.getMarkerBeat();
            nextNote = activeTrack.noteAtOrLater(markerBeat)
                    .filter(this::isInVisibleBeatRange);
        }
        if (!nextNote.isPresent()) {
            nextNote = activeTrack.noteAtOrLater(displayContext.getVisibleAreaBounds().getLowerXBound());
        }
        if (activeLine != null) {
            nextNote = nextNote.filter(note -> note.getLine().equals(activeLine));
        }
        nextNote.ifPresent(noteSelection::selectOnly);
    }

    private boolean isInVisibleBeatRange(Note note) {
        return displayContext.getVisibleAreaBounds().inRangeX(note.getStart());
    }

    @Override
    public KarediActions handles() {
        return KarediActions.SELECT_NEXT;
    }
}