package main.java.com.github.nianna.karedi.action.file;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import org.springframework.stereotype.Component;

import java.io.File;

import static main.java.com.github.nianna.karedi.action.KarediActions.SAVE_AS;

@Component
class SaveSongAsAction extends KarediAction {

    private AppContext appContext;

    SaveSongAsAction(DisplayContext displayContext, AppContext appContext) {
        this.appContext = appContext;
        setDisabledCondition(displayContext.activeSongIsNullProperty());
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

