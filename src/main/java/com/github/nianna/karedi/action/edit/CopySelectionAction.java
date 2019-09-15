package main.java.com.github.nianna.karedi.action.edit;

import javafx.event.ActionEvent;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.context.SongDisassembler;
import main.java.com.github.nianna.karedi.parser.Unparser;
import main.java.com.github.nianna.karedi.parser.element.LineBreakElement;
import main.java.com.github.nianna.karedi.parser.element.VisitableSongElement;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.SongLine;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static main.java.com.github.nianna.karedi.action.KarediActions.COPY;

@Component
class CopySelectionAction extends NewKarediAction {

    private final NoteSelection noteSelection;
    private final Unparser unparser;
    private final SongDisassembler songDisassembler;
    private boolean includeLineBreak;

    CopySelectionAction(NoteSelection noteSelection, Unparser unparser, SongDisassembler songDisassembler) {
        this.noteSelection = noteSelection;
        this.unparser = unparser;
        this.songDisassembler = songDisassembler;
        setDisabledCondition(this.noteSelection.isEmptyProperty());
    }

    @Override
    protected void onAction(ActionEvent event) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        includeLineBreak = false;
        String result = noteSelection.get().stream()
                .collect(
                        Collectors.groupingBy(Note::getLine, TreeMap::new, Collectors.toList()))
                .entrySet().stream().flatMap(this::disassembleLinePart).map(unparser::unparse)
                .collect(Collectors.joining(System.lineSeparator()));

        content.putString(result);
        clipboard.setContent(content);
    }

    private Stream<VisitableSongElement> disassembleLinePart(
            Map.Entry<SongLine, List<Note>> entry) {
        List<VisitableSongElement> list = entry.getValue().stream()
                .map(songDisassembler::disassemble).collect(Collectors.toList());
        if (includeLineBreak) {
            list.add(0, new LineBreakElement(entry.getKey().getLineBreak()));
        } else {
            includeLineBreak = true;
        }
        return list.stream();
    }

    @Override
    public KarediActions handles() {
        return COPY;
    }
}
