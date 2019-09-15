package main.java.com.github.nianna.karedi.action.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.SongLine;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.VIEW_NEXT_LINE;

@Component
class NextLineAction extends NewKarediAction {

    private final SongState songState;
    private final NoteSelection selection;
    private final SongPlayer songPlayer; //TODO remove

    NextLineAction(SongState songState, NoteSelection selection, SongPlayer songPlayer) {
        this.songState = songState;
        this.selection = selection;
        this.songPlayer = songPlayer;
        setDisabledCondition(Bindings.createBooleanBinding(
                () -> this.songState.getActiveTrack() == null || !computeNextLine().isPresent(),
                songState.activeTrackProperty(), songState.activeLineProperty(), songPlayer.markerBeatProperty())
        );
    }

    @Override
    protected void onAction(ActionEvent event) {
        computeNextLine().ifPresent(songState::setActiveLine);
    }

    private Optional<SongLine> computeNextLine() {
        if (songState.getActiveLine() != null) {
            return songState.getActiveLine().getNext();
        } else {
            SongLine nextLine = selection.getLast()
                    .map(Note::getLine)
                    .orElse(songState.getActiveTrack()
                            .lineAtOrLater(songPlayer.getMarkerBeat())
                            .orElse(null)
                    );
            return Optional.ofNullable(nextLine);
        }

    }

    @Override
    public KarediActions handles() {
        return VIEW_NEXT_LINE;
    }
}