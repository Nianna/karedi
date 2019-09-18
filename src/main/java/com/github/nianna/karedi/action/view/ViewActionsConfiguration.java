package main.java.com.github.nianna.karedi.action.view;

import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.SongContext;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.region.Direction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static main.java.com.github.nianna.karedi.action.KarediActions.*;

@Configuration
class ViewActionsConfiguration {

    @Bean
    public NewKarediAction fitToVisibleAction(SongContext songContext) {
        return new FitToVisibleAction(FIT_TO_VISIBLE, true, true, songContext);
    }

    @Bean
    public NewKarediAction fitVerticallyToVisibleAction(SongContext songContext) {
        return new FitToVisibleAction(FIT_VERTICALLY, true, false, songContext);
    }

    @Bean
    public NewKarediAction fitHorizontallyToVisibleAction(SongContext songContext) {
        return new FitToVisibleAction(FIT_HORIZONTALLY, false, true, songContext);
    }

    @Bean
    public NewKarediAction moveVisibleAreaLeftAction(VisibleArea visibleArea, SongContext songContext) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_LEFT, Direction.LEFT, 1, songContext);
    }

    @Bean
    public NewKarediAction moveVisibleAreaRightAction(VisibleArea visibleArea, SongContext songContext) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_RIGHT, Direction.RIGHT, 1, songContext);
    }

    @Bean
    public NewKarediAction moveVisibleAreaUpAction(VisibleArea visibleArea, SongContext songContext) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_UP, Direction.UP, 1, songContext);
    }

    @Bean
    public NewKarediAction moveVisibleAreaDownAction(VisibleArea visibleArea, SongContext songContext) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_DOWN, Direction.DOWN, 1, songContext);
    }
}
