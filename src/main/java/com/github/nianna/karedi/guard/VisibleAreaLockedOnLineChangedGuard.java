package main.java.com.github.nianna.karedi.guard;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.song.SongLine;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class VisibleAreaLockedOnLineChangedGuard implements Guard {

	private final SongContext songContext;

	private final VisibleArea visibleArea;

	private final InvalidationListener boundsListener = obs -> onBoundsInvalidated();

	public VisibleAreaLockedOnLineChangedGuard(SongContext songContext, VisibleArea visibleArea) {
		this.songContext = songContext;
		this.visibleArea = visibleArea;
	}

	@Override
	public void enable() {
		songContext.activeLineProperty().addListener(this::onLineChanged);
	}

	@Override
	public void disable() {
		songContext.activeLineProperty().removeListener(this::onLineChanged);
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
		SongLine activeLine = songContext.getActiveLine();
		if (activeLine != null && activeLine.isValid()) {
			visibleArea.adjustToBounds(activeLine);
		}
	}
}
