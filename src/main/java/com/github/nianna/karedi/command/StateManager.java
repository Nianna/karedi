package main.java.com.github.nianna.karedi.command;

import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class StateManager {

    private final DisplayContext displayContext;

    private final NoteSelection noteSelection;

    StateManager(DisplayContext displayContext, NoteSelection noteSelection) {
        this.displayContext = displayContext;
        this.noteSelection = noteSelection;
    }

    StateSnapshot createSnapshot() {
        return new StateSnapshot(displayContext.getActiveTrack(), displayContext.getActiveLine(), noteSelection.get());
    }

    void restore(StateSnapshot stateSnapshot) {
        displayContext.setActiveTrack(stateSnapshot.activeTrack);
        displayContext.setActiveLine(stateSnapshot.activeLine);
        noteSelection.set(stateSnapshot.selectedNotes);
    }

    static class StateSnapshot {
        private final SongTrack activeTrack;
        private final SongLine activeLine;
        private final List<Note> selectedNotes = new ArrayList<>();

        private StateSnapshot(SongTrack activeTrack, SongLine activeLine, List<Note> selectedNotes) {
            this.activeTrack = activeTrack;
            this.activeLine = activeLine;
            this.selectedNotes.addAll(selectedNotes);
        }
    }
}
