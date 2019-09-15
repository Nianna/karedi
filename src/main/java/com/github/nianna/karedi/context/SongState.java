package main.java.com.github.nianna.karedi.context;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import org.springframework.stereotype.Component;

@Component
public class SongState {

    private final ReadOnlyObjectWrapper<Song> activeSong = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<SongTrack> activeTrack = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<SongLine> activeLine = new ReadOnlyObjectWrapper<>();

    private BooleanBinding activeSongIsNull = activeSongProperty().isNull();

    private BooleanBinding activeTrackIsNull = activeTrackProperty().isNull();

    public Song getActiveSong() {
        return activeSong.get();
    }

    public ReadOnlyObjectWrapper<Song> activeSongProperty() {
        return activeSong;
    }

    public SongTrack getActiveTrack() {
        return activeTrack.get();
    }

    public ReadOnlyObjectWrapper<SongTrack> activeTrackProperty() {
        return activeTrack;
    }

    public SongLine getActiveLine() {
        return activeLine.get();
    }

    public ReadOnlyObjectWrapper<SongLine> activeLineProperty() {
        return activeLine;
    }

    public BooleanBinding activeSongIsNullProperty() {
        return activeSongIsNull;
    }

    public BooleanBinding activeTrackIsNullProperty() {
        return activeTrackIsNull;
    }
}
