package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.track.DeleteTrackCommand;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.DELETE_TRACK;

@Component
class DeleteTrackAction extends KarediAction {

    private final CommandExecutor commandExecutor;
    private final DisplayContext displayContext;

    private DeleteTrackAction(CommandExecutor commandExecutor, DisplayContext displayContext) {
        this.commandExecutor = commandExecutor;
        this.displayContext = displayContext;
        setDisabledCondition(displayContext.activeSongHasOneOrZeroTracksProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        commandExecutor.execute(new DeleteTrackCommand(displayContext.getActiveSong(), displayContext.getActiveTrack()));
    }

    @Override
    public KarediActions handles() {
        return DELETE_TRACK;
    }
}

