package main.java.com.github.nianna.karedi.context;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.ActionManager;
import main.java.com.github.nianna.karedi.action.ActionMap;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.audio.AudioFileLoader;
import main.java.com.github.nianna.karedi.audio.CachedAudioFile;
import main.java.com.github.nianna.karedi.audio.Player.Mode;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.CommandHistory;
import main.java.com.github.nianna.karedi.guard.Guard;
import main.java.com.github.nianna.karedi.parser.Parser;
import main.java.com.github.nianna.karedi.parser.Unparser;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;
import main.java.com.github.nianna.karedi.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Component
public class AppContext {
	public static final Logger LOGGER = Logger.getLogger(KarediApp.class.getPackage().getName()); //TODO refactor

	@Autowired
	private SongContext songContext;
//	private ReadOnlyObjectWrapper<Song> activeSong;
	private ReadOnlyObjectProperty<SongTrack> activeTrack;
	private ReadOnlyObjectProperty<SongLine> activeLine;

	private final ReadOnlyObjectWrapper<File> activeFile = new ReadOnlyObjectWrapper<>();
	private final ObjectProperty<Command> lastSavedCommand = new SimpleObjectProperty<>();

	@Autowired
	private Parser parser;

	@Autowired
	private Unparser unparser;

	@Autowired
	private SongLoader songLoader;

	@Autowired
	private SongDisassembler songDisassembler;

	@Autowired
	private SongSaver songSaver;

	private final ActionHelper actionHelper = new ActionHelper();

	@Autowired
	private CommandHistory history;

	@Autowired
	private NoteSelection selection;

	@Autowired
	private BeatMillisConverter beatMillisConverter;

	@Autowired
	private SongPlayer player;

	@Autowired
	private  BeatRange beatRange;

	@Autowired
	private  VisibleArea visibleArea;

	@Autowired
	private List<Guard> guards;

	@Autowired
    private ActionManager actionManager;

	public ObservableList<Note> getObservableSelection() {
		return observableSelection;
	}

	private final ObservableList<Note> observableSelection = FXCollections
			.observableArrayList(note -> new Observable[] { note });

	public IntBounded getSelectionBounds() {
		return selectionBounds;
	}

	private final IntBounded selectionBounds = new BoundingBox<>(observableSelection);
	private File directory;

	private final InvalidationListener beatMillisConverterInvalidationListener = obs -> onBeatMillisConverterInvalidated();

	// Convenience bindings for actions
	private BooleanBinding activeFileIsNull;
	private BooleanBinding activeAudioIsNull;
//	private IntegerProperty activeSongTrackCount;

	@Autowired
    private CommandExecutor commandExecutor;

    public AppContext() {
		LOGGER.setUseParentHandlers(false);
	}

	public BooleanBinding hasNoChangesProperty() {
        return lastSavedCommand.isEqualTo(history.activeCommandProperty());
    }

    public BooleanBinding activeFileIsNullProperty() {
        return activeFileIsNull;
    }

    public BooleanBinding activeAudioIsNullProperty() {
        return activeAudioIsNull;
    }

	@PostConstruct
	public void initAppContext() {
		activeTrack = songContext.activeTrackProperty();
		activeLine = songContext.activeLineProperty();

		activeFileIsNull = activeFileProperty().isNull();
		activeAudioIsNull = player.activeAudioFileProperty().isNull();

		LOGGER.setUseParentHandlers(false);

		Bindings.bindContent(observableSelection, getSelected());
		selectionBounds.addListener(obs -> onSelectionBoundsInvalidated());

		guards.forEach(Guard::enable);
	}

	// Actions
	public void addAction(KarediActions key, KarediAction action) {
		actionHelper.add(key, action);
	}

	public KarediAction getAction(KarediActions key) {
		return Optional.ofNullable(actionHelper.get(key)).orElse(actionManager.get(key));
	}

	public void execute(KarediActions action) {
		actionHelper.execute(action);
	}

	public boolean canExecute(KarediActions action) {
		return actionHelper.canExecute(action);
	}

	// Beat range
    public ReadOnlyIntegerProperty minBeatProperty() {
		return beatRange.minBeatProperty();
	}

	public ReadOnlyIntegerProperty maxBeatProperty() {
		return beatRange.maxBeatProperty();
	}

