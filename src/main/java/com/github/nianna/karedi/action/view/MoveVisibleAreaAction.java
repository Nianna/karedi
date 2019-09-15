package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.region.Direction;

class MoveVisibleAreaAction extends NewKarediAction {

    private final KarediActions handledAction;
    private final Direction direction;
    private final int by;
    private final VisibleArea visibleArea;
    private final SongState songState;

    MoveVisibleAreaAction(KarediActions handledAction, Direction direction, int by, VisibleArea visibleArea, SongState songState) {
        this.handledAction = handledAction;
        this.direction = direction;
        this.by = by;
        this.visibleArea = visibleArea;
        this.songState = songState;
    }

    @Override
    protected void onAction(ActionEvent event) {
        visibleArea.move(direction, by);
        songState.setActiveLine(null);
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}
