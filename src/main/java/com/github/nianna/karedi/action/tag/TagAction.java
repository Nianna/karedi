package main.java.com.github.nianna.karedi.action.tag;

import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.DisplayContext;

abstract class TagAction extends KarediAction {

    protected final DisplayContext displayContext;

    TagAction(DisplayContext displayContext) {
        this.displayContext = displayContext;
        setDisabledCondition(this.displayContext.activeSongIsNullProperty());
    }
}
