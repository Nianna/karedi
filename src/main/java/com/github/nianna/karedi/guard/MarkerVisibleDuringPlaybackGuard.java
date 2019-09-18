package main.java.com.github.nianna.karedi.guard;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import main.java.com.github.nianna.karedi.audio.Player;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.region.IntBounded;
import org.springframework.stereotype.Component;

@Component
public class MarkerVisibleDuringPlaybackGuard implements Guard {

    private final SongPlayer songPlayer;

    private final DisplayContext displayContext;

    private final InvalidationListener markerPositionChangeListener = this::onMarkerPositionWhilePlayingChanged;

    public MarkerVisibleDuringPlaybackGuard(SongPlayer songPlayer, DisplayContext displayContext) {
        this.songPlayer = songPlayer;
        this.displayContext = displayContext;
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
        IntBounded visibleAreaBounds = displayContext.getVisibleAreaBounds();
        if (!visibleAreaBounds.inBoundsX(markerBeat)) {
            int xRange = visibleAreaBounds.getUpperXBound() - visibleAreaBounds.getLowerXBound();
            displayContext.setVisibleAreaXBounds(markerBeat - 1, markerBeat - 1 + xRange);
        }
    }
}
