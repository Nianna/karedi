package main.java.com.github.nianna.karedi.action.play;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.audio.Player;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;

abstract class PlayAuxiliaryNoteAction extends KarediAction {
    protected final NoteSelection selection;
    private final SongPlayer player;
    private final DisplayContext displayContext;
    private final BeatMillisConverter beatMillisConverter;
    private int oldLowerBound;
    private int oldUpperBound;
    private ChangeListener<? super Player.Status> statusListener;

    PlayAuxiliaryNoteAction(NoteSelection selection, SongPlayer player, DisplayContext displayContext, BeatMillisConverter beatMillisConverter) {
        this.selection = selection;
        this.player = player;
        this.displayContext = displayContext;
        this.beatMillisConverter = beatMillisConverter;
        setDisabledCondition(this.selection.isEmptyProperty().or(player.activeAudioIsNullProperty()));
        statusListener = (obs, oldStatus, newStatus) -> {
            if (oldStatus == Player.Status.PLAYING && newStatus == Player.Status.READY) {
                this.displayContext.setVisibleAreaXBounds(oldLowerBound, oldUpperBound, false);
                obs.removeListener(statusListener);
            }
        };
    }

    @Override
    protected void onAction(ActionEvent event) {
        player.stop();

        int auxiliaryNoteStartBeat = getAuxiliaryNoteStartBeat();
        int auxiliaryNoteEndBeat = auxiliaryNoteStartBeat + getAuxiliaryNoteLength();
        adjustVisibleArea(auxiliaryNoteStartBeat, auxiliaryNoteEndBeat);
        player.play(beatMillisConverter.beatToMillis(auxiliaryNoteStartBeat),
                beatMillisConverter.beatToMillis(auxiliaryNoteEndBeat), null, Player.Mode.AUDIO_ONLY);
    }

    private void adjustVisibleArea(int auxiliaryNoteStartBeat, int auxiliaryNoteEndBeat) {
        oldLowerBound = displayContext.getVisibleAreaBounds().getLowerXBound();
        oldUpperBound = displayContext.getVisibleAreaBounds().getUpperXBound();
        int newLowerBound = Math.min(oldLowerBound, auxiliaryNoteStartBeat);
        int newUpperBound = Math.max(oldUpperBound, auxiliaryNoteEndBeat);
        if (newLowerBound != oldLowerBound || newUpperBound != oldUpperBound) {
            displayContext.setVisibleAreaXBounds(newLowerBound, newUpperBound, false);
            player.statusProperty().addListener(statusListener);
        }
    }

    protected int getAuxiliaryNoteLength() {
        return (int) (beatMillisConverter.getBpm() / 100) + 1;
    }

    protected abstract int getAuxiliaryNoteStartBeat();
}