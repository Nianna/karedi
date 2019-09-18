package main.java.com.github.nianna.karedi.display;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Tooltip;
import javafx.stage.WindowEvent;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.MathUtils;
import main.java.com.github.nianna.karedi.util.MusicalScale;

class NoteTooltip extends Tooltip {
	private final Note note;
	private NoteTooltipDisplayer displayer = new NoteTooltipDisplayer();

	public NoteTooltip(Note note) {
		this.note = note;
		setOnShowing(this::onShowing);
		setOnHidden(this::onHidden);
		setGraphic(displayer);
	}

	private void onShowing(WindowEvent event) {
		displayer.lyricsProperty().bind(note.lyricsProperty());
		displayer.toneProperty().bind(Bindings.createStringBinding(() -> {
			return MusicalScale.getNote(note.getTone()) + " (" + note.getTone() + ")";
		}, note.toneProperty()));
		displayer.lengthProperty().bind(note.lengthProperty().asString());
		displayer.startBeatProperty().bind(note.startProperty().asString());
		displayer.startTimeProperty().bind(Bindings.createStringBinding(() -> {
			double startTime = MathUtils.msToSeconds(note.getLine().getSong().beatToMillis(note.getStart()));
			if (startTime >= 0)
				return startTime + " s";
			return "?";
		}, note.startProperty()));
	}

	private void onHidden(WindowEvent event) {
		displayer.lyricsProperty().unbind();
		displayer.toneProperty().unbind();
		displayer.lengthProperty().unbind();
		displayer.startBeatProperty().unbind();
		displayer.startTimeProperty().unbind();
	}
}