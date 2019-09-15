package main.java.com.github.nianna.karedi.action.tag;

import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.SongState;

abstract class TagAction extends NewKarediAction {

    TagAction(SongState songState) {
        setDisabledCondition(songState.activeSongIsNullProperty());
    }
}
