package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.Song;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.VIEW_NEXT_TRACK;

@Component
class NextTrackAction extends KarediAction {

    private final DisplayContext displayContext;

    NextTrackAction(DisplayContext displayContext) {
        this.displayContext = displayContext;
        setDisabledCondition(displayContext.activeSongHasOneOrZeroTracksProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song song = displayContext.getActiveSong();
        int nextIndex = (song.indexOf(displayContext.getActiveTrack()) + 1) % song.size();
        displayContext.setActiveTrack(song.get(nextIndex));
    }

    @Override
    public KarediActions handles() {
        return VIEW_NEXT_TRACK;
    }
}