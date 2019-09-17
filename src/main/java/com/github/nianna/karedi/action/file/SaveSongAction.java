package main.java.com.github.nianna.karedi.action.file;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.ActionManager;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongContext;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.SAVE;

@Component
class SaveSongAction extends NewKarediAction {

    private final AppContext appContext;
    private final ActionManager actionManager;

    SaveSongAction(AppContext appContext, SongContext songContext, ActionManager actionManager) {
        this.appContext = appContext;
        this.actionManager = actionManager;
        setDisabledCondition(songContext.activeSongIsNullProperty().or(appContext.hasNoChangesProperty()));
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (appContext.getActiveFile() != null) {
            appContext.saveSongToFile(appContext.getActiveFile());
        } else {
            actionManager.execute(KarediActions.SAVE_AS);
        }
    }

    @Override
    public KarediActions handles() {
        return SAVE;
    }
}
