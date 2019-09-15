package main.java.com.github.nianna.karedi.guard;

import javafx.collections.ListChangeListener;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.ListenersUtils;
import org.springframework.stereotype.Component;

@Component
public class PlaybackStoppedOnSelectionChangedGuard implements Guard {

	private final NoteSelection noteSelection;

	private final SongPlayer songPlayer;

	private final ListChangeListener<? super Note> noteListChangeListener = ListenersUtils
			.createListContentChangeListener(this::onSelectionChanged, this::onSelectionChanged);

	public PlaybackStoppedOnSelectionChangedGuard(NoteSelection noteSelection, SongPlayer songPlayer) {
		this.noteSelection = noteSelection;
		this.songPlayer = songPlayer;
	}

	@Override
	public void enable() {
		noteSelection.get().addListener(noteListChangeListener);
	}

	@Override
	public void disable() {
		noteSelection.get().removeListener(noteListChangeListener);
	}

	private void onSelectionChanged(Note ignored) {
		if (noteSelection.size() > 0) {
			songPlayer.stop();
		}
	}
}

