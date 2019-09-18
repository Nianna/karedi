package main.java.com.github.nianna.karedi.guard;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.SongLine;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class VisibleAreaLockedOnLineChangedGuard implements Guard {

	private final DisplayContext displayContext;

	private final InvalidationListener boundsListener = obs -> onBoundsInvalidated();

	public VisibleAreaLockedOnLineChangedGuard(DisplayContext displayContext) {
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
			if (oldLine != null) {
				oldLine.removeListener(boundsListener);
			}
			if (newLine != null) {
				newLine.addListener(boundsListener);
				displayContext.adjustToBounds(newLine);
			}
		}
	}

	private void onBoundsInvalidated() {
		SongLine activeLine = displayContext.getActiveLine();
		if (activeLine != null && activeLine.isValid()) {
			displayContext.adjustToBounds(activeLine);
		}
	}
}