	public IntegerBinding playToTheEndStartBeatProperty() {
        return Bindings.createIntegerBinding(() -> {
            if (isMarkerVisible()) {
                return getMarkerBeat();
            } else {
                return visibleArea.getLowerXBound();
            }
        }, markerBeatProperty(), visibleArea);
    }

	// Visible area
	public void invalidateVisibleArea() {
		visibleArea.invalidate();
	}

	public void assertAllNeededTonesVisible() {
		assertAllNeededTonesVisible(visibleArea.getLowerXBound(), visibleArea.getUpperXBound());
	}

	public void assertAllNeededTonesVisible(int fromBeat, int toBeat) {
		List<Note> notes = getSong().getVisibleNotes(fromBeat, toBeat);
		visibleArea.assertBoundsYVisible(addMargins(new BoundingBox<>(notes)));
	}

	public void setVisibleAreaXBounds(int lowerXBound, int upperXBound) {
		setVisibleAreaXBounds(lowerXBound, upperXBound, true);
	}

	private void setVisibleAreaXBounds(int lowerXBound, int upperXBound, boolean setLineToNull) {
		if (visibleArea.setXBounds(lowerXBound, upperXBound) && setLineToNull) {
			setActiveLine(null);
		}
	}

	public void setVisibleAreaYBounds(int lowerBound, int upperBound) {
		if (visibleArea.setYBounds(lowerBound, upperBound)) {
			setActiveLine(null);
		}
	}

	public void increaseVisibleAreaXBounds(int by) {
		if (visibleArea.increaseXBounds(by)) {
			setActiveLine(null);
		}
	}

	public void increaseVisibleAreaYBounds(int by) {
		if (visibleArea.increaseYBounds(by)) {
			setActiveLine(null);
		}
	}

	public IntBounded addMargins(IntBounded bounds) {
		return visibleArea.addMargins(bounds);
	}

	public IntBounded getVisibleAreaBounds() {
		return visibleArea;
	}

	public void moveVisibleArea(Direction direction, int by) {
		visibleArea.move(direction, by);
		setActiveLine(null);
	}

	// History
	public boolean execute(Command command) {
		return commandExecutor.execute(command);
	}

	// Selection
	public NoteSelection getSelection() {
		return selection;
	}

	public ObservableList<Note> getSelected() {
		return selection.get();
	}

	// Audio
	public CachedAudioFile getActiveAudioFile() {
		return player.getActiveAudioFile();
	}

	public void removeAudioFile(CachedAudioFile file) {
		player.removeAudioFile(file);
	}

	public void loadAudioFile(File file) {
		AudioFileLoader.loadMp3File(file, (newAudio -> {
			if (newAudio.isPresent()) {
				player.addAudioFile(newAudio.get());
				setActiveAudioFile(newAudio.get());
				LOGGER.info(I18N.get("import.audio.success"));
			} else {
				LOGGER.severe(I18N.get("import.audio.fail"));
			}
		}));
	}

	public void setActiveAudioFile(CachedAudioFile file) {
		if (file != getActiveAudioFile()) {
			execute(KarediActions.STOP_PLAYBACK);
			player.setActiveAudioFile(file);
			if (file != null) {
				beatRange.setMaxTime(file.getDuration());
			} else {
				beatRange.setMaxTime(null);
			}
		}
	}

	// Player
    public void playRange(int fromBeat, int toBeat, Mode mode) {
		assertAllNeededTonesVisible(fromBeat, toBeat);
		player.play(fromBeat, toBeat, mode);
	}

	private void play(long startMillis, long endMillis, List<Note> notes, Mode mode) {
		if (!isMarkerVisible()) {
			setMarkerBeat(visibleArea.getLowerXBound());
		}
		player.play(startMillis, endMillis, notes, mode);
	}

	// Marker
	private ReadOnlyIntegerProperty markerBeatProperty() {
		return player.markerBeatProperty();
	}

	private int getMarkerBeat() {
		return player.getMarkerBeat();
	}

	private void setMarkerBeat(int beat) {
		player.setMarkerBeat(beat);
	}

	private ReadOnlyLongProperty markerTimeProperty() {
		return player.markerTimeProperty();
	}

	private Long getMarkerTime() {
		return player.getMarkerTime();
	}

	private void setMarkerTime(long time) {
		player.setMarkerTime(time);
	}

	private boolean isMarkerVisible() {
		return MathUtils.inRange(getMarkerTime(), beatMillisConverter.beatToMillis(visibleArea.getLowerXBound()),
				beatMillisConverter.beatToMillis(visibleArea.getUpperXBound()));
	}

