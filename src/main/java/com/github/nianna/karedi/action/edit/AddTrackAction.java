package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.track.AddTrackCommand;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.ADD_TRACK;

@Component
class AddTrackAction extends KarediAction {

    private final DisplayContext displayContext;
    private final CommandExecutor commandExecutor;

    private AddTrackAction(DisplayContext displayContext, CommandExecutor commandExecutor) {
        this.displayContext = displayContext;
        this.commandExecutor = commandExecutor;
        setDisabledCondition(this.displayContext.activeSongIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Command cmd = new AddTrackCommand(displayContext.getActiveSong());
        commandExecutor.execute(new ChangePostStateCommandDecorator(cmd, c -> {
            displayContext.getActiveSong().getLastTrack().ifPresent(displayContext::setActiveTrack);
        }));
    }

    @Override
    public KarediActions handles() {
        return ADD_TRACK;
    }
}