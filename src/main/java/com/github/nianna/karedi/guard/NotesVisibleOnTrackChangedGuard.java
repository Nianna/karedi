package main.java.com.github.nianna.karedi.guard;

import javafx.beans.value.ObservableValue;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.SongTrack;
import org.springframework.stereotype.Component;

@Component
public class NotesVisibleOnTrackChangedGuard implements Guard {

	private final DisplayContext displayContext;

	private final AppContext appContext;

	public NotesVisibleOnTrackChangedGuard(DisplayContext displayContext, AppContext appContext) {
		this.displayContext = displayContext;
		this.appContext = appContext;
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
		if (newTrack != oldTrack && newTrack != null) {
            displayContext.assertAllNeededTonesVisible();
		}
	}
}
