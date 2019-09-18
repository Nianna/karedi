package main.java.com.github.nianna.karedi.action.play;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.TOGGLE_TICKS;

@Component
class ToggleTicksAction extends KarediAction {

    private final SongPlayer songPlayer;

    ToggleTicksAction(SongPlayer songPlayer) {
        this.songPlayer = songPlayer;
        setSelected(this.songPlayer.isTickingEnabled());
    }

    @Override
    protected void onAction(ActionEvent event) {
        songPlayer.setTickingEnabled(!songPlayer.isTickingEnabled());
    }

    @Override
    public KarediActions handles() {
        return TOGGLE_TICKS;
    }
}