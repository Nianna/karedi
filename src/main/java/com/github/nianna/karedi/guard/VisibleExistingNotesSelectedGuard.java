package main.java.com.github.nianna.karedi.guard;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.util.ListenersUtils;
import org.springframework.stereotype.Component;

@Component
public class VisibleExistingNotesSelectedGuard implements Guard {

	private final NoteSelection noteSelection;

	private final DisplayContext displayContext;

	private final ListChangeListener<? super Note> noteListChangeListener = ListenersUtils
			.createListContentChangeListener(ListenersUtils::pass, this::onNoteRemoved);

	public VisibleExistingNotesSelectedGuard(NoteSelection noteSelection, DisplayContext displayContext) {
		this.noteSelection = noteSelection;
		this.displayContext = displayContext;
	}

	@Override
	public void enable() {
		displayContext.activeTrackProperty().addListener(this::onTrackChanged);
	}

	@Override
	public void disable() {
		displayContext.activeTrackProperty().removeListener(this::onTrackChanged);
	}

	private void onTrackChanged(ObservableValue<? extends SongTrack> observableValue, SongTrack oldTrack, SongTrack newTrack) {
		if (oldTrack != newTrack) {
			noteSelection.clear();
			if (oldTrack != null) {
				oldTrack.removeNoteListListener(noteListChangeListener);
			}
			if (newTrack != null) {
				newTrack.addNoteListListener(noteListChangeListener);
			}
		}
	}

	private void onNoteRemoved(Note note) {
		if (noteSelection.isSelected(note)) {
			noteSelection.deselect(note);
		}
	}

}

