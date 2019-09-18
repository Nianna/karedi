package main.java.com.github.nianna.karedi.action.edit;

import main.java.com.github.nianna.karedi.action.ActionManager;
import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.command.CommandExecutor;
import main.java.com.github.nianna.karedi.command.MergeNotesCommand;
import main.java.com.github.nianna.karedi.command.RollLyricsLeftCommand;
import main.java.com.github.nianna.karedi.command.RollLyricsRightCommand;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.parser.Parser;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.song.Note;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static main.java.com.github.nianna.karedi.action.KarediActions.*;

@Configuration
class EditActionsConfiguration {

    @Bean
    public KarediAction moveSelectionUpAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MoveSelectionAction(MOVE_SELECTION_UP, Direction.UP, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction moveSelectionDownAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MoveSelectionAction(MOVE_SELECTION_DOWN, Direction.DOWN, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction moveSelectionLeftAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MoveSelectionAction(MOVE_SELECTION_LEFT, Direction.LEFT, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction moveSelectionRightAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MoveSelectionAction(MOVE_SELECTION_RIGHT, Direction.RIGHT, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction shortenSelectionLeftAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new ResizeAction(SHORTEN_LEFT_SIDE, Direction.LEFT, -1, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction shortenSelectionRightAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new ResizeAction(SHORTEN_RIGHT_SIDE, Direction.RIGHT, -1, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction lengthenSelectionLeftAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new ResizeAction(LENGTHEN_LEFT_SIDE, Direction.LEFT, 1, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction lengthenSelectionRightAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new ResizeAction(LENGTHEN_RIGHT_SIDE, Direction.RIGHT, 1, noteSelection, commandExecutor);
    }

    @Bean
    public DeleteSelectionAction deleteSelectionAction(NoteSelection noteSelection, CommandExecutor commandExecutor, DisplayContext displayContext) {
        return new DeleteSelectionAction(DELETE_SELECTION, true, noteSelection, commandExecutor, displayContext);
    }

    @Bean
    public DeleteSelectionAction deleteSelectionHardAction(NoteSelection noteSelection, CommandExecutor commandExecutor, DisplayContext displayContext) {
        return new DeleteSelectionAction(DELETE_SELECTION_HARD, false, noteSelection, commandExecutor, displayContext);
    }

    @Bean
    public KarediAction cutSelectionAction(NoteSelection noteSelection, CommandExecutor commandExecutor, ActionManager actionManager, DeleteSelectionAction deleteSelectionHardAction) {
        return new CutSelectionAction(noteSelection, commandExecutor, actionManager, deleteSelectionHardAction);
    }

    @Bean
    public KarediAction changeSelectionToFreestyleAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new ChangeSelectionTypeAction(MARK_AS_FREESTYLE, Note.Type.FREESTYLE, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction changeSelectionToGoldenAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new ChangeSelectionTypeAction(MARK_AS_GOLDEN, Note.Type.GOLDEN, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction changeSelectionToRapAction(NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new ChangeSelectionTypeAction(MARK_AS_RAP, Note.Type.RAP, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction rollLyricsLeftAction(NoteSelection noteSelection, DisplayContext displayContext, CommandExecutor commandExecutor) {
        return new RollLyricsAction(ROLL_LYRICS_LEFT, noteSelection, displayContext, commandExecutor, RollLyricsLeftCommand::new);
    }

    @Bean
    public KarediAction rollLyricsRightAction(NoteSelection noteSelection, DisplayContext displayContext, CommandExecutor commandExecutor) {
        return new RollLyricsAction(ROLL_LYRICS_RIGHT, noteSelection, displayContext, commandExecutor, RollLyricsRightCommand::new);
    }

    @Bean
    public KarediAction setTonesAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_TONES, MergeNotesCommand.MergeMode.TONES, parser, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction setSynchroAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_SYNCHRO, MergeNotesCommand.MergeMode.SYNCHRO, parser, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction setLyricsAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_LYRICS, MergeNotesCommand.MergeMode.LYRICS, parser, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction setTonesAndSynchroAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_TONES_AND_SYNCHRO, MergeNotesCommand.MergeMode.TONES_SYNCHRO, parser, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction setTonesAndLyricsAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_TONES_AND_LYRICS, MergeNotesCommand.MergeMode.TONES_LYRICS, parser, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction seSynchroAndLyricsAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_SYNCHRO_AND_LYRICS, MergeNotesCommand.MergeMode.SYNCHRO_LYRICS, parser, noteSelection, commandExecutor);
    }

    @Bean
    public KarediAction setTonesAndSynchroAndLyricsAction(Parser parser, NoteSelection noteSelection, CommandExecutor commandExecutor) {
        return new MergeAction(SET_TONES_SYNCHRO_AND_LYRICS, MergeNotesCommand.MergeMode.TONES_SYNCHRO_LYRICS, parser, noteSelection, commandExecutor);
    }
}
