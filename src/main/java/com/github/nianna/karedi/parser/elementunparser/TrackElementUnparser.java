package main.java.com.github.nianna.karedi.parser.elementunparser;

import main.java.com.github.nianna.karedi.parser.element.TrackElement;

public class TrackElementUnparser extends SongElementUnparser {

	@Override
	public void visit(TrackElement element) {
		result = Utils.join(" ", "P", Utils.integerToString(element.getNumber()));
	}

}
