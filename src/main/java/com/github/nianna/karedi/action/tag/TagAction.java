package main.java.com.github.nianna.karedi.action.tag;

import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.SongContext;

abstract class TagAction extends NewKarediAction {

    protected final SongContext songContext;

    TagAction(SongContext songContext) {
        this.songContext = songContext;
        setDisabledCondition(this.songContext.activeSongIsNullProperty());
    }
}
