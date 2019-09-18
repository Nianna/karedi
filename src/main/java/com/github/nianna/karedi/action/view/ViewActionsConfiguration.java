package main.java.com.github.nianna.karedi.action.view;

import main.java.com.github.nianna.karedi.action.KarediAction;
import main.java.com.github.nianna.karedi.context.DisplayContext;
import main.java.com.github.nianna.karedi.context.VisibleArea;
import main.java.com.github.nianna.karedi.region.Direction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static main.java.com.github.nianna.karedi.action.KarediActions.*;

@Configuration
class ViewActionsConfiguration {

    @Bean
    public KarediAction fitToVisibleAction(DisplayContext displayContext) {
        return new FitToVisibleAction(FIT_TO_VISIBLE, true, true, displayContext);
    }

    @Bean
    public KarediAction fitVerticallyToVisibleAction(DisplayContext displayContext) {
        return new FitToVisibleAction(FIT_VERTICALLY, true, false, displayContext);
    }

    @Bean
    public KarediAction fitHorizontallyToVisibleAction(DisplayContext displayContext) {
        return new FitToVisibleAction(FIT_HORIZONTALLY, false, true, displayContext);
    }

    @Bean
    public KarediAction moveVisibleAreaLeftAction(VisibleArea visibleArea, DisplayContext displayContext) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_LEFT, Direction.LEFT, 1, displayContext);
    }

    @Bean
    public KarediAction moveVisibleAreaRightAction(VisibleArea visibleArea, DisplayContext displayContext) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_RIGHT, Direction.RIGHT, 1, displayContext);
    }

    @Bean
    public KarediAction moveVisibleAreaUpAction(VisibleArea visibleArea, DisplayContext displayContext) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_UP, Direction.UP, 1, displayContext);
    }

    @Bean
    public KarediAction moveVisibleAreaDownAction(VisibleArea visibleArea, DisplayContext displayContext) {
        return new MoveVisibleAreaAction(MOVE_VISIBLE_AREA_DOWN, Direction.DOWN, 1, displayContext);
    }
}
