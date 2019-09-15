package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.command.*;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.parser.Parser;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static main.java.com.github.nianna.karedi.action.KarediActions.PASTE;

@Component
class PasteAction extends ClipboardAction {

    private final SongState songState;
    private final NoteSelection selection;
    private final CommandExecutor commandExecutor;
    private final SongPlayer songPlayer;

    PasteAction(Parser parser, SongState songState, NoteSelection selection, CommandExecutor commandExecutor, SongPlayer songPlayer) {
        super(parser);
        this.songState = songState;
        this.selection = selection;
        this.commandExecutor = commandExecutor;
        this.songPlayer = songPlayer;
        setDisabledCondition(this.songState.activeTrackIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song pastedSong = buildSong(getLinesFromClipboard());
        List<Note> notesToSelect = new ArrayList<>();
        if (pastedSong.size() > 0) {
            notesToSelect.addAll(pastedSong.get(0).getNotes());
        }
        Command cmd = new CommandComposite(I18N.get("common.paste")) {
            @Override
            protected void buildSubCommands() {
                addSubCommand(new DeleteNotesCommand(selection.get(), false));
                addSubCommand(new PasteCommand(songState.getActiveTrack(), pastedSong, songPlayer.getMarkerBeat()));
            }
        };
        commandExecutor.execute(new ChangePostStateCommandDecorator(cmd, c -> {
            selection.set(notesToSelect);
        }));
    }

    @Override
    public KarediActions handles() {
        return PASTE;
    }
}