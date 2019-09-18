package main.java.com.github.nianna.karedi.guard;

import javafx.beans.Observable;
import main.java.com.github.nianna.karedi.audio.CachedAudioFile;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import org.springframework.stereotype.Component;

@Component
class DisplayRangeConsistentWithActiveAudioGuard implements Guard {

    private final SongPlayer songPlayer;
    private final DisplayContext displayContext;

    DisplayRangeConsistentWithActiveAudioGuard(SongPlayer songPlayer, DisplayContext displayContext) {
        this.songPlayer = songPlayer;
        this.displayContext = displayContext;
    }

    @Override
    public void enable() {
        songPlayer.activeAudioFileProperty().addListener(this::onActiveAudioFileChanged);
    }

    @Override
    public void disable() {
        songPlayer.activeAudioFileProperty().removeListener(this::onActiveAudioFileChanged);
    }

    private void onActiveAudioFileChanged(Observable observable) {
        CachedAudioFile activeAudioFile = songPlayer.getActiveAudioFile();
        displayContext.setMaxTime(activeAudioFile != null ? activeAudioFile.getDuration() : null);
    }
}
