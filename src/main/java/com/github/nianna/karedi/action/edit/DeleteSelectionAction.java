package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.DeleteNotesCommand;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.IntBounded;

class DeleteSelectionAction extends NewKarediAction {
    private final KarediActions handledAction;
    private final boolean keepLyrics;
    private final NoteSelection selection;
    private final CommandExecutor commandExecutor;
    private final DisplayContext displayContext;

    DeleteSelectionAction(KarediActions handledAction, boolean keepLyrics, NoteSelection selection, CommandExecutor commandExecutor, DisplayContext displayContext) {
        this.handledAction = handledAction;
        this.keepLyrics = keepLyrics;
        this.selection = selection;
        this.commandExecutor = commandExecutor;
        this.displayContext = displayContext;
        setDisabledCondition(this.selection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        commandExecutor.execute(getCommand());
    }

    Command getCommand() {
        Command cmd = new DeleteNotesCommand(selection.get(), keepLyrics);
        IntBounded bounds = BoundingBox.boundsFrom(displayContext.getVisibleAreaBounds());
        return new ChangePostStateCommandDecorator(cmd, (command) -> {
            selection.clear();
            if (displayContext.getActiveLine() != null && !displayContext.getActiveLine().isValid()) {
                displayContext.setActiveLine(null);
                displayContext.setBounds(bounds);
            }
        });
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}