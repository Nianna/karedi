package main.java.com.github.nianna.karedi.action.tag;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import main.java.com.github.nianna.karedi.util.Converter;
import main.java.com.github.nianna.karedi.util.MathUtils;

class SetTagValueFromMarkerPositionAction extends TagAction {

    private final KarediActions handledAction;

    private final TagKey key;

    private final SongPlayer songPlayer;

    private final CommandExecutor commandExecutor;

    private final AppContext appContext; //TODO

    SetTagValueFromMarkerPositionAction(KarediActions handledAction, TagKey key, SongState songState, SongPlayer songPlayer, CommandExecutor commandExecutor, AppContext appContext) {
        super(songState);
        this.key = key;
        this.handledAction = handledAction;
        this.songPlayer = songPlayer;
        this.commandExecutor = commandExecutor;
        this.appContext = appContext;
    }

    @Override
    protected void onAction(ActionEvent event) {
        String value = null;
        if (TagKey.expectsADouble(key)) {
            value = Converter.toString(MathUtils.msToSeconds(songPlayer.getMarkerTime()));
        } else {
            if (TagKey.expectsAnInteger(key)) {
                value = Converter.toString(songPlayer.getMarkerTime());
            }
        }
        if (value != null) {
            commandExecutor.execute(new ChangeTagValueCommand(appContext.getSong(), key, value));
        }
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}
