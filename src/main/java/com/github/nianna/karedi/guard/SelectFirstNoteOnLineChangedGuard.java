package main.java.com.github.nianna.karedi.guard;

import javafx.beans.value.ObservableValue;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.song.SongLine;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class SelectFirstNoteOnLineChangedGuard implements Guard {

	private final NoteSelection noteSelection;

	private final SongState songState;

	public SelectFirstNoteOnLineChangedGuard(NoteSelection noteSelection, SongState songState) {
		this.noteSelection = noteSelection;
		this.songState = songState;
	}

	@Override
	public void enable() {
		songState.activeLineProperty().addListener(this::onLineChanged);
	}

	@Override
	public void disable() {
		songState.activeLineProperty().removeListener(this::onLineChanged);
	}

	private void onLineChanged(ObservableValue<? extends SongLine> observableValue, SongLine oldLine, SongLine newLine) {
		if (newLine != oldLine) {
			if (newLine != null && newLine.size() > 0) {
				noteSelection.selectOnly(newLine.getFirst());
			}
		}
	}
}
