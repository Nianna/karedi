package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.DeleteNotesCommand;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.IntBounded;

class DeleteSelectionAction extends NewKarediAction {
    private final KarediActions handledAction;
    private final boolean keepLyrics;
    private final NoteSelection selection;
    private final CommandExecutor commandExecutor;
    private final VisibleArea visibleArea;
    private final SongContext songContext;

    DeleteSelectionAction(KarediActions handledAction, boolean keepLyrics, NoteSelection selection, CommandExecutor commandExecutor, VisibleArea visibleArea, SongContext songContext) {
        this.handledAction = handledAction;
        this.keepLyrics = keepLyrics;
        this.selection = selection;
        this.commandExecutor = commandExecutor;
        this.visibleArea = visibleArea;
        this.songContext = songContext;
        setDisabledCondition(this.selection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        commandExecutor.execute(getCommand());
    }

    Command getCommand() {
        Command cmd = new DeleteNotesCommand(selection.get(), keepLyrics);
        IntBounded bounds = BoundingBox.boundsFrom(visibleArea);
        return new ChangePostStateCommandDecorator(cmd, (command) -> {
            selection.clear();
            if (songContext.getActiveLine() != null && !songContext.getActiveLine().isValid()) {
                songContext.setActiveLine(null);
                visibleArea.setBounds(bounds);//TODO
            }
        });
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}