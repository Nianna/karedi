package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.Song;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.VIEW_PREVIOUS_TRACK;

@Component
class PreviousTrackAction extends NewKarediAction {

    private final DisplayContext displayContext;

    PreviousTrackAction(DisplayContext displayContext) {
        this.displayContext = displayContext;
        setDisabledCondition(displayContext.activeSongHasOneOrZeroTracksProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song song = displayContext.getActiveSong();
        int prevIndex = (song.indexOf(displayContext.getActiveTrack()) + song.size() - 1) % song.size();
        displayContext.setActiveTrack(song.get(prevIndex));
    }

    @Override
    public KarediActions handles() {
        return VIEW_PREVIOUS_TRACK;
    }
}