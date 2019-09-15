package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.song.Song;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.VIEW_NEXT_TRACK;

@Component
class NextTrackAction extends NewKarediAction {

    private final SongContext songContext;

    NextTrackAction(SongContext songContext) {
        this.songContext = songContext;
        setDisabledCondition(songContext.activeSongHasOneOrZeroTracksProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song song = songContext.getActiveSong();
        int nextIndex = (song.indexOf(songContext.getActiveTrack()) + 1) % song.size();
        songContext.setActiveTrack(song.get(nextIndex));
    }

    @Override
    public KarediActions handles() {
        return VIEW_NEXT_TRACK;
    }
}