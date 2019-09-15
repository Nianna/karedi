package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.song.Song;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.VIEW_PREVIOUS_TRACK;

@Component
class PreviousTrackAction extends NewKarediAction {

    private final SongState songState;
    private final AppContext appContext; //TODO remove

    PreviousTrackAction(SongState songState, AppContext appContext) {
        this.songState = songState;
        this.appContext = appContext;
        setDisabledCondition(appContext.activeSongHasOneOrZeroTracksProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song song = appContext.getSong();
        int prevIndex = (song.indexOf(songState.getActiveTrack()) + song.size() - 1) % song.size();
        songState.setActiveTrack(song.get(prevIndex));
    }

    @Override
    public KarediActions handles() {
        return VIEW_PREVIOUS_TRACK;
    }
}