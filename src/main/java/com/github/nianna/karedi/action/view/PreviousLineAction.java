package main.java.com.github.nianna.karedi.action.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.SongLine;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.*;

@Component
class PreviousLineAction extends NewKarediAction {

    private final SongContext songContext;
    private final NoteSelection noteSelection;
    private final SongPlayer songPlayer; //TODO remove

    PreviousLineAction(SongContext songContext, NoteSelection noteSelection, SongPlayer songPlayer) {
        this.songContext = songContext;
        this.noteSelection = noteSelection;
        this.songPlayer = songPlayer;
        setDisabledCondition(Bindings.createBooleanBinding(
                () -> this.songContext.getActiveTrack() == null || !computePreviousLine().isPresent(),
                songContext.activeTrackProperty(), songContext.activeLineProperty(), this.songPlayer.markerBeatProperty())
        );
    }

    @Override
    protected void onAction(ActionEvent event) {
        computePreviousLine().ifPresent(songContext::setActiveLine);
    }

    private Optional<SongLine> computePreviousLine() {
        if (songContext.getActiveLine() != null) {
            return songContext.getActiveLine().getPrevious();
        } else {
            SongLine previousLine = noteSelection.getFirst()
                    .map(Note::getLine)
                    .orElse(songContext.getActiveTrack()
                                    .lineAtOrEarlier(songPlayer.getMarkerBeat())
                                    .orElse(null)
                    );
            return Optional.ofNullable(previousLine);
        }
    }

    @Override
    public KarediActions handles() {
        return VIEW_PREVIOUS_LINE;
    }
}
