package main.java.com.github.nianna.karedi.context;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import main.java.com.github.nianna.karedi.audio.CachedAudioFile;
import main.java.com.github.nianna.karedi.audio.Player;
import main.java.com.github.nianna.karedi.audio.Player.Mode;
import main.java.com.github.nianna.karedi.audio.Player.Status;
import main.java.com.github.nianna.karedi.audio.Playlist;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SongPlayer {
	private static final int TONE_OFFSET = 60;

	private final Player player;
	private final Marker marker;
	private final BeatMillisConverter converter;

	private final Playlist playlist;
	private final BooleanBinding activeAudioIsNull;

	private ReadOnlyObjectWrapper<Playback> currentPlayback = new ReadOnlyObjectWrapper<>();

	private Song song;

	public SongPlayer(Player player, Marker marker, BeatMillisConverter converter) {
		this.player = player;
		this.marker = marker;
		this.converter = converter;
		playlist = player.getPlaylist();
		activeAudioIsNull = activeAudioFileProperty().isNull();
		converter.addListener(this::onBeatMillisConverterInvalidated);
	}

	private void onBeatMillisConverterInvalidated(Observable observable) {
		stop();
	}

	public void play(int fromBeat, int toBeat, Mode mode) {
		long startMillis = beatToMillis(fromBeat);
		long endMillis = beatToMillis(toBeat);
		List<Note> notes = song.getAudibleNotes(fromBeat, toBeat);
		play(startMillis, endMillis, notes, mode);
	}

	public void play(long startMillis, long endMillis, List<? extends Note> notes, Mode mode) {
		Queue<Pair<Long, Integer>> list = new LinkedList<>();
		if (notes != null) {
			notes.stream()
					.sorted(Comparator.comparing(Note::getStart))
					.map(this::noteToTimeTonePair).forEach(list::add);
		}
		currentPlayback.setValue(new Playback(startMillis, endMillis, notes));
		player.play(startMillis, endMillis, list, mode);
	}

	public void setSong(Song song) {
		this.song = song;
	}

	private Pair<Long, Integer> noteToTimeTonePair(Note note) {
		return new Pair<>(beatToMillis(note.getStart()), normalize(note.getTone()));
	}

	public static int normalize(int tone) {
		return tone + TONE_OFFSET;
	}

	private long beatToMillis(int beat) {
		return converter.beatToMillis(beat);
	}

	// player
	public ReadOnlyObjectProperty<Status> statusProperty() {
		return player.statusProperty();
	}

	public Status getStatus() {
		return player.getStatus();
	}

	public boolean isTickingEnabled() {
		return player.isTickingEnabled();
	}

	public void setTickingEnabled(boolean value) {
		player.setTickingEnabled(value);
	}

	public void stop() {
		currentPlayback.setValue(null);
		player.stop();
	}

	public void reset() {
		currentPlayback.setValue(null);
		player.reset();
	}

	// playlist
	public void removeAudioFile(CachedAudioFile file) {
		playlist.removeAudioFile(file);
	}

	public void addAudioFile(CachedAudioFile file) {
		playlist.addAudioFile(file);
	}

	public ReadOnlyObjectProperty<CachedAudioFile> activeAudioFileProperty() {
		return playlist.activeAudioFileProperty();
	}

	public CachedAudioFile getActiveAudioFile() {
		return playlist.getActiveAudioFile();
	}

	public void setActiveAudioFile(CachedAudioFile file) {
		playlist.setActiveAudioFile(file);
	}

	public ObservableList<CachedAudioFile> getAudioFiles() {
		return playlist.getAudioFiles();
	}

	// marker
	public ReadOnlyIntegerProperty markerBeatProperty() {
		return marker.beatProperty();
	}

	public int getMarkerBeat() {
		return marker.getBeat();
	}

	public void setMarkerBeat(int beat) {
		marker.setBeat(beat);
	}

	public void setMarkerTime(long time) {
		marker.setTime(time);
	}

	public Long getMarkerTime() {
		return marker.getTime();
	}

	public ReadOnlyLongProperty markerTimeProperty() {
		return marker.timeProperty();
	}

	public Playback getCurrentPlayback() {
		return currentPlayback.get();
	}

	public ReadOnlyObjectWrapper<Playback> currentPlaybackProperty() {
		return currentPlayback;
	}

	public BooleanBinding activeAudioIsNullProperty() {
		return activeAudioIsNull;
	}

	public static class Playback {
		private final long startMillis;
		private final long endMillis;
		private final List<? extends Note> notes;

		Playback(long startMillis, long endMillis, List<? extends Note> notes) {
			this.startMillis = startMillis;
			this.endMillis = endMillis;
			this.notes = notes == null ? new ArrayList<>() : notes;
		}

		public long getStartMillis() {
			return startMillis;
		}

		public long getEndMillis() {
			return endMillis;
		}

		public List<? extends Note> getNotes() {
			return notes;
		}
	}
}
