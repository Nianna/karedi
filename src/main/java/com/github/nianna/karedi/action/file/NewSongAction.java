package main.java.com.github.nianna.karedi.action.file;

import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.dialog.OverwriteAlert;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import main.java.com.github.nianna.karedi.util.ForbiddenCharacterRegex;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static main.java.com.github.nianna.karedi.action.KarediActions.NEW;

@Component
class NewSongAction extends KarediAction {
    private final AppContext appContext;
    private Song song;
    private File audioFile;
    private File outputDir;

    NewSongAction(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (appContext.needsSaving()) {
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
        appContext.reset(true);
        if (outputDir == null && audioFile != null) {
            appContext.loadAudioFile(audioFile);
        }
        appContext.setSong(song);
        if (outputDir != null) {
            File songFolder = new File(outputDir, getSongFilename());
            if ((songFolder.exists() || songFolder.mkdirs()) && songFolder.canWrite()) {
                AppContext.LOGGER.info(I18N.get("creator.subfolder.success"));
                KarediApp.getInstance().setInitialDirectory(songFolder);
                copyAudioFile(songFolder);
                createTxtFile(songFolder);
            } else {
                AppContext.LOGGER.severe(I18N.get("creator.subfolder.fail"));
            }
        }
        return true;
    }

    private void createTxtFile(File songFolder) {
        File txtFile = new File(songFolder, getSongFilename() + ".txt");
        if (canProceedToWriteFile(txtFile)) {
            appContext.saveSongToFile(txtFile);
            appContext.setActiveFile(txtFile);
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
                        AppContext.LOGGER.warning(I18N.get("creator.copy_audio.fail"));
                        e.printStackTrace();
                    }
                    appContext.loadAudioFile(newAudioFile);
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
        String filename = song.getTagValue(TagKey.ARTIST).get() + " - " + song.getTagValue(TagKey.TITLE).get();
        return filename.replaceAll(ForbiddenCharacterRegex.FOR_FILENAME, "");
    }

    @Override
    public KarediActions handles() {
        return NEW;
    }
}
