package main.java.com.github.nianna.karedi.guard;

import javafx.beans.value.ObservableValue;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.song.SongTrack;
import org.springframework.stereotype.Component;

@Component
public class ActiveTrackIsVisibleAndUnmutedGuard implements Guard {

	private final SongContext songContext;

	public ActiveTrackIsVisibleAndUnmutedGuard(SongContext songContext) {
		this.songContext = songContext;
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
			newTrack.setVisible(true);
			newTrack.setMuted(false);
		}
	}
}
