package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.SELECT_PREVIOUS;

@Component
public class SelectPreviousAction extends KarediAction {

    private final DisplayContext displayContext;

    private final NoteSelection noteSelection;

    private final SongPlayer songPlayer;

    private final BeatMillisConverter beatMillisConverter;

    private SelectPreviousAction(DisplayContext displayContext, NoteSelection noteSelection, SongPlayer songPlayer, BeatMillisConverter beatMillisConverter) {
        this.displayContext = displayContext;
        this.noteSelection = noteSelection;
        this.songPlayer = songPlayer;
        this.beatMillisConverter = beatMillisConverter;
        setDisabledCondition(displayContext.activeTrackIsNullProperty());
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
            prevNote = displayContext.getActiveTrack().noteAtOrEarlier(markerBeat)
                    .filter(this::isInVisibleBeatRange);
        }
        if (!prevNote.isPresent()) {
            prevNote = displayContext.getActiveTrack().noteAtOrEarlier(displayContext.getVisibleAreaBounds().getUpperXBound() - 1);
        }
        if (displayContext.getActiveLine() != null) {
            prevNote = prevNote.filter(note -> note.getLine().equals(displayContext.getActiveLine()));
        }
        prevNote.ifPresent(noteSelection::selectOnly);
    }

    private boolean isInVisibleBeatRange(Note note) {
        return displayContext.getVisibleAreaBounds().inRangeX(note.getStart());
    }

    @Override
    public KarediActions handles() {
        return SELECT_PREVIOUS;
    }
}

