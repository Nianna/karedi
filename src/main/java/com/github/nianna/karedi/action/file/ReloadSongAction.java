package main.java.com.github.nianna.karedi.action.file;

import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import main.java.com.github.nianna.karedi.KarediApp;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.song.SongTrack;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static main.java.com.github.nianna.karedi.action.KarediActions.RELOAD;

@Component
class ReloadSongAction extends NewKarediAction {
    private final AppContext appContext;
    private final SongContext songContext;
    private Integer trackNumber;
    private Integer lineNumber;
    private List<Color> colors;

    ReloadSongAction(AppContext appContext, SongContext songContext) {
        this.appContext = appContext;
        this.songContext = songContext;
        setDisabledCondition(appContext.activeFileIsNullProperty().or(this.songContext.activeSongIsNullProperty()));
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (KarediApp.getInstance().saveChangesIfUserWantsTo()) {
            backupTrackAndLine();
            backupColors();
            appContext.loadSongFile(appContext.getActiveFile(), false);

            if (songContext.getActiveSong() != null) {
                restoreTrackAndLine();
                restoreColors();
            }
        }
    }

    private void backupTrackAndLine() {
        trackNumber = null;
        lineNumber = null;
        if (songContext.getActiveTrack() != null) {
            trackNumber = songContext.getActiveSong().indexOf(songContext.getActiveTrack());
            if (songContext.getActiveLine() != null) {
                lineNumber = songContext.getActiveTrack().indexOf(songContext.getActiveLine());
            }
        }
    }

    private void backupColors() {
        colors = songContext.getActiveSong().getTracks().stream().map(SongTrack::getColor)
                .collect(Collectors.toList());
    }

    private void restoreTrackAndLine() {
        if (trackNumber != null && songContext.getActiveSong().size() > trackNumber) {
            songContext.setActiveTrack(songContext.getActiveSong().get(trackNumber));
            if (lineNumber != null && songContext.getActiveTrack().size() > lineNumber) {
                songContext.setActiveLine(songContext.getActiveTrack().get(lineNumber));
            }
        }
    }

    private void restoreColors() {
        for (int i = 0; i < songContext.getActiveSong().size() && i < colors.size(); ++i) {
            songContext.getActiveSong().getTrack(i).setColor(colors.get(i));
        }
    }

    @Override
    public KarediActions handles() {
        return RELOAD;
    }
}
