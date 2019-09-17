package main.java.com.github.nianna.karedi.action.play;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.audio.Player;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongContext;

class PlayRangeAction extends NewKarediAction {
    private final KarediActions handledAction;
    private final Player.Mode mode;
    private final ObservableValue<? extends Number> from;
    private final ObservableValue<? extends Number> to;
    private final AppContext appContext;

    PlayRangeAction(KarediActions handledAction, Player.Mode mode, ObservableValue<? extends Number> from, ObservableValue<? extends Number> to, SongContext songContext, AppContext appContext) {
        this.handledAction = handledAction;
        this.mode = mode;
            this.from = from;
            this.to = to;
        this.appContext = appContext;

        BooleanBinding condition = songContext.activeSongIsNullProperty();
            if (mode != Player.Mode.MIDI_ONLY) {
                condition = condition.or(this.appContext.activeAudioIsNullProperty());
            }
            setDisabledCondition(condition);
        }

        @Override
        protected void onAction(ActionEvent event) {
            appContext.playRange(from.getValue().intValue(), to.getValue().intValue(), mode);
        }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}

