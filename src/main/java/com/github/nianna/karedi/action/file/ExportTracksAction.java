package main.java.com.github.nianna.karedi.action.file;

import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongSaver;
import main.java.com.github.nianna.karedi.dialog.ChooseTracksDialog;
import main.java.com.github.nianna.karedi.dialog.ExportWithErrorsAlert;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongTrack;

import java.io.File;
import java.util.List;
import java.util.Optional;

class ExportTracksAction extends KarediAction {
    private final KarediActions handledAction;
    private final int trackCount;
    private final AppContext appContext;
    private final DisplayContext displayContext;
    private final SongSaver songSaver;

    ExportTracksAction(KarediActions handledAction, int trackCount, AppContext appContext, DisplayContext displayContext, SongSaver songSaver) {
        this.handledAction = handledAction;
        this.trackCount = trackCount;
        this.appContext = appContext;
        this.displayContext = displayContext;
        this.songSaver = songSaver;
        this.displayContext.activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
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
        if (displayContext.getActiveSong().getProblems().size() > 0) {
            new ExportWithErrorsAlert().showAndWait().filter(result -> result == ButtonType.OK)
                    .ifPresent(ok -> export());
        } else {
            export();
        }
    }

    private void export() {
        Song activeSong = displayContext.getActiveSong();
        List<SongTrack> tracks = activeSong.getTracks();
        if (tracks.size() != trackCount) {
            ChooseTracksDialog dialog = new ChooseTracksDialog(tracks, trackCount);
            dialog.select(displayContext.getActiveTrack());
            Optional<List<SongTrack>> result = dialog.showAndWait();
            if (result.isPresent()) {
                tracks = result.get();
            } else {
                return;
            }
        }

        File file = KarediApp.getInstance().getTxtFileToSave(getInitialFileName());
        songSaver.exportToFile(file, activeSong.getTags(), tracks);
    }

    private String getInitialFileName() {
        File file = appContext.getActiveFile();
        return file == null ? "" : file.getName();
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}