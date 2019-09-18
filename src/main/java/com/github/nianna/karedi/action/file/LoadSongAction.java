package main.java.com.github.nianna.karedi.action.file;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import org.springframework.stereotype.Component;

import java.io.File;

import static main.java.com.github.nianna.karedi.action.KarediActions.LOAD;

@Component
class LoadSongAction extends KarediAction {

    private final AppContext appContext;

    LoadSongAction(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (KarediApp.getInstance().saveChangesIfUserWantsTo()) {
            File file = KarediApp.getInstance().getTxtFileToOpen();
            if (file != null) {
                appContext.loadSongFile(file, true);
            }
        }
    }

    @Override
    public KarediActions handles() {
        return LOAD;
    }
}
