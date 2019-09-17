package main.java.com.github.nianna.karedi.action.play;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.audio.Player;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.STOP_PLAYBACK;

@Component
class StopPlaybackAction extends NewKarediAction {

    private final SongPlayer songPlayer;

    private StopPlaybackAction(SongPlayer songPlayer) {
        this.songPlayer = songPlayer;
        setDisabledCondition(this.songPlayer.statusProperty().isNotEqualTo(Player.Status.PLAYING));
    }

    @Override
    protected void onAction(ActionEvent event) {
        // player.stop();
        songPlayer.setMarkerTime(songPlayer.getMarkerTime());
    }

    @Override
    public KarediActions handles() {
        return STOP_PLAYBACK;
    }
}