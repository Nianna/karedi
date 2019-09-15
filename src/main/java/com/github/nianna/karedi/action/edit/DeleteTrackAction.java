package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.track.DeleteTrackCommand;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongState;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.DELETE_TRACK;

@Component
class DeleteTrackAction extends NewKarediAction {

    private final CommandExecutor commandExecutor;
    private final SongState songState;
    private final AppContext appContext;

    private DeleteTrackAction(CommandExecutor commandExecutor, SongState songState, AppContext appContext) {
        this.commandExecutor = commandExecutor;
        this.songState = songState;
        this.appContext = appContext;
        setDisabledCondition(appContext.activeSongHasOneOrZeroTracksProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        commandExecutor.execute(new DeleteTrackCommand(appContext.getSong(), songState.getActiveTrack()));
    }

    @Override
    public KarediActions handles() {
        return DELETE_TRACK;
    }
}

