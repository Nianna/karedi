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

import static main.java.com.github.nianna.karedi.action.KarediActions.VIEW_NEXT_LINE;

@Component
class NextLineAction extends NewKarediAction {

    private final DisplayContext displayContext;
    private final NoteSelection selection;
    private final SongPlayer songPlayer; //TODO remove

    NextLineAction(DisplayContext displayContext, NoteSelection selection, SongPlayer songPlayer) {
        this.displayContext = displayContext;
        this.selection = selection;
        this.songPlayer = songPlayer;
        setDisabledCondition(Bindings.createBooleanBinding(
                () -> this.displayContext.getActiveTrack() == null || !computeNextLine().isPresent(),
                displayContext.activeTrackProperty(), displayContext.activeLineProperty(), songPlayer.markerBeatProperty())
        );
    }

    @Override
    protected void onAction(ActionEvent event) {
        computeNextLine().ifPresent(displayContext::setActiveLine);
    }

    private Optional<SongLine> computeNextLine() {
        if (displayContext.getActiveLine() != null) {
            return displayContext.getActiveLine().getNext();
        } else {
            SongLine nextLine = selection.getLast()
                    .map(Note::getLine)
                    .orElse(displayContext.getActiveTrack()
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