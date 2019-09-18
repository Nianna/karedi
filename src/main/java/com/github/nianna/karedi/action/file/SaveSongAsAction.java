package main.java.com.github.nianna.karedi.action.file;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongContext;
import org.springframework.stereotype.Component;

import java.io.File;

import static main.java.com.github.nianna.karedi.action.KarediActions.SAVE_AS;

@Component
class SaveSongAsAction extends NewKarediAction {

    private AppContext appContext;

    SaveSongAsAction(SongContext songContext, AppContext appContext) {
        this.appContext = appContext;
        setDisabledCondition(songContext.activeSongIsNullProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        File file = KarediApp.getInstance().getTxtFileToSave();
        if (appContext.saveSongToFile(file)) {
            appContext.setActiveFile(file);
        }
    }

    @Override
    public KarediActions handles() {
        return SAVE_AS;
    }
}
