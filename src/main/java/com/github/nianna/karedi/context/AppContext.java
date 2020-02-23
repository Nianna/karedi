package main.java.com.github.nianna.karedi.context;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.ActionManager;
import main.java.com.github.nianna.karedi.audio.AudioFileLoader;
import main.java.com.github.nianna.karedi.guard.Guard;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

@Component
public class AppContext {
    public static final Logger LOGGER = Logger.getLogger(KarediApp.class.getPackage().getName()); //TODO refactor

    private final ReadOnlyObjectWrapper<File> activeFile = new ReadOnlyObjectWrapper<>();

    @Autowired
    private DisplayContext displayContext;

    @Autowired
    private SongLoader songLoader;

    @Autowired
    private SongChangesContext songChangesContext;

    @Autowired
    private SongSaver songSaver;

    @Autowired
    private SongPlayer player;

    @Autowired
    private List<Guard> guards;

    @Autowired
    private ActionManager actionManager;

    private File directory;

    // Convenience bindings for actions
    private BooleanBinding activeFileIsNull;

    public AppContext() {
        LOGGER.setUseParentHandlers(false);
    }

    public BooleanBinding hasNoChangesToBeSavedProperty() {
        return displayContext.activeSongIsNullProperty().or(songChangesContext.hasNoChangesProperty());
    }

    public BooleanBinding activeFileIsNullProperty() {
        return activeFileIsNull;
    }

    @PostConstruct
    public void initAppContext() {
        activeFileIsNull = activeFileProperty().isNull();

        LOGGER.setUseParentHandlers(false);

        guards.forEach(Guard::enable);
    }

    public void loadAudioFile(File file) {
        AudioFileLoader.loadMp3File(file, (newAudio -> {
            if (newAudio.isPresent()) {
                player.addAudioFile(newAudio.get());
                player.setActiveAudioFile(newAudio.get());
                LOGGER.info(I18N.get("import.audio.success"));
            } else {
                LOGGER.severe(I18N.get("import.audio.fail"));
            }
        }));
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
                songChangesContext.persistChanges();
                return true;
            }
        }
        return false;
    }

    private Song getSong() {
        return displayContext.activeSongProperty().get();
    }

    public final void setSong(Song song) {
        Song oldSong = getSong();
        new SongNormalizer(song).normalize();
        // The song has at least one track now
        if (song != oldSong) {
            displayContext.setActiveSong(song);
            player.setSong(song);
        }
    }

    // Other
    public boolean needsSaving() {
        return getSong() != null && songChangesContext.hasChanges();
    }

    public Logger getMainLogger() {
        return LOGGER;
    }

    public void reset(boolean resetPlayer) {
        songChangesContext.abandonChanges();
        player.stop();
        displayContext.reset();
        if (resetPlayer) {
            player.reset();
        }
    }
}