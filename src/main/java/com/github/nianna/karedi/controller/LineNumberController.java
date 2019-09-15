package main.java.com.github.nianna.karedi.controller;

import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongState;
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

	private AppContext appContext;
	private ListChangeListener<? super SongLine> lineListChangeListener;
	private final SongState songState;

	public LineNumberController(SongState songState) {
		this.songState = songState;
	}

	@Override
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;

		lineListChangeListener = ListenersUtils.createListChangeListener(line -> updateLabel(),
				ListenersUtils::pass, ListenersUtils::pass, ListenersUtils::pass);

		songState.activeLineProperty().addListener(obs -> updateLabel());
		songState.activeTrackProperty().addListener(this::onTrackChanged);
	}

	@Override
	public Node getContent() {
		return pane;
	}

	private String getActiveLineNumber() {
		if (songState.getActiveTrack() != null && songState.getActiveLine() != null) {
			return (songState.getActiveTrack().indexOf(songState.getActiveLine()) + 1) + "";
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
