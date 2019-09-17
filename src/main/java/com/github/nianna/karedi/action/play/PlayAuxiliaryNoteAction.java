package main.java.com.github.nianna.karedi.action.play;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.audio.Player;
import main.java.com.github.nianna.karedi.context.*;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;

abstract class PlayAuxiliaryNoteAction extends NewKarediAction {
    protected final NoteSelection selection;
    private final SongPlayer player;
    private final VisibleArea visibleArea;
    private final BeatMillisConverter beatMillisConverter; //TODO should be in songContext?
    private int oldLowerBound;
    private int oldUpperBound;
    private ChangeListener<? super Player.Status> statusListener;

    PlayAuxiliaryNoteAction(NoteSelection selection, AppContext appContext, SongPlayer player, SongContext songContext, VisibleArea visibleArea, BeatMillisConverter beatMillisConverter) {
        this.selection = selection;
        this.player = player;
        this.visibleArea = visibleArea;
        this.beatMillisConverter = beatMillisConverter;
        setDisabledCondition(this.selection.isEmptyProperty().or(appContext.activeAudioIsNullProperty()));
        statusListener = (obs, oldStatus, newStatus) -> {
            if (oldStatus == Player.Status.PLAYING && newStatus == Player.Status.READY) {
                visibleArea.setXBounds(oldLowerBound, oldUpperBound);
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
        oldLowerBound = visibleArea.getLowerXBound();
        oldUpperBound = visibleArea.getUpperXBound();
        int newLowerBound = Math.min(oldLowerBound, auxiliaryNoteStartBeat);
        int newUpperBound = Math.max(oldUpperBound, auxiliaryNoteEndBeat);
        if (newLowerBound != oldLowerBound || newUpperBound != oldUpperBound) {
            visibleArea.setXBounds(newLowerBound, newUpperBound);
            player.statusProperty().addListener(statusListener);
        }
    }

    protected int getAuxiliaryNoteLength() {
        return (int) (beatMillisConverter.getBpm() / 100) + 1;
    }

    protected abstract int getAuxiliaryNoteStartBeat();
}