package main.java.com.github.nianna.karedi.action.file;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.EXIT;

@Component
class ExitAction extends NewKarediAction {

    @Override
    protected void onAction(ActionEvent event) {
        KarediApp.getInstance().exit(event);
    }

    @Override
    public KarediActions handles() {
        return EXIT;
    }
}