	// Files
	public ReadOnlyObjectProperty<File> activeFileProperty() {
		return activeFile.getReadOnlyProperty();
	}

	public File getActiveFile() {
		return activeFileProperty().get();
	}

	public void setActiveFile(File file) {
		this.activeFile.set(file);
		KarediApp.getInstance().updatePrimaryStageTitle(file);
		directory = file == null ? null : file.getParentFile();
	}

	public File getDirectory() {
		return directory;
	}

	public void loadSongFile(File file) {
		loadSongFile(file, true);
	}

	public void loadSongFile(File file, boolean resetPlayer) {
		if (file != null) {
			reset(resetPlayer);
			setActiveFile(file);
			Song song = songLoader.load(file);
			song.getTagValue(TagKey.MP3).ifPresent(audioFileName -> {
				loadAudioFile(new File(file.getParent(), audioFileName));
			});
			setSong(song);
			LOGGER.info(I18N.get("load.success"));
		}
	}

	public boolean saveSongToFile(File file) {
		if (file != null) {
			if (songSaver.saveSongToFile(file, getSong())) {
				lastSavedCommand.set(history.getActiveCommand());
				return true;
			}
		}
		return false;
	}

	public Song getSong() {
		return songContext.activeSongProperty().get();
	}

	public final void setSong(Song song) {
		Song oldSong = getSong();
		new SongNormalizer(song).normalize();
		// The song has at least one track now
		if (song != oldSong) {
			songContext.setActiveSong(song);
			player.setSong(song);
			onBeatMillisConverterInvalidated();
			if (oldSong != null) {
				oldSong.getBeatMillisConverter()
						.removeListener(beatMillisConverterInvalidationListener);
//				activeSongTrackCount.unbind();
			}
			if (song == null) {
//				setActiveTrack(null);
//				activeSongTrackCount.set(0);
			} else {
				song.getBeatMillisConverter().addListener(beatMillisConverterInvalidationListener);
//				activeSongTrackCount.bind(song.trackCount());
//				setActiveTrack(song.getDefaultTrack().orElse(null));
			}
			beatRange.setBounds(song);
		}
	}

	public ReadOnlyObjectProperty<SongTrack> activeTrackProperty() {
		return activeTrack;
	}

	public final SongTrack getActiveTrack() {
		return activeTrack.get();
	}

	public final void setActiveTrack(SongTrack track) {
		songContext.setActiveTrack(track);
	}

	public final SongLine getActiveLine() {
		return activeLine.get();
	}

	public final void setActiveLine(SongLine line) {
        songContext.setActiveLine(line);
	}

	// Listeners that are necessary to assure consistency

	private void onBeatMillisConverterInvalidated() {
		player.stop();
		if (getSong() == null) {
			beatMillisConverter.setBpm(Song.DEFAULT_BPM);
			beatMillisConverter.setGap(Song.DEFAULT_GAP);
		} else {
			beatMillisConverter.setBpm(getSong().getBpm());
			beatMillisConverter.setGap(getSong().getGap());
		}
	}

	private void onSelectionBoundsInvalidated() {
		if (selection.size() > 0 && selectionBounds.isValid()) {
			setMarkerBeat(selectionBounds.getLowerXBound());
			if (visibleArea.assertBorderlessBoundsVisible(selectionBounds)) {
				setActiveLine(null);
				assertAllNeededTonesVisible();
			}
		}
	}


	// Other
	public boolean needsSaving() {
		return getSong() != null && lastSavedCommand.get() != history.getActiveCommand();
	}

	public Logger getMainLogger() {
		return LOGGER;
	}

	public void reset(boolean resetPlayer) {
		setSong(null);
		lastSavedCommand.set(null);
		history.clear();
		player.stop();
		visibleArea.setDefault();
		if (resetPlayer) {
			player.reset();
		}
	}

	// *************************** ACTIONS ***************************

	private class ActionHelper {
        private ActionMap actionMap = new ActionMap();

        public void add(KarediActions key, KarediAction action) {
            actionMap.put(key.toString(), action);
        }

        public void execute(KarediActions action) {
            if (canExecute(action)) {
                getAction(action).handle(null);
            }
        }

        public boolean canExecute(KarediActions action) {
            return !getAction(action).isDisabled();
        }

        public KarediAction get(KarediActions key) {
            return actionMap.get(key.toString());
        }
    }
}