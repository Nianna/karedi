package main.java.com.github.nianna.karedi.action.play;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.audio.Player;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;

class PlaySelectionAction extends NewKarediAction {
    private final KarediActions handledAction;
    private final Player.Mode mode;
    private final NoteSelection selection;
    private final SongPlayer songPlayer;
    private final BeatMillisConverter beatMillisConverter;

    PlaySelectionAction(KarediActions handledAction, Player.Mode mode, NoteSelection selection, SongPlayer songPlayer, BeatMillisConverter beatMillisConverter) {
        this.handledAction = handledAction;
        this.mode = mode;
        this.selection = selection;
        this.songPlayer = songPlayer;
        this.beatMillisConverter = beatMillisConverter;
        BooleanBinding condition = this.selection.isEmptyProperty();
        if (mode != Player.Mode.MIDI_ONLY) {
            condition = condition.or(this.songPlayer.activeAudioIsNullProperty());
        }
        setDisabledCondition(condition);
    }

    @Override
    protected void onAction(ActionEvent event) {
        playSelection(mode);
    }

    private void playSelection(Player.Mode mode) {
        IntBounded selectionBounds = selection.getSelectionBounds();
        if (selection.size() > 0 && selectionBounds.isValid()) {
            long startMillis = beatMillisConverter.beatToMillis(selectionBounds.getLowerXBound());
            long endMillis = beatMillisConverter.beatToMillis(selectionBounds.getUpperXBound());
            songPlayer.play(startMillis, endMillis, selection.get(), mode);
        }
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}

