package main.java.com.github.nianna.karedi.action.edit;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.AddNoteCommand;
import main.java.com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.SongLine;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static main.java.com.github.nianna.karedi.action.KarediActions.ADD_NOTE;

@Component
 class AddNoteAction extends NewKarediAction { //TODO refactor this class
    private static final int NEW_NOTE_DEFAULT_LENGTH = 3;
    private final SongContext songContext;
    private final SongPlayer songPlayer; //TODO
    private final NoteSelection selection;
    private final CommandExecutor commandExecutor;

    AddNoteAction(SongContext songContext, SongPlayer songPlayer, NoteSelection selection, CommandExecutor commandExecutor) {
        this.songContext = songContext;
        this.songPlayer = songPlayer;
        this.selection = selection;
        this.commandExecutor = commandExecutor;
        setDisabledCondition(Bindings.createBooleanBinding(() -> {
            if (this.songContext.getActiveTrack() == null) {
                return true;
            } else {
                int newNotePosition = computePosition();
                return songContext.getActiveTrack().noteAt(newNotePosition).isPresent();
            }
        }, this.selection.getSelectionBounds(), this.songPlayer.markerTimeProperty(), songContext.activeTrackProperty()));
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
                .orElseGet(() -> new AddNoteCommand(note, songContext.getActiveTrack()));
        commandExecutor.execute(new ChangePostStateCommandDecorator(cmd, (command) -> {
            selection.selectOnly(note);
        }));
    }

    private int computePosition() {
        if (selection.size() > 0 && selection.getSelectionBounds().isValid()) {
            return selection.getSelectionBounds().getUpperXBound();
        } else {
            return songPlayer.getMarkerBeat();
        }
    }

    private Optional<Integer> computeTone(SongLine line, int beat) {
        return line.noteAtOrEarlier(beat).map(Note::getTone);
    }

    private int computeLength(int startBeat) {
        Optional<Integer> nextNoteStartBeat = songContext.getActiveTrack().noteAtOrLater(startBeat)
                .map(Note::getStart);
        return nextNoteStartBeat.
                map(integer -> Math.min(NEW_NOTE_DEFAULT_LENGTH, Math.max(integer - startBeat - 1, 1)))
                .orElse(NEW_NOTE_DEFAULT_LENGTH);
    }

    private Optional<SongLine> computeLine() {
        if (songContext.getActiveLine() != null) {
            return Optional.of(songContext.getActiveLine());
        }
        Optional<SongLine> line = selection.getLast().map(Note::getLine);
        if (!line.isPresent()) {
            line = getLastVisibleLineBeforeMarker();
        }
        return line;
    }

    private Optional<SongLine> getLastVisibleLineBeforeMarker() {
        return songContext.getActiveTrack().lineAtOrEarlier(songPlayer.getMarkerBeat())
                .filter(prevLine -> prevLine.getUpperXBound() > songContext.getVisibleAreaBounds().getLowerXBound());
    }

    @Override
    public KarediActions handles() {
        return ADD_NOTE;
    }
}
