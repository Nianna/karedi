package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.track.DeleteTrackCommand;
import main.java.com.github.nianna.karedi.context.SongContext;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.DELETE_TRACK;

@Component
class DeleteTrackAction extends NewKarediAction {

    private final CommandExecutor commandExecutor;
    private final SongContext songContext;

    private DeleteTrackAction(CommandExecutor commandExecutor, SongContext songContext) {
        this.commandExecutor = commandExecutor;
        this.songContext = songContext;
        setDisabledCondition(songContext.activeSongHasOneOrZeroTracksProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        commandExecutor.execute(new DeleteTrackCommand(songContext.getActiveSong(), songContext.getActiveTrack()));
    }

    @Override
    public KarediActions handles() {
        return DELETE_TRACK;
    }
}

