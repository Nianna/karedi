package main.java.com.github.nianna.karedi.guard;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.song.SongLine;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class VisibleAreaIsLockedAfterLineChangedGuard implements Guard {

	private final SongState songState;

	private final VisibleArea visibleArea;

	private final InvalidationListener boundsListener = obs -> onBoundsInvalidated();

	public VisibleAreaIsLockedAfterLineChangedGuard(SongState songState, VisibleArea visibleArea) {
		this.songState = songState;
		this.visibleArea = visibleArea;
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
			if (oldLine != null) {
				oldLine.removeListener(boundsListener);
			}
			if (newLine != null) {
				newLine.addListener(boundsListener);
				visibleArea.adjustToBounds(newLine);
			}
		}
	}
	private void onBoundsInvalidated() {
		SongLine activeLine = songState.getActiveLine();
		if (activeLine != null && activeLine.isValid()) {
			visibleArea.adjustToBounds(activeLine);
		}
	}
}
