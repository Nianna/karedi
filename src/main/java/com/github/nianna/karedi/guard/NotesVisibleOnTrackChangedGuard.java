package main.java.com.github.nianna.karedi.guard;

import javafx.beans.value.ObservableValue;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.song.SongTrack;
import org.springframework.stereotype.Component;

@Component
public class NotesVisibleOnTrackChangedGuard implements Guard {

	private final SongState songState;

	private final AppContext appContext;

	public NotesVisibleOnTrackChangedGuard(SongState songState, AppContext appContext) {
		this.songState = songState;
		this.appContext = appContext;
	}

	@Override
	public void enable() {
		songState.activeTrackProperty().addListener(this::onTrackChanged);
	}

	@Override
	public void disable() {
		songState.activeTrackProperty().removeListener(this::onTrackChanged);
	}

	private void onTrackChanged(ObservableValue<? extends SongTrack> observableValue, SongTrack oldTrack, SongTrack newTrack) {
		if (newTrack != oldTrack && newTrack != null) {
			appContext.assertAllNeededTonesVisible();
		}
	}
}
