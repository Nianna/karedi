package main.java.com.github.nianna.karedi.context;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.input.Clipboard;
import javafx.scene.paint.Color;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.ActionManager;
import main.java.com.github.nianna.karedi.action.ActionMap;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.audio.AudioFileLoader;
import main.java.com.github.nianna.karedi.audio.CachedAudioFile;
import main.java.com.github.nianna.karedi.audio.Player.Mode;
import main.java.com.github.nianna.karedi.audio.Player.Status;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandComposite;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.CommandHistory;
import main.java.com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import main.java.com.github.nianna.karedi.dialog.ChooseTracksDialog;
import main.java.com.github.nianna.karedi.dialog.EditFilenamesDialog;
import main.java.com.github.nianna.karedi.dialog.EditFilenamesDialog.FilenamesEditResult;
import main.java.com.github.nianna.karedi.dialog.ExportWithErrorsAlert;
import main.java.com.github.nianna.karedi.dialog.OverwriteAlert;
import main.java.com.github.nianna.karedi.guard.Guard;
import main.java.com.github.nianna.karedi.parser.Parser;
import main.java.com.github.nianna.karedi.parser.Unparser;
import main.java.com.github.nianna.karedi.parser.element.InvalidSongElementException;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.Song.Medley;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;
import main.java.com.github.nianna.karedi.util.ForbiddenCharacterRegex;
import main.java.com.github.nianna.karedi.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class AppContext {
	private static final Logger LOGGER = Logger.getLogger(KarediApp.class.getPackage().getName());

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

	@PostConstruct
	public void initAppContext() {
		activeTrack = songContext.activeTrackProperty();
		activeLine = songContext.activeLineProperty();

		activeFileIsNull = activeFileProperty().isNull();
		activeAudioIsNull = player.activeAudioFileProperty().isNull();

		LOGGER.setUseParentHandlers(false);
		actionHelper.addActions();

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
	private ReadOnlyIntegerProperty minBeatProperty() {
		return beatRange.minBeatProperty();
	}

	private ReadOnlyIntegerProperty maxBeatProperty() {
		return beatRange.maxBeatProperty();
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

	private void loadAudioFile(File file) {
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
	private void playRange(int fromBeat, int toBeat, Mode mode) {
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
		return MathUtils.inRange(getMarkerTime(), beatToMillis(visibleArea.getLowerXBound()),
				beatToMillis(visibleArea.getUpperXBound()));
	}

	// Beat <-> millis convertion
	public long beatToMillis(int beat) {
		return beatMillisConverter.beatToMillis(beat);
	}

	public int millisToBeat(long millis) {
		return beatMillisConverter.millisToBeat(millis);
	}

	public BeatMillisConverter getBeatMillisConverter() {
		return beatMillisConverter;
	}

	// Files
	public ReadOnlyObjectProperty<File> activeFileProperty() {
		return activeFile.getReadOnlyProperty();
	}

	public File getActiveFile() {
		return activeFileProperty().get();
	}

	private void setActiveFile(File file) {
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

	private void loadSongFile(File file, boolean resetPlayer) {
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

	private boolean saveSongToFile(File file) {
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

	private void reset(boolean resetPlayer) {
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

		private void addActions() {
			addFileActions();
			addPlayActions();
		}

		private void addFileActions() {
			add(KarediActions.NEW, new NewSongAction());
			add(KarediActions.LOAD, new LoadSongAction());
			add(KarediActions.RELOAD, new ReloadSongAction());
			add(KarediActions.RENAME, new RenameAction());
			add(KarediActions.SAVE, new SaveSongAction());
			add(KarediActions.SAVE_AS, new SaveSongAsAction());
			add(KarediActions.IMPORT_AUDIO, new ImportAudioAction());
			add(KarediActions.EXPORT_AS_SINGLEPLAYER, new ExportTracksAction(1));
			add(KarediActions.EXPORT_AS_DUET, new ExportTracksAction(2));
			add(KarediActions.EXIT, new ExitAction());
		}

		private void addPlayActions() {
			add(KarediActions.PLAY_SELECTION_AUDIO, new PlaySelectionAction(Mode.AUDIO_ONLY));
			add(KarediActions.PLAY_SELECTION_MIDI, new PlaySelectionAction(Mode.MIDI_ONLY));
			add(KarediActions.PLAY_SELECTION_AUDIO_MIDI, new PlaySelectionAction(Mode.AUDIO_MIDI));
			add(KarediActions.PLAY_VISIBLE_AUDIO, new PlayRangeAction(Mode.AUDIO_ONLY,
					visibleArea.lowerXBoundProperty(), visibleArea.upperXBoundProperty()));
			add(KarediActions.PLAY_VISIBLE_MIDI, new PlayRangeAction(Mode.MIDI_ONLY,
					visibleArea.lowerXBoundProperty(), visibleArea.upperXBoundProperty()));
			add(KarediActions.PLAY_VISIBLE_AUDIO_MIDI, new PlayRangeAction(Mode.AUDIO_MIDI,
					visibleArea.lowerXBoundProperty(), visibleArea.upperXBoundProperty()));
			add(KarediActions.PLAY_ALL_AUDIO,
					new PlayRangeAction(Mode.AUDIO_ONLY, minBeatProperty(), maxBeatProperty()));
			add(KarediActions.PLAY_ALL_MIDI,
					new PlayRangeAction(Mode.MIDI_ONLY, minBeatProperty(), maxBeatProperty()));
			add(KarediActions.PLAY_ALL_AUDIO_MIDI,
					new PlayRangeAction(Mode.AUDIO_MIDI, minBeatProperty(), maxBeatProperty()));

			IntegerBinding playToTheEndStartBeat = Bindings.createIntegerBinding(() -> {
				if (isMarkerVisible()) {
					return getMarkerBeat();
				} else {
					return visibleArea.getLowerXBound();
				}
			}, markerBeatProperty(), visibleArea);
			add(KarediActions.PLAY_TO_THE_END_AUDIO,
					new PlayRangeAction(Mode.AUDIO_ONLY, playToTheEndStartBeat, maxBeatProperty()));
			add(KarediActions.PLAY_TO_THE_END_MIDI,
					new PlayRangeAction(Mode.MIDI_ONLY, playToTheEndStartBeat, maxBeatProperty()));
			add(KarediActions.PLAY_TO_THE_END_AUDIO_MIDI,
					new PlayRangeAction(Mode.AUDIO_MIDI, playToTheEndStartBeat, maxBeatProperty()));
			add(KarediActions.PLAY_MEDLEY_AUDIO, new PlayMedleyAction(Mode.AUDIO_ONLY));
			add(KarediActions.PLAY_MEDLEY_AUDIO_MIDI, new PlayMedleyAction(Mode.AUDIO_MIDI));
			add(KarediActions.PLAY_MEDLEY_MIDI, new PlayMedleyAction(Mode.MIDI_ONLY));
			add(KarediActions.PLAY_BEFORE_SELECTION, new PlayAuxiliaryNoteBeforeSelectionAction());
			add(KarediActions.PLAY_AFTER_SELECTION, new PlayAuxiliaryNoteAfterSelectionAction());
			add(KarediActions.STOP_PLAYBACK, new StopPlaybackAction());
			add(KarediActions.TOGGLE_TICKS, new ToggleTicksAction());
		}
	}

	private class ExitAction extends KarediAction {

		@Override
		protected void onAction(ActionEvent event) {
			KarediApp.getInstance().exit(event);
		}

	}

	private class PlaySelectionAction extends KarediAction {
		private Mode mode;

		private PlaySelectionAction(Mode mode) {
			this.mode = mode;
			BooleanBinding condition = selection.isEmptyProperty();
			if (mode != Mode.MIDI_ONLY) {
				condition = condition.or(activeAudioIsNull);
			}
			setDisabledCondition(condition);
		}

		@Override
		protected void onAction(ActionEvent event) {
			playSelection(mode);
		}

		private void playSelection(Mode mode) {
			if (selection.size() > 0 && selectionBounds.isValid()) {
				long startMillis = beatToMillis(selectionBounds.getLowerXBound());
				long endMillis = beatToMillis(selectionBounds.getUpperXBound());
				play(startMillis, endMillis, getSelected(), mode);
			}
		}
	}

	private class PlayRangeAction extends KarediAction {
		private Mode mode;
		private ObservableValue<? extends Number> from;
		private ObservableValue<? extends Number> to;

		private PlayRangeAction(Mode mode, ObservableValue<? extends Number> from,
				ObservableValue<? extends Number> to) {
			this.mode = mode;
			this.from = from;
			this.to = to;

			BooleanBinding condition = songContext.activeSongIsNullProperty();
			if (mode != Mode.MIDI_ONLY) {
				condition = condition.or(activeAudioIsNull);
			}
			setDisabledCondition(condition);
		}

		@Override
		protected void onAction(ActionEvent event) {
			playRange(from.getValue().intValue(), to.getValue().intValue(), mode);
		}

	}

	private class PlayMedleyAction extends KarediAction {
		private Mode mode;
		private BooleanBinding basicCondition;
		private Medley medley;

		private PlayMedleyAction(Mode mode) {
			this.mode = mode;

			basicCondition = songContext.activeSongIsNullProperty();
			if (mode != Mode.MIDI_ONLY) {
				basicCondition = basicCondition.or(activeAudioIsNull);
			}
			setDisabledCondition(basicCondition);
			songContext.activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
				if (newVal == null) {
					setDisabledCondition(basicCondition);
				} else {
					medley = newVal.getMedley();
					setDisabledCondition(
							basicCondition.or(medley.sizeProperty().lessThanOrEqualTo(0)));
				}
			});
		}

		@Override
		protected void onAction(ActionEvent event) {
			selection.clear();
			playRange(medley.getStartBeat(), medley.getEndBeat(), mode);
		}

	}

	private class LoadSongAction extends KarediAction {

		@Override
		protected void onAction(ActionEvent event) {
			if (KarediApp.getInstance().saveChangesIfUserWantsTo()) {
				File file = KarediApp.getInstance().getTxtFileToOpen();
				if (file != null) {
					loadSongFile(file, true);
				}
			}
		}
	}

	private class ReloadSongAction extends KarediAction {
		private Integer trackNumber;
		private Integer lineNumber;
		private List<Color> colors;

		private ReloadSongAction() {
			setDisabledCondition(activeFileIsNull.or(songContext.activeSongIsNullProperty()));
		}

		@Override
		protected void onAction(ActionEvent event) {
			if (KarediApp.getInstance().saveChangesIfUserWantsTo()) {
				backupTrackAndLine();
				backupColors();
				loadSongFile(getActiveFile(), false);

				if (getSong() != null) {
					restoreTrackAndLine();
					restoreColors();
				}
			}
		}

		private void backupTrackAndLine() {
			trackNumber = null;
			lineNumber = null;
			if (getActiveTrack() != null) {
				trackNumber = getSong().indexOf(getActiveTrack());
				if (getActiveLine() != null) {
					lineNumber = getActiveTrack().indexOf(getActiveLine());
				}
			}
		}

		private void backupColors() {
			colors = getSong().getTracks().stream().map(SongTrack::getColor)
					.collect(Collectors.toList());
		}

		private void restoreTrackAndLine() {
			if (trackNumber != null && getSong().size() > trackNumber) {
				setActiveTrack(getSong().get(trackNumber));
				if (lineNumber != null && getActiveTrack().size() > lineNumber) {
					setActiveLine(getActiveTrack().get(lineNumber));
				}
			}
		}

		private void restoreColors() {
			for (int i = 0; i < getSong().size() && i < colors.size(); ++i) {
				getSong().getTrack(i).setColor(colors.get(i));
			}
		}
	}

	private class SaveSongAction extends KarediAction {

		private SaveSongAction() {
			setDisabledCondition(songContext.activeSongIsNullProperty()
					.or(lastSavedCommand.isEqualTo(history.activeCommandProperty())));
		}

		@Override
		protected void onAction(ActionEvent event) {
			if (getActiveFile() != null) {
				saveSongToFile(getActiveFile());
			} else {
				execute(KarediActions.SAVE_AS);
			}
		}
	}

	private class SaveSongAsAction extends KarediAction {

		private SaveSongAsAction() {
			setDisabledCondition(songContext.activeSongIsNullProperty());
		}

		@Override
		protected void onAction(ActionEvent event) {
			File file = KarediApp.getInstance().getTxtFileToSave();
			if (saveSongToFile(file)) {
				setActiveFile(file);
			}
		}
	}

	private class ImportAudioAction extends KarediAction {

		private ImportAudioAction() {
			setDisabledCondition(songContext.activeSongIsNullProperty());
		}

		@Override
		protected void onAction(ActionEvent event) {
			File file = KarediApp.getInstance().getMp3FileToOpen();
			if (file != null) {
				loadAudioFile(file);
			}
		}
	}

	private abstract class PlayAuxiliaryNoteAction extends KarediAction {
		private int oldLowerBound;
		private int oldUpperBound;
		private ChangeListener<? super Status> statusListener;

		private PlayAuxiliaryNoteAction() {
			setDisabledCondition(selection.isEmptyProperty().or(activeAudioIsNull));
			statusListener = (obs, oldStatus, newStatus) -> {
				if (oldStatus == Status.PLAYING && newStatus == Status.READY) {
					setVisibleAreaXBounds(oldLowerBound, oldUpperBound, false);
					obs.removeListener(statusListener);
				}
			};
		}

		@Override
		protected void onAction(ActionEvent event) {
			player.stop();

			int auxiliaryNoteStartBeat = getAuxiliaryNoteStartBeat();
			int auxiliaryNoteEndBeat = auxiliaryNoteStartBeat + getAuxiliaryNoteLength();
			adjustVisibleArea(auxiliaryNoteStartBeat, auxiliaryNoteEndBeat);
			player.play(beatMillisConverter.beatToMillis(auxiliaryNoteStartBeat),
					beatMillisConverter.beatToMillis(auxiliaryNoteEndBeat), null, Mode.AUDIO_ONLY);
		}

		private void adjustVisibleArea(int auxiliaryNoteStartBeat, int auxiliaryNoteEndBeat) {
			oldLowerBound = visibleArea.getLowerXBound();
			oldUpperBound = visibleArea.getUpperXBound();
			int newLowerBound = Math.min(oldLowerBound, auxiliaryNoteStartBeat);
			int newUpperBound = Math.max(oldUpperBound, auxiliaryNoteEndBeat);
			if (newLowerBound != oldLowerBound || newUpperBound != oldUpperBound) {
				setVisibleAreaXBounds(newLowerBound, newUpperBound, false);
				player.statusProperty().addListener(statusListener);
			}
		}

		protected int getAuxiliaryNoteLength() {
			return (int) (beatMillisConverter.getBpm() / 100) + 1;
		}

		protected abstract int getAuxiliaryNoteStartBeat();
	}

	private class PlayAuxiliaryNoteBeforeSelectionAction extends PlayAuxiliaryNoteAction {

		@Override
		protected int getAuxiliaryNoteStartBeat() {
			return selection.getFirst().get().getStart() + 1 - getAuxiliaryNoteLength();
		}

	}

	private class PlayAuxiliaryNoteAfterSelectionAction extends PlayAuxiliaryNoteAction {

		@Override
		protected int getAuxiliaryNoteStartBeat() {
			return selection.getLast().get().getEnd() - 1;
		}

	}

	private class StopPlaybackAction extends KarediAction {

		private StopPlaybackAction() {
			setDisabledCondition(player.statusProperty().isNotEqualTo(Status.PLAYING));
		}

		@Override
		protected void onAction(ActionEvent event) {
			// player.stop();
			setMarkerTime(getMarkerTime());
		}

	}

	private class ToggleTicksAction extends KarediAction {

		private ToggleTicksAction() {
			setSelected(player.isTickingEnabled());
		}

		@Override
		protected void onAction(ActionEvent event) {
			player.setTickingEnabled(!player.isTickingEnabled());
		}

	}

	private abstract class ClipboardAction extends KarediAction {

		protected Song buildSong(String[] lines) {
			SongBuilder builder = new BasicSongBuilder();
			Arrays.asList(lines).forEach(line -> {
				try {
					builder.buildPart(parser.parse(line));
				} catch (InvalidSongElementException e) {
					// ignore
				}
			});
			return builder.getResult();
		}

		protected String[] getLinesFromClipboard() {
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			if (clipboard.getString() == null) {
				return new String[0];
			}
			return clipboard.getString().split("\\R");
		}
	}



	private class RenameAction extends KarediAction {

		private RenameAction() {
			setDisabledCondition(songContext.activeSongIsNullProperty());
		}

		@Override
		protected void onAction(ActionEvent event) {
			EditFilenamesDialog dialog = new EditFilenamesDialog();

			getSong().getTagValue(TagKey.ARTIST).ifPresent(value -> dialog.setSongArtist(value));
			getSong().getTagValue(TagKey.TITLE).ifPresent(value -> dialog.setSongTitle(value));
			getSong().getTagValue(TagKey.MP3).ifPresent(value -> dialog.setAudioFilename(value));
			getSong().getTagValue(TagKey.COVER).ifPresent(value -> dialog.setCoverFilename(value));

			Optional<String> optVideoFilename = getSong().getTagValue(TagKey.VIDEO);
			if (optVideoFilename.isPresent()) {
				dialog.setVideoFilename(optVideoFilename.get());
			} else {
				dialog.hideVideo();
			}

			Optional<String> optBackgroundFilename = getSong().getTagValue(TagKey.BACKGROUND);
			if (optBackgroundFilename.isPresent()) {
				dialog.setBackgroundFilename(optBackgroundFilename.get());
			} else {
				dialog.hideBackground();
			}

			Optional<FilenamesEditResult> optionalResult = dialog.showAndWait();
			optionalResult.ifPresent(result -> execute(commandFromResults(result)));
		}

		private Command commandFromResults(FilenamesEditResult result) {
			return new CommandComposite(I18N.get("ui.common.rename")) {

				@Override
				protected void buildSubCommands() {
					addSubCommand(new ChangeTagValueCommand(getSong(), TagKey.ARTIST,
							result.getArtist()));
					addSubCommand(
							new ChangeTagValueCommand(getSong(), TagKey.TITLE, result.getTitle()));
					addSubCommand(new ChangeTagValueCommand(getSong(), TagKey.MP3,
							result.getAudioFilename()));
					addSubCommand(new ChangeTagValueCommand(getSong(), TagKey.COVER,
							result.getCoverFilename()));
					result.getBackgroundFilename().ifPresent(filename -> {
						addSubCommand(
								new ChangeTagValueCommand(getSong(), TagKey.BACKGROUND, filename));
					});
					result.getVideoFilename().ifPresent(filename -> {
						addSubCommand(new ChangeTagValueCommand(getSong(), TagKey.VIDEO, filename));
					});
				}
			};
		}
	}

	private class ExportTracksAction extends KarediAction {
		private int trackCount;

		private ExportTracksAction(int trackCount) {
			this.trackCount = trackCount;
			songContext.activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
				if (newVal != null) {
					setDisabledCondition(newVal.trackCount().lessThan(trackCount));
				} else {
					setDisabledCondition(true);
				}
			});
			setDisabledCondition(true);
		}

		@Override
		protected void onAction(ActionEvent event) {
			if (getSong().getProblems().size() > 0) {
				new ExportWithErrorsAlert().showAndWait().filter(result -> result == ButtonType.OK)
						.ifPresent(ok -> export());
			} else {
				export();
			}
		}

		private void export() {
			List<SongTrack> tracks = getSong().getTracks();
			if (tracks.size() != trackCount) {
				ChooseTracksDialog dialog = new ChooseTracksDialog(tracks, trackCount);
				dialog.select(getActiveTrack());
				Optional<List<SongTrack>> result = dialog.showAndWait();
				if (result.isPresent()) {
					tracks = result.get();
				} else {
					return;
				}
			}

			File file = KarediApp.getInstance().getTxtFileToSave(getInitialFileName());
			songSaver.exportToFile(file, getSong().getTags(), tracks);
		}

		private String getInitialFileName() {
			File file = getActiveFile();
			return file == null ? "" : file.getName();
		}
	}

	private abstract class TagAction extends KarediAction {

		private TagAction() {
			setDisabledCondition(songContext.activeSongIsNullProperty());
		}

	}

	private class NewSongAction extends KarediAction {
		private Song song;
		private File audioFile;
		private File outputDir;

		@Override
		protected void onAction(ActionEvent event) {
			if (needsSaving()) {
				boolean proceed = KarediApp.getInstance().saveChangesIfUserWantsTo();
				if (!proceed) {
					return;
				}
			}
			new NewSongWizard().start().ifPresent(result -> {
				song = result.getSong();
				audioFile = result.getAudioFile();
				outputDir = result.getOutputDir();
				finish();
			});
		}

		private boolean finish() {
			reset(true);
			if (outputDir == null && audioFile != null) {
				loadAudioFile(audioFile);
			}
			setSong(song);
			if (outputDir != null) {
				File songFolder = new File(outputDir, getSongFilename());
				if ((songFolder.exists() || songFolder.mkdirs()) && songFolder.canWrite()) {
					LOGGER.info(I18N.get("creator.subfolder.success"));
					KarediApp.getInstance().setInitialDirectory(songFolder);
					copyAudioFile(songFolder);
					createTxtFile(songFolder);
				} else {
					LOGGER.severe(I18N.get("creator.subfolder.fail"));
				}
			}
			return true;
		}

		private void createTxtFile(File songFolder) {
			File txtFile = new File(songFolder, getSongFilename() + ".txt");
			if (canProceedToWriteFile(txtFile)) {
				saveSongToFile(txtFile);
				setActiveFile(txtFile);
			}
		}

		private void copyAudioFile(File songFolder) {
			if (audioFile != null) {
				song.getTagValue(TagKey.MP3).ifPresent(audioFilename -> {
					File newAudioFile = new File(songFolder, audioFilename);
					if (canProceedToWriteFile(newAudioFile)) {
						try {
							Files.copy(audioFile.toPath(), newAudioFile.toPath(),
									StandardCopyOption.REPLACE_EXISTING);
						} catch (Exception e) {
							LOGGER.warning(I18N.get("creator.copy_audio.fail"));
							e.printStackTrace();
						}
						loadAudioFile(newAudioFile);
					}
				});
			}
		}

		private boolean canProceedToWriteFile(File file) {
			if (file.exists()) {
				return new OverwriteAlert(file).showAndWait().filter(type -> type == ButtonType.OK)
						.map(type -> true).orElse(false);
			}
			return true;
		}

		private String getSongFilename() {
			StringBuilder sb = new StringBuilder();
			sb.append(song.getTagValue(TagKey.ARTIST).get());
			sb.append(" - ");
			sb.append(song.getTagValue(TagKey.TITLE).get());
			return sb.toString().replaceAll(ForbiddenCharacterRegex.FOR_FILENAME, "");
		}
	}


}