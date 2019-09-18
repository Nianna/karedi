package main.java.com.github.nianna.karedi.guard;

import javafx.beans.Observable;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import org.springframework.stereotype.Component;

@Component
public class TonesVisibleDuringPlaybackGuard implements Guard {

    private final SongPlayer songPlayer;

    private final VisibleArea visibleArea;

    public TonesVisibleDuringPlaybackGuard(SongPlayer songPlayer, VisibleArea visibleArea) {
        this.songPlayer = songPlayer;
        this.visibleArea = visibleArea;
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
            visibleArea.assertBoundsYVisible(visibleArea.addMargins(new BoundingBox<>(playback.getNotes()))); //TODO assert tones visible - will be reused
        }
    }
}
