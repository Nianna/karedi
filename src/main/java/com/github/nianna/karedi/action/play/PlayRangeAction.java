package main.java.com.github.nianna.karedi.action.play;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.audio.Player;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;

class PlayRangeAction extends KarediAction {
    private final KarediActions handledAction;
    private final Player.Mode mode;
    private final ObservableValue<? extends Number> from;
    private final ObservableValue<? extends Number> to;
    private final SongPlayer songPlayer;

    PlayRangeAction(KarediActions handledAction, Player.Mode mode, ObservableValue<? extends Number> from, ObservableValue<? extends Number> to, DisplayContext displayContext, SongPlayer songPlayer) {
        this.handledAction = handledAction;
        this.mode = mode;
            this.from = from;
            this.to = to;
        this.songPlayer = songPlayer;

        BooleanBinding condition = displayContext.activeSongIsNullProperty();
            if (mode != Player.Mode.MIDI_ONLY) {
                condition = condition.or(songPlayer.activeAudioIsNullProperty());
            }
            setDisabledCondition(condition);
        }

        @Override
        protected void onAction(ActionEvent event) {
            songPlayer.play(from.getValue().intValue(), to.getValue().intValue(), mode);
        }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}

