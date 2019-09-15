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
    public NewKarediAction fitToVisibleAction(SongContext songContext, VisibleArea visibleArea) {
        return new FitToVisibleAction(FIT_TO_VISIBLE, true, true, songContext, visibleArea);
    }

    @Bean
    public NewKarediAction fitVerticallyToVisibleAction(SongContext songContext, VisibleArea visibleArea) {
        return new FitToVisibleAction(FIT_VERTICALLY, true, false, songContext, visibleArea);
    }

    @Bean
    public NewKarediAction fitHorizontallyToVisibleAction(SongContext songContext, VisibleArea visibleArea) {
        return new FitToVisibleAction(FIT_HORIZONTALLY, false, true, songContext, visibleArea);
    }

    @Bean
    public NewKarediAction moveVisibleAreaLeftAction(VisibleArea visibleArea, SongContext songContext) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_LEFT, Direction.LEFT, 1, visibleArea, songContext);
    }

    @Bean
    public NewKarediAction moveVisibleAreaRightAction(VisibleArea visibleArea, SongContext songContext) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_RIGHT, Direction.RIGHT, 1, visibleArea, songContext);
    }

    @Bean
    public NewKarediAction moveVisibleAreaUpAction(VisibleArea visibleArea, SongContext songContext) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_UP, Direction.UP, 1, visibleArea, songContext);
    }

    @Bean
    public NewKarediAction moveVisibleAreaDownAction(VisibleArea visibleArea, SongContext songContext) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_DOWN, Direction.DOWN, 1, visibleArea, songContext);
    }
}
