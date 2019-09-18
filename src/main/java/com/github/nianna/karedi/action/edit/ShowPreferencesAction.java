package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.Settings;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.dialog.PreferencesDialog;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.SHOW_PREFERENCES;

@Component
class ShowPreferencesAction extends KarediAction {

    ShowPreferencesAction() {
        setDisabledCondition(false);
    }

    @Override
    protected void onAction(ActionEvent event) {
        PreferencesDialog dialog = new PreferencesDialog();
        dialog.showAndWait().filter(locale -> locale != I18N.getCurrentLocale())
                .ifPresent(Settings::setLocale);
    }

    @Override
    public KarediActions handles() {
        return SHOW_PREFERENCES;
    }
}
