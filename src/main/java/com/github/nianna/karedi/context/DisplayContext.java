package main.java.com.github.nianna.karedi.context;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;
import main.java.com.github.nianna.karedi.util.ListenersUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DisplayContext {

    private final BeatRange beatRange;
    private final VisibleArea visibleArea;
    private final BeatMillisConverter beatMillisConverter;

    private final ReadOnlyObjectWrapper<Song> activeSong = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<SongTrack> activeTrack = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<SongLine> activeLine = new ReadOnlyObjectWrapper<>();

    private BooleanBinding activeSongIsNull = activeSongProperty().isNull();
    private BooleanBinding activeTrackIsNull = activeTrackProperty().isNull();
    private IntegerProperty activeSongTrackCount = new SimpleIntegerProperty();
    private BooleanBinding activeSongHasOneOrZeroTracks = activeSongTrackCount.lessThanOrEqualTo(1);

    private final ListChangeListener<? super SongLine> lineListChangeListener = ListenersUtils
            .createListContentChangeListener(ListenersUtils::pass, this::onLineRemoved);

    private final InvalidationListener beatMillisConverterInvalidationListener = obs -> onBeatMillisConverterInvalidated();

    public DisplayContext(BeatRange beatRange, VisibleArea visibleArea, BeatMillisConverter beatMillisConverter) {
        this.beatRange = beatRange;
        this.visibleArea = visibleArea;
        this.beatMillisConverter = beatMillisConverter;
    }

    public Song getActiveSong() {
        return activeSong.get();
    }

    public ReadOnlyObjectProperty<Song> activeSongProperty() {
        return activeSong.getReadOnlyProperty();
    }

    public SongTrack getActiveTrack() {
        return activeTrack.get();
    }

    public ReadOnlyObjectProperty<SongTrack> activeTrackProperty() {
        return activeTrack.getReadOnlyProperty();
    }

    public SongLine getActiveLine() {
        return activeLine.get();
    }

    public ReadOnlyObjectProperty<SongLine> activeLineProperty() {
        return activeLine.getReadOnlyProperty();
    }

    public BooleanBinding activeSongIsNullProperty() {
        return activeSongIsNull;
    }

    public BooleanBinding activeTrackIsNullProperty() {
        return activeTrackIsNull;
    }

    public BooleanBinding activeSongHasOneOrZeroTracksProperty() {
        return activeSongHasOneOrZeroTracks;
    }

    void setActiveSong(Song newSong) {
        Song oldSong = getActiveSong();
        activeSong.set(newSong);

        onBeatMillisConverterInvalidated();
        if (oldSong != null) {
            activeSongTrackCount.unbind();
            oldSong.getBeatMillisConverter().removeListener(beatMillisConverterInvalidationListener);
        }

        if (newSong == null) {
            setActiveTrack(null);
            activeSongTrackCount.set(0);
            visibleArea.setDefault();
        } else {
            newSong.getBeatMillisConverter().addListener(beatMillisConverterInvalidationListener);
            activeSongTrackCount.bind(newSong.trackCount());
            setActiveTrack(newSong.getDefaultTrack().orElse(null));
        }
        beatRange.setBounds(newSong);
    }

    public final void setActiveTrack(SongTrack newTrack) {
        SongTrack oldTrack = getActiveTrack();
        if (newTrack != oldTrack) {
            if (oldTrack != null) {
                oldTrack.removeLineListListener(lineListChangeListener);
            }
            if (newTrack != null) {
                newTrack.addLineListListener(lineListChangeListener);
            }
            activeTrack.set(newTrack);
            if (oldTrack == null) {
                setActiveLine(newTrack.getDefaultLine());
            } else {
                setActiveLine(null);
            }
        }
    }

    public void setActiveLine(SongLine newLine) {
        activeLine.set(newLine);
    }

    private void onLineRemoved(SongLine line) {
        if (line == getActiveLine()) {
            setActiveLine(null);
        }
    }

    private void onBeatMillisConverterInvalidated() {
        if (getActiveSong() == null) {
            beatMillisConverter.setBpm(Song.DEFAULT_BPM);
            beatMillisConverter.setGap(Song.DEFAULT_GAP);
        } else {
            beatMillisConverter.setBpm(getActiveSong().getBpm());
            beatMillisConverter.setGap(getActiveSong().getGap());
        }
    }

    public void invalidateVisibleArea() {
        visibleArea.invalidate();
    }

    public void assertAllNeededTonesVisible() {
        assertAllNeededTonesVisible(visibleArea.getLowerXBound(), visibleArea.getUpperXBound());
    }

    private void assertAllNeededTonesVisible(int fromBeat, int toBeat) {
        List<Note> notes = getActiveSong().getVisibleNotes(fromBeat, toBeat);
        visibleArea.assertBoundsYVisible(visibleArea.addMargins(new BoundingBox<>(notes)));
    }

    public void assertTonesVisible(List<? extends Note> notes) {
        if (!notes.isEmpty()) {
            visibleArea.assertBoundsYVisible(visibleArea.addMargins(new BoundingBox<>(notes)));
        }
    }

    public void setVisibleAreaXBounds(int lowerXBound, int upperXBound) {
        setVisibleAreaXBounds(lowerXBound, upperXBound, true);
    }

    public void setVisibleAreaXBounds(int lowerXBound, int upperXBound, boolean setLineToNull) {
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

    public IntBounded getVisibleAreaBounds() {
        return visibleArea;
    }

    public void moveVisibleArea(Direction direction, int by) {
        visibleArea.move(direction, by);
        setActiveLine(null);
    }

    public void setBounds(IntBounded bounds) {
        visibleArea.setBounds(bounds);
    }

    public void setBoundsWithMargin(IntBounded bounds) {
        visibleArea.setBounds(visibleArea.addMargins(bounds));
    }

    public boolean assertBorderlessBoundsVisible(IntBounded bounded) {
        return visibleArea.assertBorderlessBoundsVisible(bounded);
    }

    public IntBounded addMargins(BoundingBox<Note> boundingBox) {
        return visibleArea.addMargins(boundingBox);
    }

    public void adjustToBounds(IntBounded bounds) {
        visibleArea.adjustToBounds(bounds);
    }

    public void setMaxTime(Long maxTime) {
        beatRange.setMaxTime(maxTime);
    }

    public void reset() {
        setActiveSong(null);
    }
}
