package main.java.com.github.nianna.karedi.context;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import main.java.com.github.nianna.karedi.audio.CachedAudioFile;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BeatRange {
	private static final int MIN_BEAT = 0;
	private static final int MAX_BEAT = 100;
	private static final int BEAT_MARGIN = 20;

	private final ReadOnlyIntegerWrapper minBeat;
	private final ReadOnlyIntegerWrapper maxBeat;
	private final SongPlayer songPlayer;

	private IntBounded bounds;
	private BeatMillisConverter converter;
	private InvalidationListener refresher = obs -> refresh();

	@Autowired
	BeatRange(BeatMillisConverter converter, SongPlayer songPlayer) {
		this(MIN_BEAT, MAX_BEAT, songPlayer, converter);
	}

	private BeatRange(int minBeat, int maxBeat, SongPlayer songPlayer, BeatMillisConverter converter) {
		this.minBeat = new ReadOnlyIntegerWrapper(minBeat);
		this.maxBeat = new ReadOnlyIntegerWrapper(maxBeat);
		this.songPlayer = songPlayer;
		this.converter = converter;
		songPlayer.activeAudioFileProperty().addListener(refresher);
		converter.addListener(refresher);
	}

	private void refresh() {
		int minBeat = converter.millisToBeat(0);
		int maxBeat = MAX_BEAT;

		CachedAudioFile activeAudioFile = songPlayer.getActiveAudioFile();

		if (activeAudioFile != null) {
			maxBeat = converter.millisToBeat(activeAudioFile.getDuration());
		}
		if (bounds != null && bounds.isValid()) {
			minBeat = Math.min(bounds.getLowerXBound() - BEAT_MARGIN, minBeat);
			maxBeat = Math.max(bounds.getUpperXBound() + BEAT_MARGIN, maxBeat);
		}

		setMinBeat(minBeat);
		setMaxBeat(maxBeat);
	}

	void setBounds(IntBounded bounds) {
		if (this.bounds != null) {
			this.bounds.removeListener(refresher);
		}
		this.bounds = bounds;
		if (this.bounds != null) {
			this.bounds.addListener(refresher);
		}
		refresh();
	}

	public ReadOnlyIntegerProperty minBeatProperty() {
		return minBeat.getReadOnlyProperty();
	}

	public ReadOnlyIntegerProperty maxBeatProperty() {
		return maxBeat.getReadOnlyProperty();
	}

	public final int getMinBeat() {
		return minBeatProperty().get();
	}

	public final int getMaxBeat() {
		return maxBeatProperty().get();
	}

	private void setMaxBeat(int value) {
		maxBeat.set(value);
	}

	private void setMinBeat(int value) {
		minBeat.set(value);
	}
}