package main.java.com.github.nianna.karedi.action.tag;

import javafx.event.ActionEvent;
import javafx.stage.Modality;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.tag.ChangeMedleyCommand;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.dialog.EditMedleyDialog;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.EDIT_MEDLEY;

@Component
class EditMedleyAction extends TagAction {

    private final AppContext appContext;

    private final CommandExecutor commandExecutor;

    EditMedleyAction(SongState songState, AppContext appContext, CommandExecutor commandExecutor) {
        super(songState);
        this.appContext = appContext;
        this.commandExecutor = commandExecutor;
    }

    @Override
    protected void onAction(ActionEvent event) {
        EditMedleyDialog dialog = new EditMedleyDialog();

        getSong().getTagValue(TagKey.MEDLEYSTARTBEAT).ifPresent(dialog::setStartBeat);
        getSong().getTagValue(TagKey.MEDLEYENDBEAT).ifPresent(dialog::setEndBeat);
        dialog.initModality(Modality.NONE);
        dialog.show();

        dialog.resultProperty().addListener(obs -> {
            Song.Medley medley = dialog.getResult();
            if (medley != null) {
                commandExecutor.execute(new ChangeMedleyCommand(getSong(), medley.getStartBeat(), medley.getEndBeat()));
            }
        });
    }

    private Song getSong() {
        return appContext.getSong();
    }

    @Override
    public KarediActions handles() {
        return EDIT_MEDLEY;
    }
}
