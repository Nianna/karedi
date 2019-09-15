package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import org.springframework.stereotype.Component;

import java.util.List;

import static main.java.com.github.nianna.karedi.action.KarediActions.VIEW_MEDLEY;

@Component
class ViewMedleyAction extends NewKarediAction {

    private final SongContext songContext;
    private final VisibleArea visibleArea;

    ViewMedleyAction(SongContext songContext, VisibleArea visibleArea) {
        this.songContext = songContext;
        this.visibleArea = visibleArea;
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
        setVisibleAreaXBounds(medley.getStartBeat(), medley.getEndBeat());
        assertAllNeededTonesVisible();
    }

    private void assertAllNeededTonesVisible() { //TODO refactor
        List<Note> notes = songContext.getActiveSong().getVisibleNotes(visibleArea.getLowerXBound(), visibleArea.getUpperXBound());
        visibleArea.assertBoundsYVisible(visibleArea.addMargins(new BoundingBox<>(notes)));
    }

    private void setVisibleAreaXBounds(int lowerXBound, int upperXBound) {
        if (visibleArea.setXBounds(lowerXBound, upperXBound)) {
            songContext.setActiveLine(null);
        }
    }

    @Override
    public KarediActions handles() {
        return VIEW_MEDLEY;
    }
}

