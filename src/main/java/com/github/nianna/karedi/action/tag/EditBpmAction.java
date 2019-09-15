package main.java.com.github.nianna.karedi.action.tag;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.tag.ChangeBpmCommand;
import main.java.com.github.nianna.karedi.command.tag.RescaleSongToBpmCommand;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.dialog.EditBpmDialog;
import main.java.com.github.nianna.karedi.dialog.ModifyBpmDialog;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.tag.TagKey;

import java.util.Optional;

class EditBpmAction extends NewKarediAction {
    private final KarediActions handledAction;
    private final AppContext appContext; //TODO
    private final CommandExecutor commandExecutor;
    private double scale;
    private boolean promptUser;

    EditBpmAction(KarediActions handledAction, SongState songState, AppContext appContext, CommandExecutor commandExecutor) {
        this.handledAction = handledAction;
        this.appContext = appContext;
        this.commandExecutor = commandExecutor;
        setDisabledCondition(songState.activeSongIsNullProperty());
        promptUser = true;
    }

    EditBpmAction(KarediActions handledAction, double scale, SongState songState, AppContext appContext, CommandExecutor commandExecutor) {
        this(handledAction, songState, appContext, commandExecutor);
        this.scale = scale;
        promptUser = false;
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (promptUser) {
            double oldBpm = getSong().getBpm();

            ModifyBpmDialog dialog = new EditBpmDialog();
            getSong().getTagValue(TagKey.BPM).ifPresent(dialog::setBpmFieldText);
            Optional<ModifyBpmDialog.BpmEditResult> optionalResult = dialog.showAndWait();
            optionalResult.ifPresent(result -> {
                double newBpm = result.getBpm();
                if (result.shouldRescale()) {
                    execute(new RescaleSongToBpmCommand(getSong(), newBpm / oldBpm));
                } else {
                    execute(new ChangeBpmCommand(getSong(), newBpm));
                }
            });
        } else {
            execute(new RescaleSongToBpmCommand(getSong(), scale));
        }
    }

    private void execute(Command command) {
        commandExecutor.execute(command);
    }

    private Song getSong() {
        return appContext.getSong();
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}
