package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.track.AddTrackCommand;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongContext;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.ADD_TRACK;

@Component
class AddTrackAction extends NewKarediAction {

    private final SongContext songContext;
    private final CommandExecutor commandExecutor;
    private final AppContext appContext; //TODO

    private AddTrackAction(SongContext songContext, CommandExecutor commandExecutor, AppContext appContext) {
        this.songContext = songContext;
        this.commandExecutor = commandExecutor;
        this.appContext = appContext;
        setDisabledCondition(this.songContext.activeSongIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Command cmd = new AddTrackCommand(appContext.getSong());
        commandExecutor.execute(new ChangePostStateCommandDecorator(cmd, c -> {
            appContext.getSong().getLastTrack().ifPresent(songContext::setActiveTrack);
        }));
    }

    @Override
    public KarediActions handles() {
        return ADD_TRACK;
    }
}