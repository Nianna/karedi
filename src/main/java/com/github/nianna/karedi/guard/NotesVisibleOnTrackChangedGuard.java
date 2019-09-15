package main.java.com.github.nianna.karedi.guard;

import javafx.beans.value.ObservableValue;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.song.SongTrack;
import org.springframework.stereotype.Component;

@Component
public class NotesVisibleOnTrackChangedGuard implements Guard {

	private final SongContext songContext;

	private final AppContext appContext;

	public NotesVisibleOnTrackChangedGuard(SongContext songContext, AppContext appContext) {
		this.songContext = songContext;
		this.appContext = appContext;
	}

	@Override
	public void enable() {
		songContext.activeTrackProperty().addListener(this::onTrackChanged);
	}

	@Override
	public void disable() {
		songContext.activeTrackProperty().removeListener(this::onTrackChanged);
	}

	private void onTrackChanged(ObservableValue<? extends SongTrack> observableValue, SongTrack oldTrack, SongTrack newTrack) {
		if (newTrack != oldTrack && newTrack != null) {
			appContext.assertAllNeededTonesVisible();
		}
	}
}
