package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.Song;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.VIEW_MEDLEY;

@Component
class ViewMedleyAction extends KarediAction {

    private final DisplayContext displayContext;

    ViewMedleyAction(DisplayContext displayContext) {
        this.displayContext = displayContext;
        setDisabledCondition(true);
        this.displayContext.activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null) {
                setDisabledCondition(true);
            } else {
                setDisabledCondition(newVal.getMedley().sizeProperty().lessThanOrEqualTo(0));
            }
        });
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song.Medley medley = displayContext.getActiveSong().getMedley();
        displayContext.setVisibleAreaXBounds(medley.getStartBeat(), medley.getEndBeat());
        displayContext.assertAllNeededTonesVisible();
    }

    @Override
    public KarediActions handles() {
        return VIEW_MEDLEY;
    }
}

