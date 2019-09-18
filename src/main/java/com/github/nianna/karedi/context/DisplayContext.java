package main.java.com.github.nianna.karedi.context;

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
import main.java.com.github.nianna.karedi.util.ListenersUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DisplayContext {

    private final VisibleArea visibleArea;

    private final ReadOnlyObjectWrapper<Song> activeSong = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<SongTrack> activeTrack = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<SongLine> activeLine = new ReadOnlyObjectWrapper<>();

    private BooleanBinding activeSongIsNull = activeSongProperty().isNull();
    private BooleanBinding activeTrackIsNull = activeTrackProperty().isNull();
    private IntegerProperty activeSongTrackCount = new SimpleIntegerProperty();
    private BooleanBinding activeSongHasOneOrZeroTracks = activeSongTrackCount.lessThanOrEqualTo(1);

    private final ListChangeListener<? super SongLine> lineListChangeListener = ListenersUtils
            .createListContentChangeListener(ListenersUtils::pass, this::onLineRemoved);

    public DisplayContext(VisibleArea visibleArea) {
        this.visibleArea = visibleArea;
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

    public void setActiveSong(Song newSong) {
        Song oldSong = getActiveSong();
        activeSong.set(newSong);

        if (oldSong != null) {
            activeSongTrackCount.unbind();
        }

        if (newSong == null) {
            setActiveTrack(null);
            activeSongTrackCount.set(0);
        } else {
            setActiveTrack(newSong.getDefaultTrack().orElse(null));
            activeSongTrackCount.bind(newSong.trackCount());
        }
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

    public void invalidateVisibleArea() {
        visibleArea.invalidate();
    }

    public void assertAllNeededTonesVisible() {
        assertAllNeededTonesVisible(visibleArea.getLowerXBound(), visibleArea.getUpperXBound());
    }

    public void assertAllNeededTonesVisible(int fromBeat, int toBeat) {
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


    public void assertBorderlessBoundsVisible(BoundingBox<Note> boundingBox) {
        visibleArea.assertBorderlessBoundsVisible(boundingBox);
    }

    public IntBounded addMargins(BoundingBox<Note> boundingBox) {
        return visibleArea.addMargins(boundingBox);
    }

    public void adjustToBounds(IntBounded bounds) {
        visibleArea.adjustToBounds(bounds);
    }
}
