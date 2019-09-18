package main.java.com.github.nianna.karedi.action.view;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.SWITCH_MODE;

@Component
public class SwitchViewModeAction extends KarediAction {

    private final ReadOnlyObjectWrapper<KarediApp.ViewMode> activeViewMode; //TODO

    SwitchViewModeAction() {
        this.activeViewMode =  new ReadOnlyObjectWrapper<>(KarediApp.ViewMode.DAY);
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (KarediApp.getInstance().getViewMode() == KarediApp.ViewMode.DAY) {
            KarediApp.getInstance().setViewMode(KarediApp.ViewMode.NIGHT);
        } else {
            KarediApp.getInstance().setViewMode(KarediApp.ViewMode.DAY);
        }
        activeViewMode.set(KarediApp.getInstance().getViewMode());
    }

    @Override
    public KarediActions handles() {
        return SWITCH_MODE;
    }

    public ReadOnlyObjectProperty<KarediApp.ViewMode> activeViewModeProperty() {
        return activeViewMode.getReadOnlyProperty();
    }

    public KarediApp.ViewMode getActiveViewMode() {
        return activeViewMode.get();
    }
}
