package main.java.com.github.nianna.karedi.guard;

import javafx.beans.value.ObservableValue;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.SongTrack;
import org.springframework.stereotype.Component;

@Component
public class ActiveTrackIsVisibleAndUnmutedGuard implements Guard {

	private final DisplayContext displayContext;

	public ActiveTrackIsVisibleAndUnmutedGuard(DisplayContext displayContext) {
		this.displayContext = displayContext;
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
			newTrack.setVisible(true);
			newTrack.setMuted(false);
		}
	}
}
