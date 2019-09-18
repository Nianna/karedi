package main.java.com.github.nianna.karedi.action.file;

import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.song.SongTrack;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static main.java.com.github.nianna.karedi.action.KarediActions.RELOAD;

@Component
class ReloadSongAction extends KarediAction {
    private final AppContext appContext;
    private final DisplayContext displayContext;
    private Integer trackNumber;
    private Integer lineNumber;
    private List<Color> colors;

    ReloadSongAction(AppContext appContext, DisplayContext displayContext) {
        this.appContext = appContext;
        this.displayContext = displayContext;
        setDisabledCondition(appContext.activeFileIsNullProperty().or(this.displayContext.activeSongIsNullProperty()));
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (KarediApp.getInstance().saveChangesIfUserWantsTo()) {
            backupTrackAndLine();
            backupColors();
            appContext.loadSongFile(appContext.getActiveFile(), false);

            if (displayContext.getActiveSong() != null) {
                restoreTrackAndLine();
                restoreColors();
            }
        }
    }

    private void backupTrackAndLine() {
        trackNumber = null;
        lineNumber = null;
        if (displayContext.getActiveTrack() != null) {
            trackNumber = displayContext.getActiveSong().indexOf(displayContext.getActiveTrack());
            if (displayContext.getActiveLine() != null) {
                lineNumber = displayContext.getActiveTrack().indexOf(displayContext.getActiveLine());
            }
        }
    }

    private void backupColors() {
        colors = displayContext.getActiveSong().getTracks().stream().map(SongTrack::getColor)
                .collect(Collectors.toList());
    }

    private void restoreTrackAndLine() {
        if (trackNumber != null && displayContext.getActiveSong().size() > trackNumber) {
            displayContext.setActiveTrack(displayContext.getActiveSong().get(trackNumber));
            if (lineNumber != null && displayContext.getActiveTrack().size() > lineNumber) {
                displayContext.setActiveLine(displayContext.getActiveTrack().get(lineNumber));
            }
        }
    }

    private void restoreColors() {
        for (int i = 0; i < displayContext.getActiveSong().size() && i < colors.size(); ++i) {
            displayContext.getActiveSong().getTrack(i).setColor(colors.get(i));
        }
    }

    @Override
    public KarediActions handles() {
        return RELOAD;
    }
}
