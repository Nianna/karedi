package main.java.com.github.nianna.karedi.guard;

import javafx.beans.Observable;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import org.springframework.stereotype.Component;

@Component
public class TonesVisibleDuringPlaybackGuard implements Guard {

    private final SongPlayer songPlayer;

    private final SongContext songContext;

    public TonesVisibleDuringPlaybackGuard(SongPlayer songPlayer, SongContext songContext) {
        this.songPlayer = songPlayer;
        this.songContext = songContext;
    }

    @Override
    public void enable() {
        songPlayer.currentPlaybackProperty().addListener(this::onPlaybackInvalidated);
    }

    @Override
    public void disable() {
        songPlayer.currentPlaybackProperty().removeListener(this::onPlaybackInvalidated);
    }

    private void onPlaybackInvalidated(Observable ignored) {
        SongPlayer.Playback playback = songPlayer.getCurrentPlayback();
        if (playback != null) {
            songContext.assertTonesVisible(playback.getNotes());
        }
    }
}
