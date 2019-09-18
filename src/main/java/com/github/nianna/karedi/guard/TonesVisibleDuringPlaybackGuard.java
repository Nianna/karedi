package main.java.com.github.nianna.karedi.guard;

import javafx.beans.Observable;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import org.springframework.stereotype.Component;

@Component
public class TonesVisibleDuringPlaybackGuard implements Guard {

    private final SongPlayer songPlayer;

    private final DisplayContext displayContext;

    public TonesVisibleDuringPlaybackGuard(SongPlayer songPlayer, DisplayContext displayContext) {
        this.songPlayer = songPlayer;
        this.displayContext = displayContext;
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
            displayContext.assertTonesVisible(playback.getNotes());
        }
    }
}
