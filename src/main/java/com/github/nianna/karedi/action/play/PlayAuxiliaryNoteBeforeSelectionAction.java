package main.java.com.github.nianna.karedi.action.play;

import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;
import org.springframework.stereotype.Component;

import static main.java.com.github.nianna.karedi.action.KarediActions.PLAY_BEFORE_SELECTION;

@Component
class PlayAuxiliaryNoteBeforeSelectionAction extends PlayAuxiliaryNoteAction {

    PlayAuxiliaryNoteBeforeSelectionAction(NoteSelection selection, SongPlayer player, DisplayContext displayContext, BeatMillisConverter beatMillisConverter) {
        super(selection, player, displayContext, beatMillisConverter);
    }

    @Override
    protected int getAuxiliaryNoteStartBeat() {
        return selection.getFirst().get().getStart() + 1 - getAuxiliaryNoteLength();
    }

    @Override
    public KarediActions handles() {
        return PLAY_BEFORE_SELECTION;
    }
}
