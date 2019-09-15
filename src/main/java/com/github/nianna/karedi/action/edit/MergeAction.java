package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.MergeNotesCommand;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.parser.Parser;
import main.java.com.github.nianna.karedi.song.Song;

class MergeAction extends ClipboardAction {
    private final MergeNotesCommand.MergeMode mode;
    private final KarediActions handledAction;
    private final NoteSelection selection;
    private final CommandExecutor commandExecutor;

    MergeAction(KarediActions handledAction, MergeNotesCommand.MergeMode mode, Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        super(parser);
        this.handledAction = handledAction;
        this.mode = mode;
        this.selection = noteSelection;
        this.commandExecutor = commandExecutor;
        setDisabledCondition(this.selection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song pastedSong = buildSong(getLinesFromClipboard());
        if (pastedSong != null && pastedSong.size() > 0) {
            commandExecutor.execute(new MergeNotesCommand(selection.get(), pastedSong.get(0).getNotes(), mode));
        }
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}
