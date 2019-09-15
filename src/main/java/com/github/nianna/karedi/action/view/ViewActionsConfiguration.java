package main.java.com.github.nianna.karedi.action.view;

import main.java.com.github.nianna.karedi.action.NewKarediAction;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.SongState;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.region.Direction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static main.java.com.github.nianna.karedi.action.KarediActions.*;

@Configuration
class ViewActionsConfiguration {

    @Bean
    public NewKarediAction fitToVisibleAction(SongState songState, VisibleArea visibleArea, AppContext appContext) {
        return new FitToVisibleAction(FIT_TO_VISIBLE, true, true, songState, visibleArea, appContext);
    }

    @Bean
    public NewKarediAction fitVerticallyToVisibleAction(SongState songState, VisibleArea visibleArea, AppContext appContext) {
        return new FitToVisibleAction(FIT_VERTICALLY, true, false, songState, visibleArea, appContext);
    }

    @Bean
    public NewKarediAction fitHorizontallyToVisibleAction(SongState songState, VisibleArea visibleArea, AppContext appContext) {
        return new FitToVisibleAction(FIT_HORIZONTALLY, false, true, songState, visibleArea, appContext);
    }

    @Bean
    public NewKarediAction moveVisibleAreaLeftAction(VisibleArea visibleArea, SongState songState) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_LEFT, Direction.LEFT, 1, visibleArea, songState);
    }

    @Bean
    public NewKarediAction moveVisibleAreaRightAction(VisibleArea visibleArea, SongState songState) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_RIGHT, Direction.RIGHT, 1, visibleArea, songState);
    }

    @Bean
    public NewKarediAction moveVisibleAreaUpAction(VisibleArea visibleArea, SongState songState) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_UP, Direction.UP, 1, visibleArea, songState);
    }

    @Bean
    public NewKarediAction moveVisibleAreaDownAction(VisibleArea visibleArea, SongState songState) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_DOWN, Direction.DOWN, 1, visibleArea, songState);
    }
}
