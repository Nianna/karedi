package main.java.com.github.nianna.karedi.guard;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import main.java.com.github.nianna.karedi.audio.Player;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import org.springframework.stereotype.Component;

@Component
public class MarkerVisibleDuringPlaybackGuard implements Guard {

    private final SongPlayer songPlayer;

    private final VisibleArea visibleArea;

    private final SongState songState;

    private final InvalidationListener markerPositionChangeListener = this::onMarkerPositionWhilePlayingChanged;

    public MarkerVisibleDuringPlaybackGuard(SongPlayer songPlayer, VisibleArea visibleArea, SongState songState) {
        this.songPlayer = songPlayer;
        this.visibleArea = visibleArea;
        this.songState = songState;
    }

    @Override
    public void enable() {
        songPlayer.statusProperty().addListener(this::onPlayerStatusChanged);
    }

    @Override
    public void disable() {
        songPlayer.statusProperty().removeListener(this::onPlayerStatusChanged);
    }

    private void onPlayerStatusChanged(Observable obs, Player.Status oldStatus, Player.Status newStatus) {
        if (newStatus == Player.Status.PLAYING) {
            songPlayer.markerTimeProperty().addListener(markerPositionChangeListener);
        } else {
            songPlayer.markerTimeProperty().removeListener(markerPositionChangeListener);
        }
    }

    private void onMarkerPositionWhilePlayingChanged(Observable obs) {
        int markerBeat = songPlayer.getMarkerBeat();
        if (!visibleArea.inBoundsX(markerBeat)) {
            int xRange = visibleArea.getUpperXBound() - visibleArea.getLowerXBound();
            visibleArea.setXBounds(markerBeat - 1, markerBeat - 1 + xRange);
            songState.setActiveLine(null);
        }
    }
}
