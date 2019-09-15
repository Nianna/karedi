package main.java.com.github.nianna.karedi.action.tag;

import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.SongState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static main.java.com.github.nianna.karedi.action.KarediActions.*;
import static main.java.com.github.nianna.karedi.song.tag.TagKey.*;

@Configuration
class TagActionsConfiguration {

    @Bean
    public NewKarediAction doubleBpmAction(SongState songState, AppContext appContext, CommandExecutor commandExecutor) {
        return new EditBpmAction(MULTIPLY_BPM_BY_TWO, 2, songState, appContext, commandExecutor);
    }

    @Bean
    public NewKarediAction divideBpmByTwoAction(SongState songState, AppContext appContext, CommandExecutor commandExecutor) {
        return new EditBpmAction(DIVIDE_BPM_BY_TWO, 0.5, songState, appContext, commandExecutor);
    }

    @Bean
    public NewKarediAction editBpmAction(SongState songState, AppContext appContext, CommandExecutor commandExecutor) {
        return new EditBpmAction(EDIT_BPM, songState, appContext, commandExecutor);
    }

    @Bean
    public NewKarediAction setStartTagAction(SongState songState, SongPlayer songPlayer, AppContext appContext, CommandExecutor commandExecutor) {
        return new SetTagValueFromMarkerPositionAction(SET_START_TAG, START, songState, songPlayer, commandExecutor, appContext);
    }

    @Bean
    public NewKarediAction setEndTagAction(SongState songState, SongPlayer songPlayer, AppContext appContext, CommandExecutor commandExecutor) {
        return new SetTagValueFromMarkerPositionAction(SET_END_TAG, END, songState, songPlayer, commandExecutor, appContext);
    }

    @Bean
    public NewKarediAction setGapTagAction(SongState songState, SongPlayer songPlayer, AppContext appContext, CommandExecutor commandExecutor) {
        return new SetTagValueFromMarkerPositionAction(SET_GAP_TAG, GAP, songState, songPlayer, commandExecutor, appContext);
    }

    @Bean
    public NewKarediAction setMedleyFromSelectionAction(NoteSelection noteSelection, AppContext appContext, CommandExecutor commandExecutor) {
        return new SetMedleyFromSelectionAction(MEDLEY_FROM_SELECTION, true, true, noteSelection, commandExecutor, appContext);
    }

    @Bean
    public NewKarediAction setMedleyStartFromSelectionAction(NoteSelection noteSelection, AppContext appContext, CommandExecutor commandExecutor) {
        return new SetMedleyFromSelectionAction(MEDLEY_SET_START, true, false, noteSelection, commandExecutor, appContext);
    }

    @Bean
    public NewKarediAction setMedleyEndFromSelectionAction(NoteSelection noteSelection, AppContext appContext, CommandExecutor commandExecutor) {
        return new SetMedleyFromSelectionAction(MEDLEY_SET_END, false, true, noteSelection, commandExecutor, appContext);
    }
}
