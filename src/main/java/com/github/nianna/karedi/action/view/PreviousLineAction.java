package main.java.com.github.nianna.karedi.action.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.SongLine;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.*;

@Component
class PreviousLineAction extends NewKarediAction {

    private final DisplayContext displayContext;
    private final NoteSelection noteSelection;
    private final SongPlayer songPlayer; //TODO remove

    PreviousLineAction(DisplayContext displayContext, NoteSelection noteSelection, SongPlayer songPlayer) {
        this.displayContext = displayContext;
        this.noteSelection = noteSelection;
        this.songPlayer = songPlayer;
        setDisabledCondition(Bindings.createBooleanBinding(
                () -> this.displayContext.getActiveTrack() == null || !computePreviousLine().isPresent(),
                displayContext.activeTrackProperty(), displayContext.activeLineProperty(), this.songPlayer.markerBeatProperty())
        );
    }

    @Override
    protected void onAction(ActionEvent event) {
        computePreviousLine().ifPresent(displayContext::setActiveLine);
    }

    private Optional<SongLine> computePreviousLine() {
        if (displayContext.getActiveLine() != null) {
            return displayContext.getActiveLine().getPrevious();
        } else {
            SongLine previousLine = noteSelection.getFirst()
                    .map(Note::getLine)
                    .orElse(displayContext.getActiveTrack()
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
