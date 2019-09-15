package main.java.com.github.nianna.karedi.action.tag;

import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import main.java.com.github.nianna.karedi.context.SongContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static main.java.com.github.nianna.karedi.action.KarediActions.*;
import static main.java.com.github.nianna.karedi.song.tag.TagKey.*;

@Configuration
class TagActionsConfiguration {

    @Bean
    public NewKarediAction doubleBpmAction(SongContext songContext, AppContext appContext, CommandExecutor commandExecutor) {
        return new EditBpmAction(MULTIPLY_BPM_BY_TWO, 2, songContext, commandExecutor);
    }

    @Bean
    public NewKarediAction divideBpmByTwoAction(SongContext songContext, AppContext appContext, CommandExecutor commandExecutor) {
        return new EditBpmAction(DIVIDE_BPM_BY_TWO, 0.5, songContext, commandExecutor);
    }

    @Bean
    public NewKarediAction editBpmAction(SongContext songContext, CommandExecutor commandExecutor) {
        return new EditBpmAction(EDIT_BPM, songContext, commandExecutor);
    }

    @Bean
    public NewKarediAction setStartTagAction(SongContext songContext, SongPlayer songPlayer, CommandExecutor commandExecutor) {
        return new SetTagValueFromMarkerPositionAction(SET_START_TAG, START, songContext, songPlayer, commandExecutor);
    }

    @Bean
    public NewKarediAction setEndTagAction(SongContext songContext, SongPlayer songPlayer,  CommandExecutor commandExecutor) {
        return new SetTagValueFromMarkerPositionAction(SET_END_TAG, END, songContext, songPlayer, commandExecutor);
    }

    @Bean
    public NewKarediAction setGapTagAction(SongContext songContext, SongPlayer songPlayer,  CommandExecutor commandExecutor) {
        return new SetTagValueFromMarkerPositionAction(SET_GAP_TAG, GAP, songContext, songPlayer, commandExecutor);
    }

    @Bean
    public NewKarediAction setMedleyFromSelectionAction(NoteSelection noteSelection, SongContext songContext, AppContext appContext, CommandExecutor commandExecutor) {
        return new SetMedleyFromSelectionAction(MEDLEY_FROM_SELECTION, true, true, noteSelection, commandExecutor, songContext, appContext);
    }

    @Bean
    public NewKarediAction setMedleyStartFromSelectionAction(NoteSelection noteSelection, SongContext songContext, AppContext appContext, CommandExecutor commandExecutor) {
        return new SetMedleyFromSelectionAction(MEDLEY_SET_START, true, false, noteSelection, commandExecutor, songContext, appContext);
    }

    @Bean
    public NewKarediAction setMedleyEndFromSelectionAction(NoteSelection noteSelection, SongContext songContext, AppContext appContext, CommandExecutor commandExecutor) {
        return new SetMedleyFromSelectionAction(MEDLEY_SET_END, false, true, noteSelection, commandExecutor, songContext, appContext);
    }
}
