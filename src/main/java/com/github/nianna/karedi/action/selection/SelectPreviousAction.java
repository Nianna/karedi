package main.java.com.github.nianna.karedi.action.selection;


import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.SELECT_PREVIOUS;

@Component
public class SelectPreviousAction extends NewKarediAction {

    private final SongState songState;

    private final NoteSelection noteSelection;

    private final VisibleArea visibleArea;

    private final SongPlayer songPlayer;

    private final BeatMillisConverter beatMillisConverter;

    private SelectPreviousAction(SongState songState, NoteSelection noteSelection, VisibleArea visibleArea, SongPlayer songPlayer, BeatMillisConverter beatMillisConverter) {
        this.songState = songState;
        this.noteSelection = noteSelection;
        this.visibleArea = visibleArea;
        this.songPlayer = songPlayer;
        this.beatMillisConverter = beatMillisConverter;
        setDisabledCondition(songState.activeTrackIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Optional<Note> prevNote = noteSelection.getFirst().flatMap(Note::getPrevious)
                .filter(this::isInVisibleBeatRange);

        if (!prevNote.isPresent() && noteSelection.size() == 0) {
            int markerBeat = songPlayer.getMarkerBeat();
            if (beatMillisConverter.beatToMillis(markerBeat) > songPlayer.getMarkerTime()) {
                markerBeat -= 1;
            }
            prevNote = songState.getActiveTrack().noteAtOrEarlier(markerBeat)
                    .filter(this::isInVisibleBeatRange);
        }
        if (!prevNote.isPresent()) {
            prevNote = songState.getActiveTrack().noteAtOrEarlier(visibleArea.getUpperXBound() - 1);
        }
        if (songState.getActiveLine() != null) {
            prevNote = prevNote.filter(note -> note.getLine().equals(songState.getActiveLine()));
        }
        prevNote.ifPresent(noteSelection::selectOnly);
    }

    private boolean isInVisibleBeatRange(Note note) {
        return visibleArea.inRangeX(note.getStart());
    }

    @Override
    public KarediActions handles() {
        return SELECT_PREVIOUS;
    }
}
