package main.java.com.github.nianna.karedi.controller;

import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.util.ListenersUtils;
import org.springframework.stereotype.Component;

@Component
public class LineNumberController implements Controller {
	@FXML
	private AnchorPane pane;
	@FXML
	private Text label;

	private ListChangeListener<? super SongLine> lineListChangeListener;
	private final DisplayContext displayContext;

	public LineNumberController(DisplayContext displayContext) {
		this.displayContext = displayContext;
	}

	@Override
    public void onSceneAndContextInitialized() {
		lineListChangeListener = ListenersUtils.createListChangeListener(line -> updateLabel(),
				ListenersUtils::pass, ListenersUtils::pass, ListenersUtils::pass);

		displayContext.activeLineProperty().addListener(obs -> updateLabel());
		displayContext.activeTrackProperty().addListener(this::onTrackChanged);
	}

	@Override
	public Node getContent() {
		return pane;
	}

	private String getActiveLineNumber() {
		if (displayContext.getActiveTrack() != null && displayContext.getActiveLine() != null) {
			return (displayContext.getActiveTrack().indexOf(displayContext.getActiveLine()) + 1) + "";
		}
		return "";
	}

	private void onTrackChanged(Observable obs, SongTrack oldTrack, SongTrack newTrack) {
		if (oldTrack != null) {
			oldTrack.removeLineListListener(lineListChangeListener);
		}
		if (newTrack != null) {
			newTrack.addLineListListener(lineListChangeListener);
		}
	}

	private void updateLabel() {
		label.setText(getActiveLineNumber());
	}
}
