package main.java.com.github.nianna.karedi.guard;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.util.ListenersUtils;
import org.springframework.stereotype.Component;

@Component
public class PlayerStoppedOnNoteDeletedGuard implements Guard {

	private final SongPlayer songPlayer;

	private final SongContext songContext;

	private final ListChangeListener<? super Note> noteListChangeListener = ListenersUtils
			.createListContentChangeListener(ListenersUtils::pass, this::onNoteRemoved);

	public PlayerStoppedOnNoteDeletedGuard(SongPlayer songPlayer, SongContext songContext) {
		this.songPlayer = songPlayer;
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
		if (oldTrack != newTrack) {
			if (oldTrack != null) {
				oldTrack.removeNoteListListener(noteListChangeListener);
			}
			if (newTrack != null) {
				newTrack.addNoteListListener(noteListChangeListener);
			}
		}
	}

	private void onNoteRemoved(Note note) {
		songPlayer.stop();
	}
}

