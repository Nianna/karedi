package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.region.Direction;

class MoveVisibleAreaAction extends NewKarediAction {

    private final KarediActions handledAction;
    private final Direction direction;
    private final int by;
    private final DisplayContext displayContext;

    MoveVisibleAreaAction(KarediActions handledAction, Direction direction, int by, DisplayContext displayContext) {
        this.handledAction = handledAction;
        this.direction = direction;
        this.by = by;
        this.displayContext = displayContext;
    }

    @Override
    protected void onAction(ActionEvent event) {
        displayContext.moveVisibleArea(direction, by);
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}
