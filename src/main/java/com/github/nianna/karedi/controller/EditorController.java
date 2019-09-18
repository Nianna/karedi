package main.java.com.github.nianna.karedi.controller;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.audio.Player.Status;
import main.java.com.github.nianna.karedi.command.*;
import main.java.com.github.nianna.karedi.context.*;
import main.java.com.github.nianna.karedi.display.MainChart;
import main.java.com.github.nianna.karedi.display.NoteNode;
import main.java.com.github.nianna.karedi.display.Piano;
import main.java.com.github.nianna.karedi.event.ControllerEvent;
import main.java.com.github.nianna.karedi.region.Bounded;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.Song.Medley;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.util.*;
import main.java.com.github.nianna.karedi.util.NodeUtils.DragSelectionHelper;
import main.java.com.github.nianna.karedi.util.NodeUtils.ResizeHelper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class EditorController implements Controller {
	private static final Color MARKER_COLOR_READY = Color.BLACK;
	private static final Color MARKER_COLOR_PLAYING = Color.GREEN;
	private static final Color MARKER_COLOR_TAPPING = Color.RED;
	private static final Color MARKER_COLOR_WRITING = Color.ORANGE;

	@FXML
	private AnchorPane pane;
	@FXML
	private HBox hBox;
	@FXML
	private MainChart chart;
	@FXML
	private Rectangle markerLine;
	@FXML
	private Rectangle medleyArea;
	@FXML
	private Piano piano;

	private AppContext appContext;
	private ObservableMap<Note, NoteNode> notesMap = FXCollections.observableMap(new HashMap<>());

	private ListChangeListener<? super Note> noteListChangeListener;
	private ListChangeListener<? super SongTrack> trackListChangeListener;

	private Medley medley;
	private InvalidationListener medleyChangeListener = this::onMedleyChanged;

	private boolean pianoReady = false;
	private boolean tapping = false;

	private DragSelectionHelper selectionHelper;
	private MarkerDragHelper markerLineDragHelper = new MarkerDragHelper();
	private NoteLengthChangeScheduler noteLengthChangeScheduler = new NoteLengthChangeScheduler();
	private NoteDrawer drawer = new NoteDrawer();

	private final NoteSelection selection;
	private final SongPlayer songPlayer;
	private final DisplayContext displayContext;
    private final CommandHistory history;
    private final CommandExecutor commandExecutor;
    private final BeatMillisConverter beatMillisConverter;

    public EditorController(NoteSelection selection, SongPlayer songPlayer, DisplayContext displayContext, CommandHistory history, CommandExecutor commandExecutor, BeatMillisConverter beatMillisConverter) {
        this.selection = selection;
        this.songPlayer = songPlayer;
        this.displayContext = displayContext;
        this.history = history;
        this.commandExecutor = commandExecutor;
		this.beatMillisConverter = beatMillisConverter;
	}

    @FXML
	public void initialize() {
		notesMap.addListener((MapChangeListener<? super Note, ? super NoteNode>) c -> {
			if (c.wasAdded()) {
				chart.getChartChildren().add(c.getValueAdded().getNode());
			}
			if (c.wasRemoved()) {
				chart.getChartChildren().remove(c.getValueRemoved().getNode());
			}
		});

		markerLine.heightProperty().bind(chart.getYAxis().heightProperty());
		medleyArea.heightProperty().bind(chart.getYAxis().heightProperty());
	}

	@Override
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
		displayContext.activeSongProperty().addListener(this::onSongChanged);
        displayContext.getVisibleAreaBounds().addListener(this::onVisibleAreaChanged);
		selection.get().addListener(
				ListenersUtils.createListContentChangeListener(this::select, this::deselect));
		displayContext.activeTrackProperty().addListener(this::onTrackChanged);
		songPlayer.statusProperty().addListener(this::onPlayerStatusChanged);

		noteListChangeListener = ListenersUtils.createListContentChangeListener(this::addNote,
				this::removeNote);
		trackListChangeListener = ListenersUtils.createListContentChangeListener(this::addTrack,
				this::removeTrack);

		configureChart();
		drawer.attachTo(chart); // has to be attached before selectionHelper
		selectionHelper = NodeUtils.enableRectangleSelection(chart);
		chart.addToPlotArea(selectionHelper.getSelectionAreaNode());
		selectionHelper.activeProperty().addListener(this::onUserSelectionChanged);
		configureMarkerLine();

		addActions();
        onVisibleAreaChanged(displayContext.getVisibleAreaBounds());
	}

	private void configureMarkerLine() {
		Tooltip.install(markerLine, markerLineTooltip());
		markerLineDragHelper.attachTo(markerLine);
	}

	private void configureChart() {
		chart.disableProperty().bind(displayContext.activeSongProperty().isNull());
		chart.getTAxis().tickLabelsVisibleProperty()
				.bind(displayContext.activeSongProperty().isNotNull());

		chart.getTAxis().lowerBoundProperty().bind(Bindings.createDoubleBinding(
				() -> beatMillisConverter.beatToMillis((int) chart.getXAxis().getLowerBound()) / 1000.0,
				chart.getXAxis().lowerBoundProperty(), beatMillisConverter
		));

		chart.getTAxis().upperBoundProperty().bind(Bindings.createDoubleBinding(
				() -> beatMillisConverter.beatToMillis((int) chart.getXAxis().getUpperBound()) / 1000.0,
				chart.getXAxis().upperBoundProperty(), beatMillisConverter)
		);

		markerLine.translateXProperty().bind(Bindings.createDoubleBinding(
				() -> tUnitLengthProperty().get() * MathUtils.msToSeconds(songPlayer.getMarkerTime()),
				songPlayer.markerTimeProperty(), tUnitLengthProperty())
		);
	}

	private void addTrack(SongTrack track) {
		track.getNotes().forEach(this::addNote);
		track.addNoteListListener(noteListChangeListener);
	}

	private void removeTrack(SongTrack track) {
		track.removeNoteListListener(noteListChangeListener);
		track.getNotes().forEach(this::removeNote);
	}

	private void addNote(Note note) {
		if (!notesMap.containsKey(note)) {
			NoteNode noteNode = new NoteNode(displayContext, selection, commandExecutor, this, note);
			notesMap.put(note, noteNode);
		}
	}

	private void removeNote(Note note) {
		notesMap.remove(note);
	}

	private Tooltip markerLineTooltip() {
		Tooltip markerLineTooltip = new Tooltip();
		markerLineTooltip.textProperty().bind(Bindings.createStringBinding(
				() -> I18N.get("editor.marker.time", MathUtils.msToSeconds(songPlayer.getMarkerTime())),
				songPlayer.markerTimeProperty())
		);
		return markerLineTooltip;
	}

	private void configurePiano() {
		NumberAxis yAxis = chart.getYAxis();

		double yShift = Math.abs(piano.localToScene(piano.getBoundsInParent()).getMinY()
				- yAxis.localToScene(yAxis.getBoundsInParent()).getMinY());
		piano.setTranslateY(yShift - yAxis.getBoundsInParent().getMinY());

		yAxis.boundsInParentProperty().addListener((obsVal, oldVal, newVal) -> {
			if (oldVal.getMinY() != newVal.getMinY()) {
				piano.setTranslateY(piano.getTranslateY() + newVal.getMinY() - oldVal.getMinY());
			}
		});
		piano.lowerBoundProperty().bind(yAxis.lowerBoundProperty());
		piano.upperBoundProperty().bind(yAxis.upperBoundProperty());
		piano.prefHeightProperty().bind(yAxis.heightProperty());

		piano.show();
		pianoReady = true;
	}

	private void addActions() {
		appContext.addAction(KarediActions.TOGGLE_PIANO, new TogglePianoVisibilityAction());
		appContext.addAction(KarediActions.TAP_NOTES, new TapNotesAction());
		appContext.addAction(KarediActions.WRITE_TONES, new WriteTonesAction());
	}

	public ReadOnlyDoubleProperty yUnitLengthProperty() {
		return chart.yUnitLengthProperty();
	}

	public ReadOnlyDoubleProperty xUnitLengthProperty() {
		return chart.xUnitLengthProperty();
	}

	public ReadOnlyDoubleProperty tUnitLengthProperty() {
		return chart.tUnitLengthProperty();
	}

	public int sceneXtoBeat(double sceneX) {
		double x = chart.getXAxis().sceneToLocal(sceneX, 0).getX();
		return Math.round(chart.getXAxis().getValueForDisplay(x).floatValue());
	}

	public int sceneYtoTone(double sceneY) {
		double y = chart.getYAxis().sceneToLocal(0, sceneY).getY();
		return Math.round(chart.getYAxis().getValueForDisplay(y).floatValue());
	}

	public long sceneXtoTime(double sceneX) {
		double x = chart.getTAxis().sceneToLocal(sceneX, 0).getX();
		return (long) (chart.getTAxis().getValueForDisplay(x).floatValue() * 1000);
	}

	@FXML
	private void onKeyPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ALT)) {
			event.consume();
			return;
		}

		if (event.getCode().isDigitKey()) {
			appContext.execute(KarediActions.STOP_PLAYBACK);
			KeyEventUtils.getPressedDigit(event).ifPresent(digit -> {
				selection.leaveOne();
				Note selected = selection.getFirst().orElse(null);
				noteLengthChangeScheduler.schedule(selected, digit);
			});
			event.consume();
		}
	}

	@Override
	public Node getContent() {
		return pane;
	}

	@Override
	public void requestFocus() {
		chart.requestFocus();
	}

	private void onUserSelectionChanged(Observable obs, boolean wasActive, boolean isActive) {
		if (!isActive) {
			chart.requestFocus();
			Point2D upperLeft = selectionHelper.getUpperLeftCorner();
			Point2D bottomRight = selectionHelper.getBottomRightCorner();
            Bounded<Integer> visibleArea = displayContext.getVisibleAreaBounds();
			int lowestBeat = Math.max(sceneXtoBeat(upperLeft.getX()), visibleArea.getLowerXBound());
			int highestBeat = Math.min(sceneXtoBeat(bottomRight.getX()),
					visibleArea.getUpperXBound());
			int lowestTone = Math.max(sceneYtoTone(bottomRight.getY()),
					visibleArea.getLowerYBound());
			int highestTone = Math.min(sceneYtoTone(upperLeft.getY()),
					visibleArea.getUpperYBound());

			List<Note> selectedNotes = displayContext.getActiveTrack().getNotes(lowestBeat, highestBeat)
					.stream()
					.filter(note -> note.getTone() <= highestTone && note.getTone() >= lowestTone)
					.collect(Collectors.toList());
			selection.set(selectedNotes);
		}
	}

	private void onSongChanged(Observable obs, Song oldSong, Song newSong) {
		notesMap.clear();

		if (!pianoReady) {
			configurePiano();
		}

		if (oldSong != null) {
			oldSong.removeTrackListListener(trackListChangeListener);
			medley.removeListener(medleyChangeListener);
		}
		if (newSong != null) {
			newSong.getTracks().forEach(track -> addTrack(track));
			newSong.addTrackListListener(trackListChangeListener);
			medley = newSong.getMedley();
			medley.addListener(medleyChangeListener);
			onMedleyChanged(medley);

			chart.requestFocus();
		}
	}

	private void onTrackChanged(Observable obs, SongTrack oldTrack, SongTrack newTrack) {
		if (newTrack != null) {
			newTrack.getLines().forEach(line -> {
				line.getNotes().forEach(note -> {
					notesMap.get(note).getNode().toFront();
				});
			});
		}
	}

	private void deselect(Note note) {
		if (notesMap.containsKey(note)) {
			notesMap.get(note).deselect();
		}
	}

	private void select(Note note) {
		if (notesMap.containsKey(note)) {
			notesMap.get(note).select();
		}
	}

	private void onVisibleAreaChanged(Observable obs) {
        Bounded<Integer> area = displayContext.getVisibleAreaBounds();
		chart.getXAxis().setLowerBound(area.getLowerXBound());
		chart.getXAxis().setUpperBound(area.getUpperXBound());
		chart.getYAxis().setLowerBound(area.getLowerYBound());
		chart.getYAxis().setUpperBound(area.getUpperYBound());
	}

	private void onPlayerStatusChanged(Observable obs, Status oldStatus, Status newStatus) {
		if (newStatus == Status.PLAYING) {
			if (tapping) {
				markerLine.setFill(MARKER_COLOR_TAPPING);
			} else {
				markerLine.setFill(MARKER_COLOR_PLAYING);
			}
		} else {
			markerLine.setFill(MARKER_COLOR_READY);
		}
	}

	private void onMedleyChanged(Observable obs) {
		if (medley.getSize() > 0) {
			medleyArea.widthProperty()
					.bind(Bindings.multiply(xUnitLengthProperty(), medley.getSize()));
			medleyArea.translateXProperty()
					.bind(Bindings.multiply(xUnitLengthProperty(), medley.getStartBeat()));
			medleyArea.translateYProperty()
					.bind(Bindings
							.multiply(chart.getYAxis().upperBoundProperty(), yUnitLengthProperty())
							.negate());
			medleyArea.setVisible(true);
		} else {
			medleyArea.setVisible(false);
		}
	}

	@FXML
	private void onScroll(ScrollEvent event) {
		boolean wheelDown = event.getDeltaY() < 0 || event.getDeltaX() < 0;

		if (event.isControlDown()) {
			int increaseBy = wheelDown ? 1 : -1;
			if (event.isShiftDown() || !event.isAltDown()) {
                displayContext.increaseVisibleAreaXBounds(increaseBy);
			}
			if (event.isAltDown() || !event.isShiftDown()) {
                displayContext.increaseVisibleAreaYBounds(increaseBy);
			}
			event.consume();
			return;
		}
		if (event.isAltDown()) {
			moveAreaVertically(wheelDown);
			event.consume();
			return;
		}
		if (event.isShiftDown()) {
			moveAreaHorizontally(wheelDown);
			event.consume();
			return;
		}
		changeLine(wheelDown);
		event.consume();
	}

	private void moveAreaVertically(boolean down) {
		if (down) {
			appContext.execute(KarediActions.MOVE_VISIBLE_AREA_DOWN);
		} else {
			appContext.execute(KarediActions.MOVE_VISIBLE_AREA_UP);
		}
	}

	private void moveAreaHorizontally(boolean right) {
		if (right) {
			appContext.execute(KarediActions.MOVE_VISIBLE_AREA_RIGHT);
		} else {
			appContext.execute(KarediActions.MOVE_VISIBLE_AREA_LEFT);
		}
	}

	private void changeLine(boolean next) {
		if (next) {
			appContext.execute(KarediActions.VIEW_NEXT_LINE);
		} else {
			appContext.execute(KarediActions.VIEW_PREVIOUS_LINE);
		}
	}

	@FXML
	private void onMouseClicked(MouseEvent event) {
		if (event.isStillSincePress()) {
			if (chart.isFocused()) {
				selection.clear();
				songPlayer.setMarkerTime(sceneXtoTime(event.getSceneX()));
			} else {
				chart.requestFocus();
			}
			event.consume();
		}
	}

	public int getBeat(MouseEvent event) {
		return sceneXtoBeat(event.getSceneX());
	}

	public int getTone(MouseEvent event) {
		return sceneYtoTone(event.getSceneY());
	}

	private class TogglePianoVisibilityAction extends KarediAction {

		public TogglePianoVisibilityAction() {
			super();
			setDisabledCondition(displayContext.activeSongProperty().isNull());
		}

		@Override
		protected void onAction(ActionEvent event) {
			if (piano != null) {
				piano.toggle();
			}
		}
	}

	private class TapNotesAction extends KarediAction {
		private InvalidationListener playerStatusListener;
		private InvalidationListener activeTrackListener;
		private EventHandler<? super KeyEvent> onKeyPressed;
		private EventHandler<? super KeyEvent> onKeyReleased;
		private Note lastNote;
		private SongLine line;
		private int tone;
		private Timer updateLengthTimer;
		private long updateInterval;

		private TapNotesAction() {
			setDisabledCondition(displayContext.activeTrackProperty().isNull()
					.or(songPlayer.activeAudioFileProperty().isNull()));
			playerStatusListener = (obs -> {
				if (songPlayer.getStatus() != Status.PLAYING) {
					tapping = false;
					lastNote = null;
					hBox.setOnKeyPressed(onKeyPressed);
					hBox.setOnKeyReleased(onKeyReleased);
					obs.removeListener(this.playerStatusListener);
					displayContext.activeTrackProperty().removeListener(activeTrackListener);
				}
			});
			activeTrackListener = (obs -> {
				line = null;
			});
		}

		@Override
		protected void onAction(ActionEvent event) {
			reset();
			selection.clear();
			onKeyPressed = hBox.getOnKeyPressed();
			onKeyReleased = hBox.getOnKeyReleased();
            displayContext.assertAllNeededTonesVisible();
			tone = getToneForTappedNote();
			updateInterval = getBeatDuration() / 2;
			tapping = true;
			displayContext.activeTrackProperty().addListener(activeTrackListener);
			appContext.execute(KarediActions.PLAY_VISIBLE_AUDIO);
			songPlayer.statusProperty().addListener(playerStatusListener);
			hBox.setOnKeyPressed(this::onKeyPressedWhileTapping);
			hBox.setOnKeyReleased(this::onKeyReleasedWhileTapping);
		}

		private void reset() {
			appContext.execute(KarediActions.STOP_PLAYBACK);
			lastNote = null;
			line = null;
			updateLengthTimer = null;
		}

		private void onKeyPressedWhileTapping(KeyEvent event) {
			if (isKeyForbidden(event)) {
				return;
			}
			if (lastNote != null) {
				// ignore keyPressed events generated while the key is being
				// held down
			} else {
				if (event.getCode() == KeyCode.ENTER) {
					line = null;
					event.consume();
					return;
				}
				if (event.getCode() == KeyCode.ESCAPE) {
					appContext.execute(KarediActions.STOP_PLAYBACK);
					event.consume();
					return;
				}

				lastNote = new Note(songPlayer.getMarkerBeat() - 1, 1, tone);

				// add new note to existing SongLine if possible
				displayContext.getActiveTrack().lineAt(songPlayer.getMarkerBeat())
						.ifPresent(markerLine -> line = markerLine);

				if (line == null || !displayContext.getActiveTrack().contains(line)) {
                    commandExecutor.execute(new AddNoteCommand(lastNote, displayContext.getActiveTrack()));
					line = lastNote.getLine();
				} else {
                    commandExecutor.execute(new AddNoteCommand(lastNote, line));
				}
				scheduleChangeNoteLengthTask();
			}
			event.consume();
		}

		private void onKeyReleasedWhileTapping(KeyEvent event) {
			if (isKeyForbidden(event)) {
				return;
			}
			lastNote = null;
			if (updateLengthTimer != null) {
				updateLengthTimer.cancel();
			}
			event.consume();
		}

		private boolean isKeyForbidden(KeyEvent event) {
			return event.getCode().isModifierKey() || KeyEventUtils.isAnyModifierDown(event)
					|| event.getCode().equals(KeyCode.TAB);
		}

		private void scheduleChangeNoteLengthTask() {
			if (lastNote != null) {
				updateLengthTimer = new Timer(true);
				updateLengthTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						Platform.runLater(() -> {
							if (lastNote != null) {
								lastNote.setLength(
										Math.max(songPlayer.getMarkerBeat() - lastNote.getStart(),
												lastNote.getLength()));
								scheduleChangeNoteLengthTask();
							}
						});
					}
				}, updateInterval);

			}
		}

		private long getBeatDuration() {
			Double duration = beatMillisConverter.getBeatDuration();
			return duration.longValue();
		}

		private int getToneForTappedNote() {
            int lowerTone = displayContext.getVisibleAreaBounds().getLowerYBound();
			return lowerTone + VisibleArea.BOTTOM_MARGIN;
		}
	}

	private class WriteTonesAction extends KarediAction {
		private InvalidationListener selectionSizeListener;
		private InvalidationListener chartFocusListener;
		private InvalidationListener finishWriting = obs -> finish();
		private ListChangeListener<? super Note> typedUpdater = ListenersUtils
				.createListContentChangeListener(this::remove, this::add);
		private EventHandler<? super KeyEvent> eventConsumer = keyEvent -> keyEvent.consume();
		private EventHandler<? super KeyEvent> onKeyPressed;
		private EventHandler<? super KeyEvent> onKeyReleased;

		private Command initialCommand;
		private KeyCode lastCode;
		private Stack<Note> typed = new Stack<>();

		private WriteTonesAction() {
			setDisabledCondition(selection.isEmptyProperty());

			selectionSizeListener = (obs -> {
				if (selection.size() == 0) {
					finish();
				}
			});

			chartFocusListener = obs -> {
				if (!chart.isFocused()) {
					finish();
				}
			};
		}

		private void remove(Note n) {
			typed.remove(n);
		}

		private void add(Note n) {
			if (!typed.contains(n)) {
				typed.add(n);
			}
		}

		@Override
		protected void onAction(ActionEvent event) {
			appContext.execute(KarediActions.STOP_PLAYBACK);
			typed = new Stack<>();
			backupState();
			chart.requestFocus();
			hBox.setOnKeyPressed(this::onKeyPressedWhileWriting);
			hBox.setOnKeyReleased(this::onKeyReleasedWhileWriting);
			addListeners();
			disableActions();
			markerLine.setFill(MARKER_COLOR_WRITING);
		}

		private void backupState() {
			initialCommand = history.getActiveCommand();
			onKeyPressed = hBox.getOnKeyPressed();
			onKeyReleased = hBox.getOnKeyReleased();
		}

		private void restoreEditorState() {
			hBox.setOnKeyPressed(onKeyPressed);
			hBox.setOnKeyReleased(onKeyReleased);
			markerLine.setFill(MARKER_COLOR_READY);
		}

		private void addListeners() {
			selection.sizeProperty().addListener(selectionSizeListener);
			selection.get().addListener(typedUpdater);
			chart.focusedProperty().addListener(chartFocusListener);
			songPlayer.statusProperty().addListener(finishWriting);
		}

		private void removeListeners() {
			selection.sizeProperty().removeListener(selectionSizeListener);
			selection.get().removeListener(typedUpdater);
			chart.focusedProperty().removeListener(chartFocusListener);
			songPlayer.statusProperty().removeListener(finishWriting);
		}

		private void disableActions() {
			pane.addEventHandler(KeyEvent.ANY, eventConsumer);
			pane.fireEvent(new ControllerEvent(ControllerEvent.DISABLE_ACTION_CONTROLLERS));
		}

		private void enableActions() {
			pane.removeEventHandler(KeyEvent.ANY, eventConsumer);
			pane.fireEvent(new ControllerEvent(ControllerEvent.ENABLE_ACTION_CONTROLLERS));
		}

		private void finish() {
			lastCode = null;
			typed = new Stack<>();
			removeListeners();
			restoreEditorState();
			enableActions();
		}

		private void execute(KarediActions action) {
			selection.sizeProperty().removeListener(selectionSizeListener);
			appContext.execute(action);
			selection.sizeProperty().addListener(selectionSizeListener);
		}

		private void onKeyPressedWhileWriting(KeyEvent event) {
			if (appContext.getAction(KarediActions.UNDO).wasFired(event)) {
				undo();
				event.consume();
				return;
			}
			if (appContext.getAction(KarediActions.REDO).wasFired(event)) {
				redo();
				event.consume();
				return;
			}
			if (appContext.getAction(KarediActions.TOGGLE_PIANO).wasFired(event)) {
				appContext.execute(KarediActions.TOGGLE_PIANO);
				event.consume();
				return;
			}
			if (event.getCode() != lastCode) {
				selection.getFirst().ifPresent(note -> {
					handleEventCode(note, event);
				});
			}
			event.consume();
		}

		private void handleEventCode(Note note, KeyEvent event) {
			switch (event.getCode()) {
			case LEFT:
				if (typed.size() > 0) {
					Note lastNote = typed.peek();
					selection.select(lastNote);
					piano.play(Arrays.asList(lastNote.getTone()));
				}
				return;
			case RIGHT:
				piano.play(Arrays.asList(note.getTone()));
				selection.deselect(note);
				return;
			case ESCAPE:
			case ENTER:
				finish();
				return;
			case CONTROL:
			case COMMAND:
			case META:
			case SHIFT:
			case ALT:
				event.consume();
				return;
			default:
				lastCode = event.getCode();
			}
			getTone(note, event).ifPresent(tone -> updateTone(note, tone));
		}

		private void updateTone(Note note, int newTone) {
			Command cmd = new ChangeToneCommand(note, newTone);
            commandExecutor.execute(new ChangePostStateCommandDecorator(cmd, c -> {
				selection.deselect(note);
			}));
			piano.play(Arrays.asList(note.getTone()));
			selection.deselect(note);
		}

		private void undo() {
			if (!appContext.canExecute(KarediActions.UNDO)
					|| history.getActiveCommand() == initialCommand) {
				finish();
			} else {
				execute(KarediActions.UNDO);
				selection.getFirst().ifPresent(note -> {
					piano.play(Arrays.asList(note.getTone()));
				});
			}
		}

		private void redo() {
			if (appContext.canExecute(KarediActions.REDO)) {
				Optional<Note> optNote = selection.getFirst();
				execute(KarediActions.REDO);
				optNote.ifPresent(note -> {
					piano.play(Arrays.asList(note.getTone()));
				});
			}
		}

		private Optional<Integer> getTone(Note note, KeyEvent event) {
			int tone = 0;
			switch (event.getCode()) {
			case C:
			case D:
			case F:
			case G:
			case A:
				if (event.isControlDown()) {
					tone += 1;
				}
				// fall through
			case E:
			case H:
				tone += MusicalScale
						.getIndex(MusicalScale.Note.valueOf(event.getCode().toString()));
				break;
			case B:
				tone = MusicalScale.getIndex(MusicalScale.Note.A_SHARP);
				break;
			case PLUS:
			case ADD:
				tone = note.getTone() + MusicalScale.INTERVAL_BETWEEN_SAME_TONES;
				break;
			case MINUS:
			case SUBTRACT:
				tone = note.getTone() - MusicalScale.INTERVAL_BETWEEN_SAME_TONES;
				break;
			default:
				return Optional.empty();
			}
			if (event.isShiftDown()) {
				tone += MusicalScale.INTERVAL_BETWEEN_SAME_TONES;
			}
			if (event.isAltDown()) {
				tone -= MusicalScale.INTERVAL_BETWEEN_SAME_TONES;
			}
			return Optional.of(tone);
		}

		private void onKeyReleasedWhileWriting(KeyEvent event) {
			lastCode = null;
			event.consume();
		}
	}

	private class NoteDrawer {
		private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper();
		private Note note;

		private void attachTo(Node node) {
			NodeUtils.addOnMousePressed(node, this::onMousePressed);
			NodeUtils.addOnMouseDragged(node, this::onMouseDragged);
			NodeUtils.addOnMouseReleased(node, this::onMouseReleased);
		}

		private void onMousePressed(MouseEvent event) {
			if (event.isShortcutDown() && displayContext.getActiveTrack() != null) {
				if (MusicalScale.isToneValid(getTone(event))) {
					note = new Note(getBeat(event), 1, getTone(event));
					Optional<SongLine> line = getLineForBeat(getBeat(event));
					Command cmd;
					if (line.isPresent()) {
						cmd = new AddNoteCommand(note, line.get());
					} else {
						cmd = new AddNoteCommand(note, displayContext.getActiveTrack());
					}
                    commandExecutor.execute(new ChangePostStateCommandDecorator(cmd, c -> {
						selection.selectOnly(note);
					}));
					setActive(true);
				}
				event.consume();
			}
		}

		private Optional<SongLine> getLineForBeat(int beat) {
			Optional<SongLine> prevLine = displayContext.getActiveTrack().lineAtOrEarlier(beat)
					.filter(this::isPreviousVisible);
			if (prevLine.isPresent()) {
				return prevLine;
			} else {
				Optional<SongLine> nextLine = displayContext.getActiveTrack().lineAtOrLater(beat)
						.filter(this::isNextVisible);
				return nextLine;
			}
		}

		private boolean isPreviousVisible(SongLine line) {
            return displayContext.getVisibleAreaBounds().inRangeX(line.getUpperXBound());
		}

		private boolean isNextVisible(SongLine line) {
            return displayContext.getVisibleAreaBounds().inRangeX(line.getLowerXBound());
		}

		private void onMouseDragged(MouseEvent event) {
			if (isActive()) {
				note.setLength(Math.max(1, getBeat(event) - note.getStart()));
				event.consume();
			}
		}

		private void onMouseReleased(MouseEvent event) {
			if (isActive()) {
				setActive(false);
				event.consume();
			}
		}

		private void setActive(boolean active) {
			this.active.set(active);
		}

		private boolean isActive() {
			return active.get();
		}

	}

	private class MarkerDragHelper {
		private ResizeHelper helper;
		private List<Note> notesToDrag;
		private int initialDistance;

		private void attachTo(Node node) {
			helper = NodeUtils.makeResizable(node, new Insets(0, 1, 0, 1));
			helper.activeProperty().addListener(this::onDragActiveInvalidated);
			NodeUtils.addOnMouseDragged(node, this::onMouseDragged);
		}

		private void onMouseDragged(MouseEvent event) {
			if (helper.isActive() && notesToDrag.size() > 0) {
				int curDistance = songPlayer.getMarkerBeat() - getBeat(event);
				if (songPlayer.getMarkerBeat() == notesToDrag.get(0).getStart()) {
					int moveBy = initialDistance - curDistance;
					if (moveBy != 0) {
						Direction direction = moveBy < 0 ? Direction.LEFT : Direction.RIGHT;
						Command cmd = new MoveCollectionCommand<Integer, Note>(notesToDrag,
								direction, Math.abs(moveBy));
                        commandExecutor.execute(cmd);
						// User moved notes that were not selected - it's
						// necessary to invalidate visibleArea to let others
						// know that some notes may no longer be visible
                        displayContext.invalidateVisibleArea();
					}
				} else {
					helper.deactivate();
				}
			}
		}

		private void onDragActiveInvalidated(Observable obs) {
			if (helper.isActive()) {
				int beat = songPlayer.getMarkerBeat();
				notesToDrag = displayContext.getActiveTrack().getNotes(beat);
				if (notesToDrag.size() > 0) {
					selection.selectOnly(notesToDrag.get(0));
					initialDistance = songPlayer.getMarkerBeat() - beat;
				} else {
					helper.deactivate();
				}
			}
		}
	}

	private class NoteLengthChangeScheduler {
		private final static int TIME_LIMIT = 300;
		private Timer keyPressedTimer = new Timer();
		private int newLength = 0;
		private Note currentlyResizedNote = null;

		private void schedule(Note note, int newDigit) {
			if (note != null) {
				if (note == currentlyResizedNote) {
					keyPressedTimer.cancel();
					keyPressedTimer = new Timer(true);
				} else {
					newLength = 0;
					currentlyResizedNote = note;
				}
				newLength = 10 * newLength + newDigit;
				keyPressedTimer.schedule(new ChangeNoteLengthTimerTask(this, note, newLength),
						TIME_LIMIT);
			}
		}

		private void reset() {
			currentlyResizedNote = null;
			newLength = 0;
		}
	}

	private class ChangeNoteLengthTimerTask extends TimerTask {
		private Note note;
		private int length;
		private NoteLengthChangeScheduler scheduler;

		private ChangeNoteLengthTimerTask(NoteLengthChangeScheduler scheduler, Note note,
				int length) {
			this.note = note;
			this.length = length;
			this.scheduler = scheduler;
		}

		@Override
		public void run() {
			Platform.runLater(() -> {
				if (length > 0 && note != null) {
					List<Note> currentSelection = new ArrayList<>(selection.get());
					selection.selectOnly(note);
                    commandExecutor.execute(new ResizeNotesCommand(Arrays.asList(note), Direction.RIGHT,
							length - note.getLength()));
					selection.set(currentSelection);
				}
				scheduler.reset();
			});
		}
	}
}