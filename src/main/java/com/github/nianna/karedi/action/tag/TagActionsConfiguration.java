package main.java.com.github.nianna.karedi.action.tag;

import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.SongPlayer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static main.java.com.github.nianna.karedi.action.KarediActions.*;
import static main.java.com.github.nianna.karedi.song.tag.TagKey.*;

@Configuration
class TagActionsConfiguration {

    @Bean
    public NewKarediAction doubleBpmAction(DisplayContext displayContext, AppContext appContext, CommandExecutor commandExecutor) {
        return new EditBpmAction(MULTIPLY_BPM_BY_TWO, 2, displayContext, commandExecutor);
    }

    @Bean
    public NewKarediAction divideBpmByTwoAction(DisplayContext displayContext, AppContext appContext, CommandExecutor commandExecutor) {
        return new EditBpmAction(DIVIDE_BPM_BY_TWO, 0.5, displayContext, commandExecutor);
    }

    @Bean
    public NewKarediAction editBpmAction(DisplayContext displayContext, CommandExecutor commandExecutor) {
        return new EditBpmAction(EDIT_BPM, displayContext, commandExecutor);
    }

    @Bean
    public NewKarediAction setStartTagAction(DisplayContext displayContext, SongPlayer songPlayer, CommandExecutor commandExecutor) {
        return new SetTagValueFromMarkerPositionAction(SET_START_TAG, START, displayContext, songPlayer, commandExecutor);
    }

    @Bean
    public NewKarediAction setEndTagAction(DisplayContext displayContext, SongPlayer songPlayer, CommandExecutor commandExecutor) {
        return new SetTagValueFromMarkerPositionAction(SET_END_TAG, END, displayContext, songPlayer, commandExecutor);
    }

    @Bean
    public NewKarediAction setGapTagAction(DisplayContext displayContext, SongPlayer songPlayer, CommandExecutor commandExecutor) {
        return new SetTagValueFromMarkerPositionAction(SET_GAP_TAG, GAP, displayContext, songPlayer, commandExecutor);
    }

    @Bean
    public NewKarediAction setMedleyFromSelectionAction(NoteSelection noteSelection, DisplayContext displayContext, AppContext appContext, CommandExecutor commandExecutor, NoteSelection selection) {
        return new SetMedleyFromSelectionAction(MEDLEY_FROM_SELECTION, true, true, noteSelection, commandExecutor, displayContext, selection);
    }

    @Bean
    public NewKarediAction setMedleyStartFromSelectionAction(NoteSelection noteSelection, DisplayContext displayContext, AppContext appContext, CommandExecutor commandExecutor, NoteSelection selection) {
        return new SetMedleyFromSelectionAction(MEDLEY_SET_START, true, false, noteSelection, commandExecutor, displayContext, selection);
    }

    @Bean
    public NewKarediAction setMedleyEndFromSelectionAction(NoteSelection noteSelection, DisplayContext displayContext, AppContext appContext, CommandExecutor commandExecutor, NoteSelection selection) {
        return new SetMedleyFromSelectionAction(MEDLEY_SET_END, false, true, noteSelection, commandExecutor, displayContext, selection);
    }
}
