package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SelectNextAction extends NewKarediAction {

    private final SongContext songContext;

    private final NoteSelection noteSelection;

    private final VisibleArea visibleArea;

    private final SongPlayer songPlayer;

    private SelectNextAction(SongContext songContext, NoteSelection noteSelection, VisibleArea visibleArea, SongPlayer songPlayer) {
        this.songContext = songContext;
        this.noteSelection = noteSelection;
        this.visibleArea = visibleArea;
        this.songPlayer = songPlayer;
        setDisabledCondition(this.songContext.activeTrackIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Optional<Note> nextNote = noteSelection.getLast().flatMap(Note::getNext)
                .filter(this::isInVisibleBeatRange);
        SongTrack activeTrack = songContext.getActiveTrack();
        SongLine activeLine = songContext.getActiveLine();

        if (!nextNote.isPresent() && noteSelection.size() == 0) {
            int markerBeat = songPlayer.getMarkerBeat();
            nextNote = activeTrack.noteAtOrLater(markerBeat)
                    .filter(this::isInVisibleBeatRange);
        }
        if (!nextNote.isPresent()) {
            nextNote = activeTrack.noteAtOrLater(visibleArea.getLowerXBound());
        }
        if (activeLine != null) {
            nextNote = nextNote.filter(note -> note.getLine().equals(activeLine));
        }
        nextNote.ifPresent(noteSelection::selectOnly);
    }

    private boolean isInVisibleBeatRange(Note note) {
        return visibleArea.inRangeX(note.getStart());
    }

    @Override
    public KarediActions handles() {
        return KarediActions.SELECT_NEXT;
    }
}