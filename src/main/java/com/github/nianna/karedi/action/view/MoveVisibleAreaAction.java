package main.java.com.github.nianna.karedi.action.view;

import javafx.event.ActionEvent;
import main.java.com.github.nianna.karedi.action.KarediActions;
import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.region.Direction;

class MoveVisibleAreaAction extends NewKarediAction {

    private final KarediActions handledAction;
    private final Direction direction;
    private final int by;
    private final VisibleArea visibleArea;
    private final SongContext songContext;

    MoveVisibleAreaAction(KarediActions handledAction, Direction direction, int by, VisibleArea visibleArea, SongContext songContext) {
        this.handledAction = handledAction;
        this.direction = direction;
        this.by = by;
        this.visibleArea = visibleArea;
        this.songContext = songContext;
    }

    @Override
    protected void onAction(ActionEvent event) {
        visibleArea.move(direction, by);
        songContext.setActiveLine(null);
    }

    @Override
    public KarediActions handles() {
        return handledAction;
    }
}
