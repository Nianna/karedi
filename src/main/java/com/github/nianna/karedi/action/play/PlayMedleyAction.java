package main.java.com.github.nianna.karedi.action.play;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.audio.Player;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.song.Song;

class PlayMedleyAction extends NewKarediAction {
    private final KarediActions handledAction;
    private final Player.Mode mode;
    private final AppContext appContext;
    private final NoteSelection selection;
    private BooleanBinding basicCondition;
    private Song.Medley medley;

    PlayMedleyAction(KarediActions handledAction, Player.Mode mode, SongContext songContext, AppContext appContext, NoteSelection selection) {
        this.handledAction = handledAction;
        this.mode = mode;
        this.appContext = appContext;
        this.selection = selection;

        basicCondition = songContext.activeSongIsNullProperty();
        if (mode != Player.Mode.MIDI_ONLY) {
            basicCondition = basicCondition.or(this.appContext.activeAudioIsNullProperty());
        }
        setDisabledCondition(basicCondition);
        songContext.activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null) {
                setDisabledCondition(basicCondition);
            } else {
                medley = newVal.getMedley();
                setDisabledCondition(
                        basicCondition.or(medley.sizeProperty().lessThanOrEqualTo(0)));
            }
        });
    }

    @Override
    protected void onAction(ActionEvent event) {
        selection.clear();
        appContext.playRange(medley.getStartBeat(), medley.getEndBeat(), mode);
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}