package main.java.com.github.nianna.karedi.action.edit;

import javafx.scene.input.Clipboard;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.BasicSongBuilder;
import main.java.com.github.nianna.karedi.context.SongBuilder;
import main.java.com.github.nianna.karedi.parser.Parser;
import main.java.com.github.nianna.karedi.parser.element.InvalidSongElementException;
import main.java.com.github.nianna.karedi.song.Song;

import java.util.Arrays;

abstract class ClipboardAction extends NewKarediAction {

    private final Parser parser;

    protected ClipboardAction(Parser parser) {
        this.parser = parser;
    }

    protected Song buildSong(String[] lines) {
        SongBuilder builder = new BasicSongBuilder();
        Arrays.asList(lines).forEach(line -> {
            try {
                builder.buildPart(parser.parse(line));
            } catch (InvalidSongElementException e) {
                // ignore
            }
        });
        return builder.getResult();
    }

    protected String[] getLinesFromClipboard() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.getString() == null) {
            return new String[0];
        }
        return clipboard.getString().split("\\R");
    }
}
