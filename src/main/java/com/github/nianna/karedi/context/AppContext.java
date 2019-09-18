package main.java.com.github.nianna.karedi.context;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.ActionManager;
import main.java.com.github.nianna.karedi.action.ActionMap;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.audio.AudioFileLoader;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.CommandHistory;
import main.java.com.github.nianna.karedi.guard.Guard;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
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

    private final ReadOnlyObjectWrapper<File> activeFile = new ReadOnlyObjectWrapper<>();

    private final ObjectProperty<Command> lastSavedCommand = new SimpleObjectProperty<>();

    private final ActionHelper actionHelper = new ActionHelper();

    @Autowired
    private DisplayContext displayContext;

    @Autowired
    private SongLoader songLoader;

    @Autowired
    private SongSaver songSaver;

    @Autowired
    private CommandHistory history;

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

    public BooleanBinding hasNoChangesProperty() {
        return lastSavedCommand.isEqualTo(history.activeCommandProperty());
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
                lastSavedCommand.set(history.getActiveCommand());
                return true;
            }
        }
        return false;
    }

    public Song getSong() {
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
        return getSong() != null && lastSavedCommand.get() != history.getActiveCommand();
    }

    public Logger getMainLogger() {
        return LOGGER;
    }

    public void reset(boolean resetPlayer) {
        lastSavedCommand.set(null);
        history.clear();
        player.stop();
        displayContext.reset();
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