package main.java.com.github.nianna.karedi.action.tag;

import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.DisplayContext;

abstract class TagAction extends NewKarediAction {

    protected final DisplayContext displayContext;

    TagAction(DisplayContext displayContext) {
        this.displayContext = displayContext;
        setDisabledCondition(this.displayContext.activeSongIsNullProperty());
    }
}
