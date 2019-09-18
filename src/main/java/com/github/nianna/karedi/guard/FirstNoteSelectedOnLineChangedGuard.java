package main.java.com.github.nianna.karedi.guard;

import javafx.beans.value.ObservableValue;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.SongLine;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class FirstNoteSelectedOnLineChangedGuard implements Guard {

	private final NoteSelection noteSelection;

	private final DisplayContext displayContext;

	public FirstNoteSelectedOnLineChangedGuard(NoteSelection noteSelection, DisplayContext displayContext) {
		this.noteSelection = noteSelection;
		this.displayContext = displayContext;
	}

	@Override
	public void enable() {
		displayContext.activeLineProperty().addListener(this::onLineChanged);
	}

	@Override
	public void disable() {
		displayContext.activeLineProperty().removeListener(this::onLineChanged);
	}

	private void onLineChanged(ObservableValue<? extends SongLine> observableValue, SongLine oldLine, SongLine newLine) {
		if (newLine != oldLine) {
			if (newLine != null && newLine.size() > 0) {
				noteSelection.selectOnly(newLine.getFirst());
			}
		}
	}
}
