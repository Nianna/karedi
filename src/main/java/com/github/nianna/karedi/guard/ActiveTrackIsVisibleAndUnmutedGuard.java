package main.java.com.github.nianna.karedi.guard;

import javafx.beans.value.ObservableValue;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.song.SongTrack;
import org.springframework.stereotype.Component;

@Component
public class ActiveTrackIsVisibleAndUnmutedGuard implements Guard {

	private final SongState songState;

	public ActiveTrackIsVisibleAndUnmutedGuard(SongState songState) {
		this.songState = songState;
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
			newTrack.setVisible(true);
			newTrack.setMuted(false);
		}
	}
}
