package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.song.Song;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.VIEW_MEDLEY;

@Component
class ViewMedleyAction extends NewKarediAction {

    private final SongContext songContext;

    ViewMedleyAction(SongContext songContext) {
        this.songContext = songContext;
        setDisabledCondition(true);
        this.songContext.activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null) {
                setDisabledCondition(true);
            } else {
                setDisabledCondition(newVal.getMedley().sizeProperty().lessThanOrEqualTo(0));
            }
        });
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song.Medley medley = songContext.getActiveSong().getMedley();
        songContext.setVisibleAreaXBounds(medley.getStartBeat(), medley.getEndBeat());
        songContext.assertAllNeededTonesVisible();
    }

    @Override
    public KarediActions handles() {
        return VIEW_MEDLEY;
    }
}

