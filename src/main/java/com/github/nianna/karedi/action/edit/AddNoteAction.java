package main.java.com.github.nianna.karedi.action.edit;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.AddNoteCommand;
import main.java.com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.context.*;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.SongLine;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.ADD_NOTE;

@Component
 class AddNoteAction extends NewKarediAction { //TODO refactor this class
    private static final int NEW_NOTE_DEFAULT_LENGTH = 3;
    private final SongState songState;
    private final SongPlayer songPlayer; //TODO
    private final VisibleArea visibleArea;
    private final NoteSelection selection;
    private final CommandExecutor commandExecutor;
    private final AppContext appContext; //TODO remove

    AddNoteAction(SongState songState, SongPlayer songPlayer, VisibleArea visibleArea, NoteSelection selection, CommandExecutor commandExecutor, AppContext appContext) {
        this.songState = songState;
        this.songPlayer = songPlayer;
        this.visibleArea = visibleArea;
        this.selection = selection;
        this.commandExecutor = commandExecutor;
        this.appContext = appContext;
        setDisabledCondition(Bindings.createBooleanBinding(() -> {
            if (this.songState.getActiveTrack() == null) {
                return true;
            } else {
                int newNotePosition = computePosition();
                return songState.getActiveTrack().noteAt(newNotePosition).isPresent();
            }
        }, this.appContext.getSelectionBounds(), this.songPlayer.markerTimeProperty(), songState.activeTrackProperty()));
    }

    @Override
    protected void onAction(ActionEvent event) {
        int startBeat = computePosition();
        int length = computeLength(startBeat);
        Optional<SongLine> optLine = computeLine();

        int tone = optLine.flatMap(line -> computeTone(line, startBeat)).orElse(0);
        Note note = new Note(startBeat, length, tone);

        Command cmd;
        cmd = optLine
                .map(songLine -> new AddNoteCommand(note, songLine))
                .orElseGet(() -> new AddNoteCommand(note, songState.getActiveTrack()));
        commandExecutor.execute(new ChangePostStateCommandDecorator(cmd, (command) -> {
            selection.selectOnly(note);
        }));
    }

    private int computePosition() {
        if (selection.size() > 0 && appContext.getSelectionBounds().isValid()) {
            return appContext.getSelectionBounds().getUpperXBound();
        } else {
            return songPlayer.getMarkerBeat();
        }
    }

    private Optional<Integer> computeTone(SongLine line, int beat) {
        return line.noteAtOrEarlier(beat).map(Note::getTone);
    }

    private int computeLength(int startBeat) {
        Optional<Integer> nextNoteStartBeat = songState.getActiveTrack().noteAtOrLater(startBeat)
                .map(Note::getStart);
        return nextNoteStartBeat.
                map(integer -> Math.min(NEW_NOTE_DEFAULT_LENGTH, Math.max(integer - startBeat - 1, 1)))
                .orElse(NEW_NOTE_DEFAULT_LENGTH);
    }

    private Optional<SongLine> computeLine() {
        if (songState.getActiveLine() != null) {
            return Optional.of(songState.getActiveLine());
        }
        Optional<SongLine> line = selection.getLast().map(Note::getLine);
        if (!line.isPresent()) {
            line = getLastVisibleLineBeforeMarker();
        }
        return line;
    }

    private Optional<SongLine> getLastVisibleLineBeforeMarker() {
        return songState.getActiveTrack().lineAtOrEarlier(songPlayer.getMarkerBeat())
                .filter(prevLine -> prevLine.getUpperXBound() > visibleArea.getLowerXBound());
    }

    @Override
    public KarediActions handles() {
        return ADD_NOTE;
    }
}
