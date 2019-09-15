package main.java.com.github.nianna.karedi.context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import main.java.com.github.nianna.karedi.parser.element.EndOfSongElement;
import main.java.com.github.nianna.karedi.parser.element.LineBreakElement;
import main.java.com.github.nianna.karedi.parser.element.NoteElement;
import main.java.com.github.nianna.karedi.parser.element.NoteElement.Type;
import main.java.com.github.nianna.karedi.parser.element.TagElement;
import main.java.com.github.nianna.karedi.parser.element.TrackElement;
import main.java.com.github.nianna.karedi.parser.element.VisitableSongElement;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.song.tag.Tag;
import org.springframework.stereotype.Component;

@Component
public class SongDisassembler {

	public List<VisitableSongElement> disassemble(Song song) {
		return disassemble(song.getTags(), song.getTracks());
	}

	public List<VisitableSongElement> disassemble(List<Tag> tags, List<SongTrack> tracks) {
		List<VisitableSongElement> elts = disassembleTags(tags);
		elts.addAll(disassembleTracks(tracks));
		elts.add(new EndOfSongElement());
		return elts;
	}

	public List<VisitableSongElement> disassembleTags(List<Tag> tags) {
		return tags.stream().map(this::disassemble).collect(Collectors.toList());
	}

	public List<VisitableSongElement> disassembleTracks(List<SongTrack> tracks) {
		List<VisitableSongElement> elts = new ArrayList<>();
		int trackNumber = 1;
		for (SongTrack track : tracks) {
			if (trackNumber > 1 || tracks.size() > 1) {
				elts.add(new TrackElement(trackNumber++));
			}
			elts.addAll(disassemble(track));
		}
		return elts;
	}

	public List<VisitableSongElement> disassembleLines(List<SongLine> lines) {
		return lines.stream().flatMap(line -> {
			List<VisitableSongElement> elts = disassemble(line);
			line.getPrevious().ifPresent(prevLine -> {
				elts.add(0, new LineBreakElement(line.getLineBreak()));
			});
			return elts.stream();
		}).collect(Collectors.toList());
	}

	public List<VisitableSongElement> disassemble(SongTrack track) {
		return disassembleLines(track.getLines());
	}

	public List<VisitableSongElement> disassemble(SongLine line) {
		return line.getNotes().stream().map(this::disassemble).collect(Collectors.toList());
	}

	public NoteElement disassemble(Note note) {
		String lyrics = note.getLyrics();
		if (note.isFirstInLine()) {
			lyrics = lyrics.trim();
		}
		return new NoteElement(noteElementType(note.getType()), note.getStart(), note.getLength(),
				note.getTone(), lyrics);
	}

	private TagElement disassemble(Tag tag) {
		return new TagElement(tag.getKey(), tag.getValue());
	}

	private Type noteElementType(Note.Type type) {
		switch (type) {
		case GOLDEN:
			return Type.GOLDEN;
		case FREESTYLE:
			return Type.FREESTYLE;
		case RAP:
			return Type.RAP;
		case GOLDEN_RAP:
			return Type.GOLDEN_RAP;
		default:
			return Type.NORMAL;
		}
	}
}
