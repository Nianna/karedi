package main.java.com.github.nianna.karedi.action.play;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.audio.Player;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.song.Song;

class PlayMedleyAction extends NewKarediAction {
    private final KarediActions handledAction;
    private final Player.Mode mode;
    private final NoteSelection selection;
    private final SongPlayer songPlayer;
    private BooleanBinding basicCondition;
    private Song.Medley medley;

    PlayMedleyAction(KarediActions handledAction, Player.Mode mode, DisplayContext displayContext, NoteSelection selection, SongPlayer songPlayer) {
        this.handledAction = handledAction;
        this.mode = mode;
        this.selection = selection;
        this.songPlayer = songPlayer;

        basicCondition = displayContext.activeSongIsNullProperty();
        if (mode != Player.Mode.MIDI_ONLY) {
            basicCondition = basicCondition.or(songPlayer.activeAudioIsNullProperty());
        }
        setDisabledCondition(basicCondition);
        displayContext.activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
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
        songPlayer.play(medley.getStartBeat(), medley.getEndBeat(), mode);
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}