package main.java.com.github.nianna.karedi.action.selection;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.SELECT_PREVIOUS;

@Component
public class SelectPreviousAction extends NewKarediAction {

    private final SongContext songContext;

    private final NoteSelection noteSelection;

    private final SongPlayer songPlayer;

    private final BeatMillisConverter beatMillisConverter;

    private SelectPreviousAction(SongContext songContext, NoteSelection noteSelection, SongPlayer songPlayer, BeatMillisConverter beatMillisConverter) {
        this.songContext = songContext;
        this.noteSelection = noteSelection;
        this.songPlayer = songPlayer;
        this.beatMillisConverter = beatMillisConverter;
        setDisabledCondition(songContext.activeTrackIsNullProperty());
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
            prevNote = songContext.getActiveTrack().noteAtOrEarlier(markerBeat)
                    .filter(this::isInVisibleBeatRange);
        }
        if (!prevNote.isPresent()) {
            prevNote = songContext.getActiveTrack().noteAtOrEarlier(songContext.getVisibleAreaBounds().getUpperXBound() - 1);
        }
        if (songContext.getActiveLine() != null) {
            prevNote = prevNote.filter(note -> note.getLine().equals(songContext.getActiveLine()));
        }
        prevNote.ifPresent(noteSelection::selectOnly);
    }

    private boolean isInVisibleBeatRange(Note note) {
        return songContext.getVisibleAreaBounds().inRangeX(note.getStart());
    }

    @Override
    public KarediActions handles() {
        return SELECT_PREVIOUS;
    }
}

