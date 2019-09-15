package main.java.com.github.nianna.karedi.context;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.util.ListenersUtils;
import org.springframework.stereotype.Component;

@Component
public class SongState {

    private final ReadOnlyObjectWrapper<Song> activeSong = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<SongTrack> activeTrack = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<SongLine> activeLine = new ReadOnlyObjectWrapper<>();

    private BooleanBinding activeSongIsNull = activeSongProperty().isNull();

    private BooleanBinding activeTrackIsNull = activeTrackProperty().isNull();

    private final ListChangeListener<? super SongLine> lineListChangeListener = ListenersUtils
            .createListContentChangeListener(ListenersUtils::pass, this::onLineRemoved);

    public Song getActiveSong() {
        return activeSong.get();
    }

    public ReadOnlyObjectWrapper<Song> activeSongProperty() {
        return activeSong;
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
}
