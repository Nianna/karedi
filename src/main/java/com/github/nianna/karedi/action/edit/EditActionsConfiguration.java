package main.java.com.github.nianna.karedi.action.edit;

import main.java.com.github.nianna.karedi.action.ActionManager;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.MergeNotesCommand;
import main.java.com.github.nianna.karedi.command.RollLyricsLeftCommand;
import main.java.com.github.nianna.karedi.command.RollLyricsRightCommand;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.parser.Parser;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.song.Note;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static main.java.com.github.nianna.karedi.action.KarediActions.*;

@Configuration
class EditActionsConfiguration {

    @Bean
    public NewKarediAction moveSelectionUpAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MoveSelectionAction(MOVE_SELECTION_UP, Direction.UP, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction moveSelectionDownAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MoveSelectionAction(MOVE_SELECTION_DOWN, Direction.DOWN, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction moveSelectionLeftAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MoveSelectionAction(MOVE_SELECTION_LEFT, Direction.LEFT, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction moveSelectionRightAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MoveSelectionAction(MOVE_SELECTION_RIGHT, Direction.RIGHT, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction shortenSelectionLeftAction(NoteSelection noteSelection, CommandExecutor commandExecutor, AppContext appContext) {
        return new ResizeAction(SHORTEN_LEFT_SIDE, Direction.LEFT, -1, noteSelection, commandExecutor, appContext);
    }

    @Bean
    public NewKarediAction shortenSelectionRightAction(NoteSelection noteSelection, CommandExecutor commandExecutor, AppContext appContext) {
        return new ResizeAction(SHORTEN_RIGHT_SIDE, Direction.RIGHT, -1, noteSelection, commandExecutor, appContext);
    }

    @Bean
    public NewKarediAction lengthenSelectionLeftAction(NoteSelection noteSelection, CommandExecutor commandExecutor, AppContext appContext) {
        return new ResizeAction(LENGTHEN_LEFT_SIDE, Direction.LEFT, 1, noteSelection, commandExecutor, appContext);
    }

    @Bean
    public NewKarediAction lengthenSelectionRightAction(NoteSelection noteSelection, CommandExecutor commandExecutor, AppContext appContext) {
        return new ResizeAction(LENGTHEN_RIGHT_SIDE, Direction.RIGHT, 1, noteSelection, commandExecutor, appContext);
    }

    @Bean
    public DeleteSelectionAction deleteSelectionAction(NoteSelection noteSelection, CommandExecutor commandExecutor, VisibleArea visibleArea, SongState songState) {
        return new DeleteSelectionAction(DELETE_SELECTION, true, noteSelection, commandExecutor, visibleArea, songState);
    }

    @Bean
    public DeleteSelectionAction deleteSelectionHardAction(NoteSelection noteSelection, CommandExecutor commandExecutor, VisibleArea visibleArea, SongState songState) {
        return new DeleteSelectionAction(DELETE_SELECTION_HARD, false, noteSelection, commandExecutor, visibleArea, songState);
    }

    @Bean
    public NewKarediAction cutSelectionAction(NoteSelection noteSelection, CommandExecutor commandExecutor, ActionManager actionManager, DeleteSelectionAction deleteSelectionHardAction) {
        return new CutSelectionAction(noteSelection, commandExecutor, actionManager, deleteSelectionHardAction);
    }

    @Bean
    public NewKarediAction changeSelectionToFreestyleAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new ChangeSelectionTypeAction(MARK_AS_FREESTYLE, Note.Type.FREESTYLE, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction changeSelectionToGoldenAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new ChangeSelectionTypeAction(MARK_AS_GOLDEN, Note.Type.GOLDEN, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction changeSelectionToRapAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new ChangeSelectionTypeAction(MARK_AS_RAP, Note.Type.RAP, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction rollLyricsLeftAction(NoteSelection noteSelection, SongState songState, CommandExecutor commandExecutor) {
        return new RollLyricsAction(ROLL_LYRICS_LEFT, noteSelection, songState, commandExecutor, RollLyricsLeftCommand::new);
    }

    @Bean
    public NewKarediAction rollLyricsRightAction(NoteSelection noteSelection, SongState songState, CommandExecutor commandExecutor) {
        return new RollLyricsAction(ROLL_LYRICS_RIGHT, noteSelection, songState, commandExecutor, RollLyricsRightCommand::new);
    }

    @Bean
    public NewKarediAction setTonesAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_TONES, MergeNotesCommand.MergeMode.TONES, parser, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction setSynchroAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_SYNCHRO, MergeNotesCommand.MergeMode.SYNCHRO, parser, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction setLyricsAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_LYRICS, MergeNotesCommand.MergeMode.LYRICS, parser, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction setTonesAndSynchroAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_TONES_AND_SYNCHRO, MergeNotesCommand.MergeMode.TONES_SYNCHRO, parser, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction setTonesAndLyricsAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_TONES_AND_LYRICS, MergeNotesCommand.MergeMode.TONES_LYRICS, parser, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction seSynchroAndLyricsAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_SYNCHRO_AND_LYRICS, MergeNotesCommand.MergeMode.SYNCHRO_LYRICS, parser, noteSelection, commandExecutor);
    }

    @Bean
    public NewKarediAction setTonesAndSynchroAndLyricsAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_TONES_SYNCHRO_AND_LYRICS, MergeNotesCommand.MergeMode.TONES_SYNCHRO_LYRICS, parser, noteSelection, commandExecutor);
    }
}
